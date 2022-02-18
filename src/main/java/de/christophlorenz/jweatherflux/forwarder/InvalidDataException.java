package de.christophlorenz.jweatherflux.forwarder;

public class InvalidDataException extends
    Exception {

  public InvalidDataException(String msg) {
    super(msg);
  }

  public InvalidDataException(String msg, Exception e) {
    super(msg, e);
  }
}
