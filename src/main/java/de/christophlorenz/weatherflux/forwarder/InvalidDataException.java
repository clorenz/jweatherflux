package de.christophlorenz.weatherflux.forwarder;

public class InvalidDataException extends
    Throwable {

  public InvalidDataException(String msg) {
    super(msg);
  }
}
