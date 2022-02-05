package de.christophlorenz.weatherflux.model;

import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.stream.Collectors;

public class WeatherData {

  public static final ZoneOffset ZONE_OFFSET = ZoneOffset.UTC;
  private long timestampMillis;
  private Map<String, Float> humidities;
  Map<String, Float> tempsFarenheit;
  Map<String, Float> tempsCelsius;
  Map<String, Float> baromAbsImperial;
  Map<String, Float> baromRelImperial;
  Map<String, Float> baromAbsHpa;
  Map<String, Float> baromRelHpa;
  Map<String, Float> rainImperial;
  Map<String, Float> rainMetric;
  Map<String, Float> windspeedsMph;
  Map<String, Float> windspeedsKmh;
  Map<String, Float> winddirs;
  Float solarRadiation;
  Float uv;

  public void setTimestamp(Instant timestamp) {
    timestampMillis = timestamp.toEpochMilli();
  }

  public void setTimestamp(LocalDateTime timestamp) {
    this.timestampMillis = timestamp.toInstant(ZONE_OFFSET).toEpochMilli();
  }

  public void setHumidities(Map<String, Float> humidities) {
    this.humidities = humidities;
  }

  public void setTemperaturesInFarenheit(Map<String, Float> tempsFarenheit) {
    this.tempsFarenheit = tempsFarenheit;
  }

  public void setTemperaturesInCelsius(Map<String, Float> tempsCelsius) {
    this.tempsCelsius = tempsCelsius;
  }

  public Map<String, Float> getHumidities() {
    return humidities;
  }

  public Map<String, Float> getTempsFarenheit() {
    return tempsFarenheit;
  }


  public void setTempsFarenheit(Map<String, Float> tempsFarenheit) {
    this.tempsFarenheit = tempsFarenheit;
  }

  public Map<String, Float> getTempsCelsius() {
    return tempsCelsius;
  }

  public void setTempsCelsius(Map<String, Float> tempsCelsius) {
    this.tempsCelsius = tempsCelsius;
  }

  public Map<String, Float> getBaromAbsImperial() {
    return baromAbsImperial;
  }

  public void setBaromAbsImperial(Map<String, Float> baromAbsImperial) {
    this.baromAbsImperial = baromAbsImperial;
  }

  public Map<String, Float> getBaromRelImperial() {
    return baromRelImperial;
  }

  public void setBaromRelImperial(Map<String, Float> baromRelImperial) {
    this.baromRelImperial = baromRelImperial;
  }

  public Map<String, Float> getBaromAbsHpa() {
    return baromAbsHpa;
  }

  public void setBaromAbsHpa(Map<String, Float> baromAbsHpa) {
    this.baromAbsHpa = baromAbsHpa;
  }

  public Map<String, Float> getBaromRelHpa() {
    return baromRelHpa;
  }

  public void setBaromRelHpa(Map<String, Float> baromRelHpa) {
    this.baromRelHpa = baromRelHpa;
  }

  public Map<String, Float> getRainImperial() {
    return rainImperial;
  }

  public void setRainImperial(Map<String, Float> rainImperial) {
    this.rainImperial=rainImperial;
  }

  public Map<String, Float> getRainMetric() {
    return rainMetric;
  }

  public void setRainMetric(Map<String, Float> rainMetric) {
    this.rainMetric=rainMetric;
  }

  public void setSolrRadiation(Float solrRadiation) {
    this.solarRadiation = solrRadiation;
  }

  public void setWindspeedsMph(Map<String, Float> windspeedsMph) {
    this.windspeedsMph = windspeedsMph;
  }

  public void setWindspeedsKmh(Map<String, Float> windspeedsKmh) {
    this.windspeedsKmh = windspeedsKmh;
  }

  public void setWinddirs(Map<String, Float> winddirs) {
    this.winddirs = winddirs;
  }

  public void setUv(Float uv) {
    this.uv = uv;
  }

