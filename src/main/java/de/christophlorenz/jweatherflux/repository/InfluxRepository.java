package de.christophlorenz.jweatherflux.repository;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.exceptions.InfluxException;
import de.christophlorenz.jweatherflux.config.InfluxProperties;
import de.christophlorenz.jweatherflux.model.WeatherData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Repository;

@Repository
@EnableAutoConfiguration
public class InfluxRepository {

  private static final Logger LOGGER = LoggerFactory.getLogger(InfluxRepository.class);
  //private static final String url = "http://192.168.1.1:8086";
  //private static final char[] token = "-jsX2Zqu2WmCJ3bgTjjJ7Gd8xWaqD6ic03zsHPRV7Ve5qR356T3dFRMVkDaYlBXotoUxuWawZvQQjkIvaPHGFQ==".toCharArray();
  //private static final String org = "privat";
  //private static final String bucket = "froggit";

  //private static final String url = "http://localhost:8086";
  //private static final String url = "http://192.168.1.1:8086";
  //private static final char[] token = "xjsX2Zqu2WmCJ3bgTjjJ7Gd8xWaqD6ic03zsHPRV7Ve5qR356T3dFRMVkDaYlBXotoUxuWawZvQQjkIvaPHGFQ==".toCharArray();
  //private static final String org = "private";
  //private static final String bucket = "weather";

  private final InfluxProperties influxProperties;

  public InfluxRepository(InfluxProperties influxProperties) {
    this.influxProperties = influxProperties;
  }

  public void persist(WeatherData weatherData) throws InfluxRepositoryException {
    if (influxProperties == null || influxProperties.getUrl()==null || influxProperties.getUrl().isBlank()) {
      LOGGER.warn("No persistence enpdoint defined to persist " + weatherData);
      return;
    }

    try {
      InfluxDBClient influxDBClient = InfluxDBClientFactory.create(
          influxProperties.getUrl(),
          influxProperties.getToken().toCharArray(),
          influxProperties.getOrg(),
          influxProperties.getBucket());
      WriteApiBlocking writeApi = influxDBClient.getWriteApiBlocking();
      writeApi.writePoints(weatherData.getAllAsPoints());
      influxDBClient.close();
      LOGGER.debug("Persisted " + weatherData);
    } catch (InfluxException e) {
      throw new InfluxRepositoryException("Cannot persist data " + weatherData + ": " + e, e);
    }
  }

}
