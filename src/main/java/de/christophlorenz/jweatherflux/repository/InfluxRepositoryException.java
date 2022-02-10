package de.christophlorenz.jweatherflux.repository;

import com.influxdb.exceptions.InfluxException;

public class InfluxRepositoryException extends
    Exception {

  public InfluxRepositoryException(String msg, InfluxException e) {
    super(msg, e);
  }
}
