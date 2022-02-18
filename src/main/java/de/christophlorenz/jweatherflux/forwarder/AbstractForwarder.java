package de.christophlorenz.jweatherflux.forwarder;

import java.util.Map;
import org.slf4j.Logger;

/**
 * Encapsulates error handling and intervall sending
 */
public abstract class AbstractForwarder implements Forwarder{

  private int interval;
  private long lastRunMillis = 0;

  protected AbstractForwarder(int interval) {
    this.interval = interval;
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
      forwardToService(data);
      getLogger().debug("Successfully forwarded data to {}", getForwarderName());
    } catch (AuthorizationException e) {
      getLogger().error("Cannot authorize {}: {} ", getForwarderName(), e, e);
    } catch (InvalidDataException e) {
      getLogger().error("Invalid data to transmit to {}: {}", getForwarderName(), e, e);
    } catch (TransmitException e) {
      getLogger().warn("Temporary transmit error to {}: {}", getForwarderName(), e, e);
    } catch (Exception e) {
      getLogger().error("Unexpected error in transmitting data to {}: {}" , getForwarderName(), e, e);
    }

    lastRunMillis = System.currentTimeMillis();
  }

  private boolean isReadyToSend() {
    long deltaInSeconds = (System.currentTimeMillis() - lastRunMillis) / 1000;
    return (deltaInSeconds >= interval);
  }

  abstract void forwardToService(Map<String, String> data) throws AuthorizationException, InvalidDataException, TransmitException;

  abstract Logger getLogger();

  abstract String getForwarderName();
}
