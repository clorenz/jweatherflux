package de.christophlorenz.jweatherflux.forwarder;

import de.christophlorenz.jweatherflux.config.ForwarderProperties;
import de.christophlorenz.jweatherflux.data.Calculators;
import io.micrometer.core.instrument.MeterRegistry;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class Wunderground extends AbstractForwarder {

  private final String appName;
  private final String appVersion;
  private static final Logger LOGGER = LoggerFactory.getLogger(Wunderground.class);
  private final HttpForwarderService httpForwarderService;
  private final ForwarderProperties.Wunderground config;


  public Wunderground(ForwarderProperties forwarderProperties, HttpForwarderService httpForwarderService, @Value("${app.name:}") String appName, @Value("${app.version:}") String appVersion, MeterRegistry meterRegistry) {
    super(forwarderProperties.getWetterCom() != null ? forwarderProperties.getWetterCom().getInterval() : 0, meterRegistry);
    if (forwarderProperties.getWunderground() != null) {
      config = forwarderProperties.getWunderground();
    } else {
      config = null;
    }
    this.httpForwarderService = httpForwarderService;
    this.appName = appName;
    this.appVersion = appVersion;
  }

  @Override
  public boolean isActive() {
    return config != null && !config.getUrl().isBlank();
  }

  @Override
  public long getTimeoutInSeconds() {
    return config != null ? config.getTimeout() : 1;
  }

  @Override
  public void forwardToService(Map<String, String> data) throws AuthorizationException, TransmitException, InvalidDataException {
    URI uri;
    try {
      URIBuilder builder = new URIBuilder(config.getUrl());
      builder.setParameter("ID", config.getId());
      builder.setParameter("PASSWORD", config.getKey());
      builder.setParameter("dateutc",
          URLDecoder.decode(data.get("dateutc"), StandardCharsets.UTF_8));

      addData(builder, data, "winddir");
      addData(builder, data, "windspeedmph");
      addData(builder, data, "windgustmph");
      addData(builder, data, "humidity");
      addData(builder, data, "tempf");
      addData(builder, data, "dewptf", Calculators.celsiusToFarenheit(Calculators.calcDewpoint(
              Calculators.farenheitToCelsius(Float.parseFloat(data.get("tempf"))),
              Float.parseFloat(data.get("humidity")))));
      addData(builder, data, "rainin", "hourlyrainin");
      addData(builder, data, "dailyrainin");
      addData(builder, data, "baromin", "baromrelin");
      addData(builder, data, "solarradiation");
      addData(builder, data, "UV", "uv");
      addData(builder, data, "softwaretype", "stationtype");
      uri = builder.build();
    } catch (Exception e) {
      throw new InvalidDataException("Cannot build URI: " + e, e);
    }

    httpForwarderService.transmitByGetRequest(uri, evaluateSuccessFromBody());
  }

  private void addData(URIBuilder builder, Map<String, String> data, String wundergroundKey, Float value) {
    builder.setParameter(wundergroundKey, "" + value);
  }

  private void addData(URIBuilder builder, Map<String, String> data, String unmappedKey) throws InvalidDataException {
    addData(builder, data, unmappedKey, unmappedKey);
  }

  private void addData(URIBuilder builder, Map<String, String> data, String wundergroundKey, String key) throws InvalidDataException {
    if (data.get(key)==null) {
      throw new InvalidDataException("No data found for key=" + key);
    }
    builder.setParameter(wundergroundKey, data.get(key));
  }

  @NotNull
  protected Function<String, Boolean> evaluateSuccessFromBody() {
    return (body) -> body.toLowerCase(Locale.ROOT).matches("success");
  }

  @Override
  Logger getLogger() {
    return LOGGER;
  }

  @Override
  public String getForwarderName() {
    return "WeatherUnderground";
  }
}
