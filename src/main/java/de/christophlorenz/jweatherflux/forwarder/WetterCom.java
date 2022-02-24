package de.christophlorenz.jweatherflux.forwarder;

import de.christophlorenz.jweatherflux.config.ForwarderProperties;
import de.christophlorenz.jweatherflux.data.Calculators;
import io.micrometer.core.instrument.MeterRegistry;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import org.apache.http.client.utils.URIBuilder;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
/**
 * Forwarder for wetter.com (wwww.wetterarchiv.de)
 */
public class WetterCom extends AbstractForwarder {

  private final String appName;
  private final String appVersion;

  private static final Logger LOGGER = LoggerFactory.getLogger(WetterCom.class);
  private static final DateTimeFormatter FORMAT_DATEUTC = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
  private static final DateTimeFormatter FORMAT_WETTERCOM = DateTimeFormatter.ofPattern("yyyyMMddHHmm");

  private final HttpForwarderService httpForwarderService;
  private final ForwarderProperties.WetterCom config;

  public WetterCom(ForwarderProperties forwarderProperties, HttpForwarderService httpForwarderService, @Value("${app.name:}") String appName, @Value("${app.version:}") String appVersion, MeterRegistry meterRegistry) {    super(forwarderProperties.getWetterCom() != null ? forwarderProperties.getWetterCom().getInterval() : 0, meterRegistry);

    if (forwarderProperties.getWetterCom() != null) {
      config = forwarderProperties.getWetterCom();
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
  public void forwardToService(Map<String, String> data)
      throws AuthorizationException, TransmitException, InvalidDataException {

    String datum = LocalDateTime.parse(URLDecoder.decode(data.get(FIELD_DATEUTC), StandardCharsets.UTF_8), FORMAT_DATEUTC).format(FORMAT_WETTERCOM);
    float dewpoint = Calculators.calcDewpoint(
        Calculators.farenheitToCelsius(Float.parseFloat(data.get(FIELD_TEMPERATURE_OUTDOOR_FARENHEIT))),
        Float.parseFloat(data.get(FIELD_HUMIDITY_OUTDOOR)));

    URI uri;
    try {
      URIBuilder builder = new URIBuilder(config.getUrl());

      builder.setParameter("id", config.getUserId());
      builder.setParameter("pwd", config.getIdKennwort());
      builder.setParameter("sid", "API50");
      builder.setParameter("ver", appName + " " + appVersion);
      builder.setParameter("dtutc", datum);
      builder.setParameter("hu", data.get(FIELD_HUMIDITY_OUTDOOR));

      builder.setParameter("te", String.format(Locale.ENGLISH, "%.1f",
          Calculators.farenheitToCelsius(
              Float.parseFloat(data.get(FIELD_TEMPERATURE_OUTDOOR_FARENHEIT)))));
      builder.setParameter("pr", String.format(Locale.ENGLISH, "%.1f",
          Calculators.inHgToHpa(Float.parseFloat(data.get(FIELD_BAROMETER_RELATIVE_INTERNAL)))));
      builder.setParameter("dp", String.format(Locale.ENGLISH, "%.1f", dewpoint));
      builder.setParameter("wd", String.format(Locale.ENGLISH, "%.0f",
          Float.parseFloat(data.get(FIELD_WIND_DIRECTION_10MIN_AVG))));
      builder.setParameter("ws", String.format(Locale.ENGLISH, "%.1f",
          Calculators.mphToMs(Float.parseFloat(data.get(FIELD_WIND_SPEED_10MIN_AVG)))));
      builder.setParameter("wg", String.format(Locale.ENGLISH, "%.1f",
          Calculators.mphToMs(Float.parseFloat(data.get(FIELD_WIND_GUST_CURRENT_MPH)))));
      builder.setParameter("pa", String.format(Locale.ENGLISH, "%.2f",
          1000 * Calculators.imperialToMetric(Float.parseFloat(data.get(FIELD_RAIN_HOURLY_INCH)))));
      builder.setParameter("rr", String.format(Locale.ENGLISH, "%.2f",
          1000 * Calculators.imperialToMetric(Float.parseFloat(data.get(FIELD_RAIN_CURRENT_RATE_INCH)))));
      builder.setParameter("uv",
          String.format(Locale.ENGLISH, "%.0f", Float.parseFloat(data.get(FIELD_UV))));
      builder.setParameter("sr",
          String.format(Locale.ENGLISH, "%.2f", Float.parseFloat(data.get(FIELD_SOLAR_RADIATION))));

      uri = builder.build();
    } catch (Exception e) {
      throw new InvalidDataException("Cannot build URI: " + e, e);
    }

    httpForwarderService.transmitByGetRequest(uri, evaluateSuccessFromBody());
  }

  @NotNull
  protected Function<String, Boolean> evaluateSuccessFromBody() {
    return (body) -> body.toLowerCase(Locale.ROOT).matches(".*?\"success\".*");
  }

  @Override
  Logger getLogger() {
    return LOGGER;
  }

  @Override
  public String getForwarderName() {
    return "wetter.com";
  }
}
