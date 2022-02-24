package de.christophlorenz.jweatherflux.forwarder;

import de.christophlorenz.jweatherflux.config.ForwarderProperties;
import de.christophlorenz.jweatherflux.data.Calculators;
import io.micrometer.core.instrument.MeterRegistry;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Base64;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.client.utils.URIBuilder;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Awekas extends AbstractForwarder {

  private static final Logger LOGGER = LoggerFactory.getLogger(Awekas.class);

  private final String appName;
  private final String appVersion;
  private final HttpForwarderService httpForwarderService;
  private final ForwarderProperties.Awekas config;
  private static final DateTimeFormatter FORMAT_DATEUTC = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
  private static final DateTimeFormatter FORMAT_AWEKAS_DATE = DateTimeFormatter.ofPattern("dd.MM.yyyy");
  private static final DateTimeFormatter FORMAT_AWEKAS_TIME = DateTimeFormatter.ofPattern("HH:mm");

  public Awekas(
      HttpForwarderService httpForwarderService, ForwarderProperties forwarderProperties, @Value("${app.name:}") String appName, @Value("${app.version:}") String appVersion, MeterRegistry meterRegistry) {
    super(forwarderProperties.getAwekas() != null ? forwarderProperties.getAwekas().getInterval() : 0, meterRegistry);

    if (forwarderProperties.getAwekas() != null) {
      config = forwarderProperties.getAwekas();
    } else {
      config = null;
    }
    this.httpForwarderService = httpForwarderService;
    this.appName = appName;
    this.appVersion = appVersion;
  }

  @Override
  void forwardToService(Map<String, String> data)
      throws AuthorizationException, InvalidDataException, TransmitException {
    URI uri;
    try {
      String[] awekasData = new String[25];
      Arrays.fill(awekasData, "");
      awekasData[0] = config.getId();
      awekasData[1] = DigestUtils.md5Hex(config.getKey());
      awekasData[2] = LocalDateTime.parse(
          URLDecoder.decode(data.get(FIELD_DATEUTC), StandardCharsets.UTF_8), FORMAT_DATEUTC).format(FORMAT_AWEKAS_DATE);
      awekasData[3] = LocalDateTime.parse(
          URLDecoder.decode(data.get(FIELD_DATEUTC), StandardCharsets.UTF_8), FORMAT_DATEUTC).format(FORMAT_AWEKAS_TIME);
      awekasData[4] = String.format(Locale.ENGLISH, "%.1f", Calculators.farenheitToCelsius(Float.parseFloat(data.get(FIELD_TEMPERATURE_OUTDOOR_FARENHEIT))));
      awekasData[5] = String.format(Locale.ENGLISH, "%.0f", Float.parseFloat(data.get(FIELD_HUMIDITY_OUTDOOR)));
      awekasData[6] = String.format(Locale.ENGLISH, "%.2f", Calculators.inHgToHpa(Float.parseFloat(data.get(FIELD_BAROMETER_RELATIVE_INTERNAL))));
      awekasData[7] = String.format(Locale.ENGLISH, "%.2f", 1000*Calculators.imperialToMetric(Float.parseFloat(data.get(FIELD_RAIN_DAILY_INCH))));
      awekasData[8] = String.format(Locale.ENGLISH, "%.1f", Calculators.mphToKmh(Float.parseFloat(data.get(FIELD_WIND_SPEED_CURRENT_MPH))));
      awekasData[9] = String.format(Locale.ENGLISH, "%.0f", Float.parseFloat(data.get(FIELD_WIND_DIRECTION_CURRENT)));
      awekasData[13] = "de";
      awekasData[15] = String.format(Locale.ENGLISH, "%.1f", Calculators.mphToKmh(Float.parseFloat(data.get(FIELD_WIND_GUST_CURRENT_MPH))));
      awekasData[16] = String.format(Locale.ENGLISH, "%.1f", Float.parseFloat(data.get(FIELD_SOLAR_RADIATION)));
      awekasData[17] = String.format(Locale.ENGLISH, "%.1f", Float.parseFloat(data.get(FIELD_UV)));
      awekasData[21] = String.format(Locale.ENGLISH, "%.2f", 1000*Calculators.imperialToMetric(Float.parseFloat(data.get(FIELD_RAIN_CURRENT_RATE_INCH))));
      awekasData[22] = appName + "%20" + appVersion;

      uri = URI.create(config.getUrl() + String.join(";", awekasData));
    } catch (Exception e) {
      throw new InvalidDataException("Cannot build URI: " + e, e);
    }

    httpForwarderService.transmitByGetRequest(uri, evaluateSuccessFromBody());
  }

  @NotNull
  protected Function<String, Boolean> evaluateSuccessFromBody() {
    return (body) -> body.toLowerCase(Locale.ROOT).matches("ok");
  }

  @Override
  Logger getLogger() {
    return LOGGER;
  }

  @Override
  public String getForwarderName() {
    return "Awekas";
  }


  @Override
  public boolean isActive() {
    return config != null && !config.getUrl().isBlank();
  }

  @Override
  public long getTimeoutInSeconds() {
    return config != null ? config.getTimeout() : 1;
  }
}
