package de.christophlorenz.jweatherflux.service.async;

import de.christophlorenz.jweatherflux.forwarder.Forwarder;
import java.util.Map;

public class ForwarderTask implements Runnable {

  private final Forwarder forwarder;
  private final Map<String, String> data;
  private String threadName = null;

  private boolean isInterrupted = false;

  public ForwarderTask(Forwarder forwarder, Map<String, String> data) {
    this.forwarder = forwarder;
    this.data = data;
    threadName = forwarder.getForwarderName();
  }

  @Override
  public void run() {
  Thread t = new ForwarderThread(forwarder, data);
    t.start();

    while ( !isInterrupted) {
      try {
        Thread.sleep(100);
      } catch (InterruptedException ignore) {
        // ignore
      }
    }

    try {
      t.interrupt();
      t.join(100);
    } catch (InterruptedException ignore) {
      // ignore
    }

    t=null;
  }

  public void setInterrupted(boolean interrupted) {
    isInterrupted = interrupted;
  }

  public String getThreadName() {
    return threadName;
  }
}
