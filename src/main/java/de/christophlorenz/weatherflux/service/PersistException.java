package de.christophlorenz.weatherflux.service;

public class PersistException extends Exception {

  public PersistException(String msg, Exception e) {
    super(msg, e);
  }
}
