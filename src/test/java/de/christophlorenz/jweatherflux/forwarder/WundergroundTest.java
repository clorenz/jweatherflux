package de.christophlorenz.jweatherflux.forwarder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.christophlorenz.jweatherflux.config.ForwarderProperties;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("The WeatchUnderground forwarder")
class WundergroundTest {

  private Wunderground forwarder;
  private ForwarderProperties.Wunderground wundergroundProperties;
  private HttpForwarderService httpForwarderService;
  private MeterRegistry meterRegistry;

  @BeforeEach
  public void beforeEach() {
    ForwarderProperties forwarderProperties = mock(ForwarderProperties.class);
    wundergroundProperties = mock(ForwarderProperties.Wunderground.class);
    when(wundergroundProperties.getUrl()).thenReturn("https://weatherstation.wunderground.com/weatherstation/updateweatherstation.php");

    when(forwarderProperties.getWunderground()).thenReturn(wundergroundProperties);

    httpForwarderService = mock(HttpForwarderService.class);

    meterRegistry = mock(MeterRegistry.class);

    forwarder = new Wunderground(forwarderProperties, httpForwarderService, "Test", "1.0", meterRegistry);
  }

  @Test
  @DisplayName("evaluates the success status properly")
  public void evaluatesSuccessStatusProperly() {
    String responseBodyOK = "success";

    assertThat(forwarder.evaluateSuccessFromBody().apply(responseBodyOK)).isTrue();
  }

  @Test
  @DisplayName("evaluates the failed status properly")
  public void evaluatesFailedStatusProperly() {
    String responseBodyFailed = "unsuccessful";

    assertThat(forwarder.evaluateSuccessFromBody().apply(responseBodyFailed)).isFalse();
  }

}