package de.christophlorenz.jweatherflux.forwarder;

/**
 * This exception is thown, when an error during transmit occurs, e.g. (Socket) Timeout Exception
 * or a HTTP Status 404 or 500.
 *
 * This exception is not thrown, when the request was technically OK, but the
 * receiver reported a data error
 */
public class TransmitException extends Exception {

  public TransmitException(String msg) {
    super(msg);
  }
}
