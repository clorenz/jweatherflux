package de.christophlorenz.jweatherflux.service;

import de.christophlorenz.jweatherflux.forwarder.Forwarder;
import de.christophlorenz.jweatherflux.service.async.ForwarderTask;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ForwarderService {

  private static final Logger LOGGER = LoggerFactory.getLogger(ForwarderService.class);
  private final List<Forwarder> forwarders;
  private final ScheduledExecutorService executor;

  public ForwarderService(List<Forwarder> forwarders) {
    this.forwarders = forwarders.stream().filter(Forwarder::isActive).collect(Collectors.toList());
    executor = Executors.newScheduledThreadPool(forwarders.size());
    LOGGER.info("Active Forwarders " + forwarders.stream().filter(Forwarder::isActive).map(f -> f.getClass().getSimpleName()).collect(Collectors.toList()));
  }

  public void forwardAll(Map<String, String> data) {
    for (Forwarder forwarder : forwarders) {
      ForwarderTask forwarderTask = new ForwarderTask(forwarder, data);
      CompletableFuture.runAsync(forwarderTask).orTimeout(forwarder.getTimeoutInSeconds(), TimeUnit.SECONDS)
              .exceptionally(throwable -> {
                forwarderTask.setInterrupted(true);
                return null;
              });
      LOGGER.debug("Started " + forwarderTask.getThreadName() + " with a timeout of " + forwarder.getTimeoutInSeconds() + " seconds");
    }
  }
}