  public List<Point> getAllAsPoints() {
    List<Point> points = new ArrayList<>();
    points.addAll(toPoints("humidity", humidities));
    points.addAll(toPoints("temperature_farenheit", tempsFarenheit));
    points.addAll(toPoints("temperature_celsius", tempsCelsius));
    points.addAll(toPoints("barometer_absolute_imperial", baromAbsImperial));
    points.addAll(toPoints("barometer_relative_imperial", baromRelImperial));
    points.addAll(toPoints("barometer_absolute_hpa", baromAbsHpa));
    points.addAll(toPoints("barometer_relative_hpa", baromRelHpa));
    points.addAll(toPoints("rain_imperial", rainImperial));
    points.addAll(toPoints("rain_metric", rainMetric));
    points.add(getSolrRadiationPoint());
    points.addAll(toPoints("windspeeds_mph", windspeedsMph));
    points.addAll(toPoints("windspeeds_kmh", windspeedsKmh));
    points.addAll(toPoints("winddirs", "when", winddirs));
    points.add(getCurrentWindPoint("windrose_kmh",windspeedsKmh.get("current"), winddirs.get("current")));
    points.add(getUvPoint());
    return points;
  }

  private Point getCurrentWindPoint(String measurement, Float currentWindspeedKmh, Float currentWindDir) {
    return Point.measurement(measurement)
        .addField("speed", currentWindspeedKmh)
        .addField("direction", currentWindDir)
        .time(timestampMillis, WritePrecision.MS);
  }

  private List<Point> toPoints(String measurement, Map<String, Float> data) {
    return data.entrySet().stream()
        .map( e -> {
          return Point.measurement(measurement)
              .addTag("location",e.getKey())
              .addField("value", e.getValue())
              //.time(timestamp.atZone(ZONE_OFFSET).toInstant().toEpochMilli() + (int)(1000d * Math.random()), WritePrecision.MS);
              .time(timestampMillis, WritePrecision.MS);
        })
        .collect(Collectors.toList());
  }

  private List<Point> toPoints(String measurement, String tag, Map<String, Float> data) {
    return data.entrySet().stream()
        .map( e -> {
          return Point.measurement(measurement)
              .addTag("location",tag)
              .addField("value", e.getValue())
              //.time(timestamp.atZone(ZONE_OFFSET).toInstant().toEpochMilli() + (int)(1000d * Math.random()), WritePrecision.MS);
              //.time(timestamp.toEpochSecond(ZONE_OFFSET), WritePrecision.S);
              .time(timestampMillis, WritePrecision.MS);
        })
        .collect(Collectors.toList());
  }

  private Point getSolrRadiationPoint() {
    return Point.measurement("solarradiation")
        .addTag("when","current")
        .addField("value", solarRadiation)
        //.time(timestamp.atZone(ZONE_OFFSET).toInstant().toEpochMilli(), WritePrecision.MS);
        .time(timestampMillis, WritePrecision.MS);
  }

  private Point getUvPoint() {
    return Point.measurement("uv")
        .addTag("when","current")
        .addField("value", uv)
        //.time(timestamp.atZone(ZONE_OFFSET).toInstant().toEpochMilli(), WritePrecision.MS);
        .time(timestampMillis, WritePrecision.MS);
  }


  @Override
  public String toString() {
    return "WeatherData{" +
        "timestamp=" + LocalDateTime.ofInstant(Instant.ofEpochMilli(timestampMillis), ZONE_OFFSET) +
        " (" + timestampMillis + ")" +
        "\n, humidities=" + humidities +
        "\n, tempsFarenheit=" + tempsFarenheit +
        "\n, tempsCelsius=" + tempsCelsius +
        "\n, baromAbsImperial=" + baromAbsImperial +
        "\n, baromRelImperial=" + baromRelImperial +
        "\n, baromAbsHpa=" + baromAbsHpa +
        "\n, baromRelHpa=" + baromRelHpa +
        "\n, rainImperial=" + rainImperial +
        "\n, rainMetric=" + rainMetric +
        "\n, windspeedsMph=" + windspeedsMph +
        "\n, windspeedsKmh=" + windspeedsKmh +
        "\n, winddirs=" + winddirs +
        "\n, solarRadiation=" + solarRadiation +
        "\n, uv=" + uv +
        '}';
  }
}
