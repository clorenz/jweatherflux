package de.christophlorenz.jweatherflux.forwarder;

import java.util.Map;

public interface Forwarder {

  boolean isActive();

  void forward(Map<String, String> data);
}
