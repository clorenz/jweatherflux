package de.christophlorenz.jweatherflux.service;

public class PersistException extends Exception {

  public PersistException(String msg, Exception e) {
    super(msg, e);
  }
}
