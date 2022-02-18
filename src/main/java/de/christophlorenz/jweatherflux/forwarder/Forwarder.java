package de.christophlorenz.jweatherflux.forwarder;

import java.util.Map;

public interface Forwarder {

  public static final String FIELD_BAROMETER_ABSOULTE_INTERNAL = "baromabsin";
  public static final String FIELD_BAROMETER_RELATIVE_INTERNAL = "baromrelin";
  public static final String FIELD_BATTERY_SENSOR1 = "batt1";
  public static final String FIELD_RAIN_DAILY_INCH = "dailyrainin";
  public static final String FIELD_DATEUTC = "dateutc";
  public static final String FIELD_RAIN_EVENT_INCH = "eventrainin";
  public static final String FIELD_STATION_FREQUENCY = "freq";
  public static final String FIELD_RAIN_HOURLY_INCH = "hourlyrainin";
  public static final String FIELD_HUMIDITY_OUTDOOR = "humidity";
  public static final String FIELD_HUMIDITY_SENSOR1 = "humidity1";
  public static final String FIELD_HUMIDITY_INDOOR = "humidityin";
  public static final String FIELD_WIND_GUST_DAILY_MAX_MPH = "maxdailygust";
  public static final String FIELD_STATION_MODEL = "model";
  public static final String FIELD_RAIN_MONTHLY_INCH = "monthlyrainin";
  public static final String FIELD_STATION_PASSKEY = "PASSKEY";
  public static final String FIELD_RAIN_CURRENT_RATE_INCH = "rainratein";
  public static final String FIELD_SOLAR_RADIATION = "solarradiation";
  public static final String FIELD_STATION_TYPE = "stationtype";
  public static final String FIELD_TEMPERATURE_OUTDOOR_FARENHEIT = "tempf";
  public static final String FIELD_TEMPERATURE_SENSOR1_FARENHEIT = "temp1f";
  public static final String FIELD_TEMPERATURE_INDOOR_FARENHEIT = "tempinf";
  public static final String FIELD_UV = "uv";
  public static final String FIELD_BATTERY_WH25 = "wh25batt";
  public static final String FIELD_BATTERY_WH40 = "wh40batt";
  public static final String FIELD_BATTERY_WH80 = "wh80batt";
  public static final String FIELD_RAIN_WEEKLY_INCH = "weeklyrainin";
  public static final String FIELD_WIND_DIRECTION_CURRENT = "winddir";
  public static final String FIELD_WIND_DIRECTION_10MIN_AVG = "winddir_avg10m";
  public static final String FIELD_WIND_GUST_CURRENT_MPH = "windgustmph";
  public static final String FIELD_WIND_SPEED_CURRENT_MPH = "windspeedmph";
  public static final String FIELD_WIND_SPEED_10MIN_AVG = "windspdmph_avg10m";
  public static final String FIELD_RAIN_YEARLY_INCH = "yearlyrainin";

  boolean isActive();

  /**
   * Forward the data with the following keys:
   * <ul>
   *   <li><b<baromabsin</b>: e.g. 27.893</li>
   *   <li><b>baromrelin</b>: e.g. 29.882</b></li>
   *   <li><b>batt1</b>: e.g. 0</li>
   *   <li><b>dailyrainin</b>: e.g. 0.000</li>
   *   <li><b<dateutc</b>: e.g. 2022-02-14+19%3A46%3A11</li>
   *   <li><b<eventrainin</b>: e.g. 0.000</li>
   *   <li><b>freq</b>: e.g. 868M</li>
   *   <li><b>hourlyrainin:</b> e.g. 0.000</li>
   *   <li><b>humidity</b>: e.g. 72</li>
   *   <li><b>humidity1</b>: e.g. 51</li>
   *   <li><b>humidityin</b>: e.g. 4</li>
   *   <li><b>maxdailygust:</b>: e.g. 9.8</li>
   *   <li><b>model</b>: e.g. HP1000SE-PRO_Pro_V1.6.4</li>
   *   <li><b>monthlyrainin</b>: e.g. 0.402</li>
   *   <li><b>PASSKEY</b>: e.g. ABCDEFG</li>
   *   <li><b>rainratein</b>: e.g. 0.000</li>
   *   <li><b>solarradiation</b>: e.g. 0.000</li>
   *   <li><b>stationtype</b>: e.g. EasyWeatherV1.4.6</li>
   *   <li><b>tempf</b>: e.g. 40.3</li>
   *   <li><b>temp1f</b>: e.g. 49.3</li>
   *   <li><b>tempinf</b>: e.g. 72.7</li>
   *   <li><b<uv</b>: e.g. 0</li>
   *   <li><b>wh25batt</b>: e.g. 0</li>
   *   <li><b>wh40batt</b>: e.g. 1.6</li>
   *   <li><b>wh80batt</b>: e.g. 3.28</li>
   *   <li><b>weeklyrainin</b>: e.g. 0.000</li>
   *   <li><b>winddir</b>: e.g. 103</li>
   *   <li><b>winddir_avg10m</b>: e.g. 106</li>
   *   <li><b>windgustmph</b>: e.g. 2.5</li>
   *   <li><b>windspeedmph</b>: e.g. 0.0</li>
   *   <li><b>windspdmph_avg10m</b>: e.g. 1.1</li>
   *   <li><b>yearlyrainin</b>: e.g. 0.402</b></li>
   * </ul>
   * @param data
   */
  void forward(Map<String, String> data);
}
