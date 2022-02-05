package de.christophlorenz.weatherflux.model;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class WeatherDataBuilder {

  WeatherData weatherData = new WeatherData();

  public WeatherData build() {
    return weatherData;
  }

  public WeatherDataBuilder atUtc(String utcTimestamp) {
    Instant timestamp = Instant.parse(utcTimestamp);
    //weatherData.setTimestamp(LocalDateTime.ofInstant(timestamp, ZoneOffset.systemDefault()));
    weatherData.setTimestamp(timestamp);
    return this;
  }

  public WeatherDataBuilder withHumidities(Map<String, Float> humidities) {
    weatherData.setHumidities(humidities);
    return this;
  }

  public WeatherDataBuilder withTemperaturesInFarenheit(Map<String, Float> tempsFarenheit) {
    weatherData.setTemperaturesInFarenheit(tempsFarenheit);
    return this;
  }

  public WeatherDataBuilder withTemperaturesInCelsius(Map<String, Float> tempsCelsius) {
    weatherData.setTemperaturesInCelsius(tempsCelsius);
    return this;
  }

  public WeatherDataBuilder withBarometerAbsoluteImperial(Map<String, Float> baromAbsImperial) {
    weatherData.setBaromAbsImperial(baromAbsImperial);
    return this;
  }

  public WeatherDataBuilder withBarometerRelativeImperial(Map<String, Float> baromRelImperial) {
    weatherData.setBaromRelImperial(baromRelImperial);
    return this;
  }

  public WeatherDataBuilder withBarometerAbsoluteHPa(Map<String, Float> baromAbsHpa) {
    weatherData.setBaromAbsHpa(baromAbsHpa);
    return this;
  }

  public WeatherDataBuilder withBarometerRelativeHpa(Map<String, Float> baromRelHpa) {
    weatherData.setBaromRelHpa(baromRelHpa);
    return this;
  }

  public WeatherDataBuilder withRainImperial(Map<String, Float> rainImperial) {
    weatherData.setRainImperial(rainImperial);
    return this;
  }

  public WeatherDataBuilder withRainMetric(Map<String, Float> rainMetric) {
    weatherData.setRainMetric(rainMetric);
    return this;
  }


  public WeatherDataBuilder withSolarRadiation(Float solarradiation) {
    weatherData.setSolrRadiation(solarradiation);
    return this;
  }

  public WeatherDataBuilder withWindspeedsMph(Map<String, Float> windspeedsMph) {
    weatherData.setWindspeedsMph(windspeedsMph);
    return this;
  }

  public WeatherDataBuilder withWindspeedsKmh(Map<String, Float> windspeedsKmh) {
    weatherData.setWindspeedsKmh(windspeedsKmh);
    return this;
  }

  public WeatherDataBuilder withWinddirs(Map<String, Float> winddirs) {
    weatherData.setWinddirs(winddirs);
    return this;
  }

  public WeatherDataBuilder withUv(Float uv) {
    weatherData.setUv(uv);
    return this;
  }
}
