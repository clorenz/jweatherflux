package de.christophlorenz.weatherflux.service;

import de.christophlorenz.weatherflux.model.WeatherData;
import de.christophlorenz.weatherflux.model.WeatherDataBuilder;
import de.christophlorenz.weatherflux.repository.InfluxRepository;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class DataReportService {

  private static final Logger LOGGER = LoggerFactory.getLogger(DataReportService.class);
  private static final String PREFIX_HUMIDITY = "humidity";
  private static final String REGEX_TEMP_FARENHEIT = "temp.*?f";
  private static final String PREFIX_BAROMETER_ABSOLUTE = "baromabs";
  private static final String PREFIX_BAROMETER_RELATIVE = "baromrel";
  private static final String REGEX_WINDDIR = "winddir(_.*)?";
  private static final String REGEX_WINDSPEED_MPH = "windspeedmph|windspdmph";
  private static final String REGEX_RAIN = ".*rain.*";
  private static final String DATEUTC = "dateutc";

  private final InfluxRepository repository;

  public DataReportService(InfluxRepository influxRepository) {
    this.repository = influxRepository;
  }

  public void persist(Map<String, String> data) throws PersistException {
    try {
      Map<String, Float> humidities = extractHumidities(data);
      Map<String, Float> tempsFarenheit = extractTemperaturesFarenheit(data);
      Map<String, Float> tempsCelsius = mapToCelsius(tempsFarenheit);
      Map<String, Float> baromAbsImperial = extractBarometerImperial(data, PREFIX_BAROMETER_ABSOLUTE);
      Map<String, Float> baromRelImperial = extractBarometerImperial(data, PREFIX_BAROMETER_RELATIVE);
      Map<String, Float> baromAbsHpa = mapToHectopascal(baromAbsImperial);
      Map<String, Float> baromRelHpa = mapToHectopascal(baromRelImperial);
      Map<String, Float> rainImperial = extractRainImperial(data);
      Map<String, Float> rainMetric = mapToMetric(rainImperial);
      Float solarradiation = Float.parseFloat(data.get("solarradiation"));
      Map<String, Float> windspeedsMph = extractWindspeedsMph(data);
      windspeedsMph.put("gust", Float.parseFloat(data.get("windgustmph")));
      windspeedsMph.put("maxdailygust", Float.parseFloat(data.get("maxdailygust")));
      Map<String, Float> windspeedsKmh = mapMphToKmh(windspeedsMph);
      Map<String, Float> winddirs = extractWinddirs(data);
      Float uv = Float.parseFloat(data.get("uv"));

      WeatherData weatherData = new WeatherDataBuilder()
          .atUtc(fixTimestamp(data.get(DATEUTC)))
          .withHumidities(humidities)
          .withTemperaturesInFarenheit(tempsFarenheit)
          .withTemperaturesInCelsius(tempsCelsius)
          .withBarometerAbsoluteImperial(baromAbsImperial)
          .withBarometerRelativeImperial(baromRelImperial)
          .withBarometerAbsoluteHPa(baromAbsHpa)
          .withBarometerRelativeHpa(baromRelHpa)
          .withRainImperial(rainImperial)
          .withRainMetric(rainMetric)
          .withSolarRadiation(solarradiation)
          .withWindspeedsMph(windspeedsMph)
          .withWindspeedsKmh(windspeedsKmh)
          .withWinddirs(winddirs)
          .withUv(uv)
          .build();

      repository.persist(weatherData);
    } catch (Exception e) {
      throw new PersistException("Cannot persist data=" + data + ": " + e, e);
    }
  }

  private Map<String, Float> extractWindspeedsMph(Map<String, String> data) {
    return data.entrySet().stream().filter(d -> d.getKey().matches(REGEX_WINDSPEED_MPH))
        .collect(Collectors.toMap(d -> fixCurrentKey(d.getKey().replaceFirst(REGEX_WINDSPEED_MPH, "")), d -> Float.valueOf(d.getValue())));
  }

  private Map<String, Float> mapMphToKmh(Map<String, Float> windspeedsMph) {
    return windspeedsMph.entrySet().stream().collect(Collectors.toMap(d -> d.getKey(), d -> mphToKmh(d.getValue())));
  }

  private Map<String, Float> extractWinddirs(Map<String, String> data) {
    return data.entrySet().stream().filter(d -> d.getKey().matches(REGEX_WINDDIR))
        .collect(Collectors.toMap(d -> fixCurrentKey(d.getKey().replaceFirst("winddir_?", "")), d -> Float.valueOf(d.getValue())));
  }

  private String fixTimestamp(String encodedRawString) {
    return URLDecoder.decode(encodedRawString, StandardCharsets.UTF_8).replace(" ","T") + "Z";
  }

  private Map<String, Float> extractRainImperial(Map<String, String> data) {
    return data.entrySet().stream().filter(d -> d.getKey().matches(REGEX_RAIN))
        .collect(Collectors.toMap(d -> d.getKey().replaceFirst("rain", ""), d -> Float.valueOf(d.getValue())));
  }

  private Map<String, Float> mapToHectopascal(Map<String, Float> baromImperial) {
    return baromImperial.entrySet().stream().collect(Collectors.toMap(d -> d.getKey(), d -> inHgToHpa(d.getValue())));
  }

  private Map<String, Float> extractBarometerImperial(Map<String, String> data, String prefix) {
    return data.entrySet().stream().filter(d -> d.getKey().startsWith(prefix))
        .collect(Collectors.toMap(d -> d.getKey().replaceFirst("^" + prefix, ""), d -> Float.valueOf(d.getValue())));
  }

  private Map<String, Float> mapToCelsius(Map<String, Float> tempsFarenheit) {
    return tempsFarenheit.entrySet().stream().collect(Collectors.toMap(d -> d.getKey(), d -> farenheitToCelsius(d.getValue())));
  }

  private Map<String, Float> extractTemperaturesFarenheit(Map<String, String> data) {
    return data.entrySet().stream().filter(d -> d.getKey().matches(REGEX_TEMP_FARENHEIT))
        .collect(Collectors.toMap(d -> fixKey(d.getKey().replaceFirst("^temp", "").replaceFirst("f$","")), d -> Float.valueOf(d.getValue())));
  }

  private Map<String, Float> extractHumidities(Map<String, String> data) {
    return data.entrySet().stream().filter(d -> d.getKey().startsWith(PREFIX_HUMIDITY))
        .collect(Collectors.toMap(d -> fixKey(d.getKey().replaceFirst("^" + PREFIX_HUMIDITY, "")), d -> Float.valueOf(d.getValue())));
  }

  private Map<String, Float> mapToMetric(Map<String, Float> rainImperial) {
    return rainImperial.entrySet().stream().collect(Collectors.toMap(d -> d.getKey(), d -> imperialToMetric(d.getValue())));
  }

  private Float farenheitToCelsius(Float farenheit) {
    return (farenheit - 32f) * (5f / 9f);
  }

  private Float inHgToHpa(Float inHg) {
    return inHg * 33.863f;
  }

  private Float mphToKmh(Float mph) {
    return mph * 1.60934f;
  }

  private Float imperialToMetric(Float imperial) {
    return imperial * 0.0254f;
  }

  private String fixKey(String key) {
    return (key==null || key.isBlank()) ? "default" : key;
  }

  private String fixCurrentKey(String key) {
    return (key==null || key.isBlank()) ? "current" : key;
  }
}
