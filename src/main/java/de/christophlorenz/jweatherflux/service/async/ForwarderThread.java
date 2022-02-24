package de.christophlorenz.jweatherflux.service.async;

import de.christophlorenz.jweatherflux.forwarder.Forwarder;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ForwarderThread extends Thread {

  private static final Logger LOGGER = LoggerFactory.getLogger(ForwarderThread.class);
  private final Forwarder forwarder;
  private final Map<String, String> data;

  public ForwarderThread(Forwarder forwarder, Map<String, String> data) {
    super("ForwarderThread-" + forwarder.getForwarderName());
    this.forwarder = forwarder;
    this.data = data;
  }


  @Override
  public void run() {
    try {
      forwarder.forward(data);
    } catch (Exception e) {
      LOGGER.error("Eek: " + e, e);
    }
  }
}
