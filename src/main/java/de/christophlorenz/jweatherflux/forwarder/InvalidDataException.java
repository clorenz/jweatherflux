package de.christophlorenz.jweatherflux.forwarder;

public class InvalidDataException extends
    Throwable {

  public InvalidDataException(String msg) {
    super(msg);
  }
}
