package de.christophlorenz.jweatherflux.forwarder;

import static de.christophlorenz.jweatherflux.forwarder.Forwarder.FIELD_BAROMETER_RELATIVE_INTERNAL;
import static de.christophlorenz.jweatherflux.forwarder.Forwarder.FIELD_DATEUTC;
import static de.christophlorenz.jweatherflux.forwarder.Forwarder.FIELD_HUMIDITY_OUTDOOR;
import static de.christophlorenz.jweatherflux.forwarder.Forwarder.FIELD_RAIN_CURRENT_RATE_INCH;
import static de.christophlorenz.jweatherflux.forwarder.Forwarder.FIELD_RAIN_DAILY_INCH;
import static de.christophlorenz.jweatherflux.forwarder.Forwarder.FIELD_RAIN_HOURLY_INCH;
import static de.christophlorenz.jweatherflux.forwarder.Forwarder.FIELD_SOLAR_RADIATION;
import static de.christophlorenz.jweatherflux.forwarder.Forwarder.FIELD_TEMPERATURE_OUTDOOR_FARENHEIT;
import static de.christophlorenz.jweatherflux.forwarder.Forwarder.FIELD_UV;
import static de.christophlorenz.jweatherflux.forwarder.Forwarder.FIELD_WIND_DIRECTION_10MIN_AVG;
import static de.christophlorenz.jweatherflux.forwarder.Forwarder.FIELD_WIND_DIRECTION_CURRENT;
import static de.christophlorenz.jweatherflux.forwarder.Forwarder.FIELD_WIND_GUST_CURRENT_MPH;
import static de.christophlorenz.jweatherflux.forwarder.Forwarder.FIELD_WIND_SPEED_10MIN_AVG;
import static de.christophlorenz.jweatherflux.forwarder.Forwarder.FIELD_WIND_SPEED_CURRENT_MPH;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.christophlorenz.jweatherflux.config.ForwarderProperties;
import io.micrometer.core.instrument.MeterRegistry;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

@DisplayName("The Awekas forwarder")
class AwekasTest {

  private Awekas forwarder;
  private ForwarderProperties.Awekas awekasProperties;
  private HttpForwarderService httpForwarderService;
  private MeterRegistry meterRegistry;

  @BeforeEach
  public void beforeEach() {
    ForwarderProperties forwarderProperties = mock(ForwarderProperties.class);
    awekasProperties = mock(ForwarderProperties.Awekas.class);
    when(awekasProperties.getUrl()).thenReturn("http://api.awekas.at/station.php?val=");

    when(forwarderProperties.getAwekas()).thenReturn(awekasProperties);

    httpForwarderService = mock(HttpForwarderService.class);

    meterRegistry = mock(MeterRegistry.class);

    forwarder = new Awekas(httpForwarderService, forwarderProperties,  "Test", "1.0", meterRegistry);
  }

  @Test
  @DisplayName("fills the queryString corrrectly")
  public void fillsQueryStringCorrectly()
      throws AuthorizationException, TransmitException, InvalidDataException {
    when(awekasProperties.getId()).thenReturn("12345");
    when(awekasProperties.getKey()).thenReturn("12345abcde");

    Map<String, String> data = new HashMap<>();
    data.put(FIELD_DATEUTC, "2022-02-14+16%3A56%3A02");
    data.put(FIELD_TEMPERATURE_OUTDOOR_FARENHEIT, "40.5");
    data.put(FIELD_HUMIDITY_OUTDOOR, "50.0");
    data.put(FIELD_WIND_DIRECTION_CURRENT, "200.0");
    data.put(FIELD_WIND_SPEED_CURRENT_MPH, "10.0");
    data.put(FIELD_WIND_GUST_CURRENT_MPH, "15.0");
    data.put(FIELD_BAROMETER_RELATIVE_INTERNAL, "30.5");
    data.put(FIELD_RAIN_CURRENT_RATE_INCH, "0.5");
    data.put(FIELD_RAIN_DAILY_INCH, "2.5");
    data.put(FIELD_UV, "3");
    data.put(FIELD_SOLAR_RADIATION, "200.2");

    forwarder.forward(data);

    ArgumentCaptor<URI> uriArgumentCaptor = ArgumentCaptor.forClass(URI.class);
    verify(httpForwarderService, times(1)).transmitByGetRequest(uriArgumentCaptor.capture(), any(
        Function.class));

    String queryString = uriArgumentCaptor.getValue().getQuery().split("=")[1];

    String[] parts = queryString.split(";", -1);

    assertThat(parts[0]).isEqualTo("12345");
    assertThat(parts[1]).isEqualTo("d5170a3e24af791ba3d674760619fcd9");
    assertThat(parts[2]).isEqualTo("14.02.2022");
    assertThat(parts[3]).isEqualTo("16:56");        // UTC ?!
    assertThat(parts[4]).isEqualTo("4.7");
    assertThat(parts[5]).isEqualTo("50");
    assertThat(parts[6]).isEqualTo("1032.82");
    assertThat(parts[7]).isEqualTo("63.50");      // mm
    assertThat(parts[8]).isEqualTo("16.1");
    assertThat(parts[9]).isEqualTo("200");
    assertThat(parts[10]).isEmpty();
    assertThat(parts[11]).isEmpty();
    assertThat(parts[12]).isEmpty();
    assertThat(parts[13]).isEqualTo("de");
    assertThat(parts[14]).isEmpty();
    assertThat(parts[15]).isEqualTo("24.1");
    assertThat(parts[16]).isEqualTo("200.2");
    assertThat(parts[17]).isEqualTo("3.0");
    assertThat(parts[18]).isEmpty();
    assertThat(parts[19]).isEmpty();
    assertThat(parts[20]).isEmpty();
    assertThat(parts[21]).isEqualTo("12.70");
    assertThat(parts[22]).isEqualTo("Test 1.0");
    assertThat(parts[23]).isEmpty();
    assertThat(parts[24]).isEmpty();
  }

  @Test
  @DisplayName("evaluates the success status properly")
  public void evaluatesSuccessStatusProperly() {
    String responseBodyOK = "OK";

    assertThat(forwarder.evaluateSuccessFromBody().apply(responseBodyOK)).isTrue();
  }

  @Test
  @DisplayName("evaluates the failed status properly")
  public void evaluatesFailedStatusProperly() {
    String responseBodyFailed = "Benutzer/Passwort-Fehler";

    assertThat(forwarder.evaluateSuccessFromBody().apply(responseBodyFailed)).isFalse();
  }

}