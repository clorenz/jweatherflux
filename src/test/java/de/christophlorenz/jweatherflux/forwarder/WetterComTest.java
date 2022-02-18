package de.christophlorenz.jweatherflux.forwarder;

import static de.christophlorenz.jweatherflux.forwarder.Forwarder.FIELD_BAROMETER_RELATIVE_INTERNAL;
import static de.christophlorenz.jweatherflux.forwarder.Forwarder.FIELD_DATEUTC;
import static de.christophlorenz.jweatherflux.forwarder.Forwarder.FIELD_HUMIDITY_OUTDOOR;
import static de.christophlorenz.jweatherflux.forwarder.Forwarder.FIELD_RAIN_CURRENT_RATE_INCH;
import static de.christophlorenz.jweatherflux.forwarder.Forwarder.FIELD_RAIN_HOURLY_INCH;
import static de.christophlorenz.jweatherflux.forwarder.Forwarder.FIELD_SOLAR_RADIATION;
import static de.christophlorenz.jweatherflux.forwarder.Forwarder.FIELD_TEMPERATURE_INDOOR_FARENHEIT;
import static de.christophlorenz.jweatherflux.forwarder.Forwarder.FIELD_TEMPERATURE_OUTDOOR_FARENHEIT;
import static de.christophlorenz.jweatherflux.forwarder.Forwarder.FIELD_UV;
import static de.christophlorenz.jweatherflux.forwarder.Forwarder.FIELD_WIND_DIRECTION_CURRENT;
import static de.christophlorenz.jweatherflux.forwarder.Forwarder.FIELD_WIND_GUST_CURRENT_MPH;
import static de.christophlorenz.jweatherflux.forwarder.Forwarder.FIELD_WIND_SPEED_CURRENT_MPH;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.christophlorenz.jweatherflux.config.ForwarderProperties;
import de.christophlorenz.jweatherflux.data.Calculators;
import java.net.URI;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

// FIXME Base Test (maybe AbstractHttpForwarderTest)

@DisplayName("The wetter.com forwarder")
class WetterComTest {

  private WetterCom forwarder;
  private ForwarderProperties.WetterCom wetterComProperties;
  private HttpForwarderService httpForwarderService;

  @BeforeEach
  public void beforeEach() {
    ForwarderProperties forwarderProperties = mock(ForwarderProperties.class);
    wetterComProperties = mock(ForwarderProperties.WetterCom.class);
    when(wetterComProperties.getUrl()).thenReturn("https://weatherstation.wunderground.com/weatherstation/updateweatherstation.php");

    when(forwarderProperties.getWetterCom()).thenReturn(wetterComProperties);

    httpForwarderService = mock(HttpForwarderService.class);

    forwarder = new WetterCom(forwarderProperties, httpForwarderService, "Test", "1.0");
  }


  @Test
  @DisplayName("evaluates the success status properly")
  public void evaluatesSuccessStatusProperly() {
    String responseBodyOK = "{\"status\":\"SUCCESS\",\"version\":\"6.0\"}";

    assertThat(forwarder.evaluateSuccessFromBody().apply(responseBodyOK)).isTrue();
  }

  @Test
  @DisplayName("evaluates the failed status properly")
  public void evaluatesFailedStatusProperly() {
    String responseBodyFailed = "{\"status\":\"FAIL\",\"version\":\"6.0\"}";

    assertThat(forwarder.evaluateSuccessFromBody().apply(responseBodyFailed)).isFalse();
  }

  @Test
  @DisplayName("fills the queryString corrrectly")
  public void fillsQueryStringCorrectly()
      throws AuthorizationException, TransmitException, InvalidDataException {
    when(wetterComProperties.getUserId()).thenReturn("12345");
    when(wetterComProperties.getIdKennwort()).thenReturn("12345abcde");

    Map<String, String> data = new HashMap<>();
    data.put(FIELD_DATEUTC, "2022-02-14+16%3A56%3A02");
    data.put(FIELD_TEMPERATURE_OUTDOOR_FARENHEIT, "40.5");
    data.put(FIELD_HUMIDITY_OUTDOOR, "50.0");
    data.put(FIELD_WIND_DIRECTION_CURRENT, "200.0");
    data.put(FIELD_WIND_SPEED_CURRENT_MPH, "10.0");
    data.put(FIELD_WIND_GUST_CURRENT_MPH, "15.0");
    data.put(FIELD_BAROMETER_RELATIVE_INTERNAL, "30.5");
    data.put(FIELD_RAIN_CURRENT_RATE_INCH, "0.5");
    data.put(FIELD_RAIN_HOURLY_INCH, "0.25");
    data.put(FIELD_UV, "3");
    data.put(FIELD_SOLAR_RADIATION, "200");

    forwarder.forward(data);

    String expectedUrl = "https://weatherstation.wunderground.com/weatherstation/updateweatherstation.php"
        + "?id=12345"
        + "&pwd=12345abcde"
        + "&sid=API50"
        + "&ver=Test+1.0"
        + "&dtutc=202202141656"
        + "&hu=50.0"
        + "&te=4.7"
        + "&pr=1032.8"
        + "&dp=-4.8"
        + "&wd=200"
        + "&ws=4.5"
        + "&wg=6.7"
        + "&pa=0.01"
        + "&rr=0.01"
        + "&uv=3"
        + "&sr=200.00";


    URI expectedUri = URI.create(expectedUrl);
    ArgumentCaptor<URI> uriArgumentCaptor = ArgumentCaptor.forClass(URI.class);
    verify(httpForwarderService, times(1)).transmitByGetRequest(uriArgumentCaptor.capture(), any(Function.class));

    assertThat(uriArgumentCaptor.getValue()).isEqualTo(expectedUri);
  }
}