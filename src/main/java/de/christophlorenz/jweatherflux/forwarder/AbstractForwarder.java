package de.christophlorenz.jweatherflux.forwarder;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Timer;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;

/**
 * Encapsulates error handling and intervall sending
 */
public abstract class AbstractForwarder implements Forwarder{

  private int interval;
  private long lastRunMillis = 0;
  private int successfulForwards=0;
  private int unsuccessfulForwards=0;
  private int skippedForwards=0;
  private Timer httpRequestTimer;

  protected AbstractForwarder(int interval, MeterRegistry meterRegistry) {
    this.interval = interval;
    Gauge.builder("forwarder.success", () -> successfulForwards)
        .tag("forwarder",getForwarderName())
        .register(meterRegistry);
    Gauge.builder("forwarder.error", () -> unsuccessfulForwards)
        .tag("forwarder",getForwarderName())
        .register(meterRegistry);
    Gauge.builder("forwarder.skip", () -> skippedForwards)
        .tag("forwarder",getForwarderName())
        .register(meterRegistry);
    httpRequestTimer = meterRegistry.timer("forwarder.httpRequest", List.of(Tag.of("forwarder", getForwarderName())));
  }

  @Override
  public boolean isActive() {
    return false;
  }

  @Override
  public void forward(Map<String, String> data) {
    if (!isReadyToSend()) {
      long deltaInSeconds = (System.currentTimeMillis() - lastRunMillis) / 1000;
      long remainingSeconds = interval - deltaInSeconds;
      getLogger().debug("Next interval forward to {} not reached. Wait at least {} seconds.", getForwarderName(), remainingSeconds);
      return;
    }

    try {
      getLogger().debug("Forwarding data to {}", getForwarderName());
      long start = System.currentTimeMillis();
      forwardToService(data);
      httpRequestTimer.record((System.currentTimeMillis()-start), TimeUnit.MILLISECONDS);
      successfulForwards++;
      getLogger().debug("Successfully forwarded data to {}", getForwarderName());
    } catch (AuthorizationException e) {
      getLogger().error("Cannot authorize {}: {} ", getForwarderName(), e, e);
      unsuccessfulForwards++;
    } catch (InvalidDataException e) {
      getLogger().error("Invalid data to transmit to {}: {}", getForwarderName(), e, e);
      unsuccessfulForwards++;
    } catch (TransmitException e) {
      getLogger().warn("Temporary transmit error to {}: {}", getForwarderName(), e, e);
      unsuccessfulForwards++;
    } catch (IgnoredConnectionException e) {
      skippedForwards++;
    } catch (Exception e) {
      getLogger().error("Unexpected error in transmitting data to {}: {}" , getForwarderName(), e, e);
      unsuccessfulForwards++;
    }

    lastRunMillis = System.currentTimeMillis();
  }

  private boolean isReadyToSend() {
    long deltaInSeconds = (System.currentTimeMillis() - lastRunMillis) / 1000;
    return (deltaInSeconds >= interval);
  }

  abstract void forwardToService(Map<String, String> data)
      throws AuthorizationException, InvalidDataException, TransmitException, IgnoredConnectionException;

  abstract Logger getLogger();

  public abstract String getForwarderName();
}
