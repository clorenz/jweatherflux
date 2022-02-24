package de.christophlorenz.jweatherflux.forwarder;

import de.christophlorenz.jweatherflux.config.ForwarderProperties;
import io.micrometer.core.instrument.MeterRegistry;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class TestingForwarder extends AbstractForwarder {

  private static final Logger LOGGER = LoggerFactory.getLogger(TestingForwarder.class);

  private final ForwarderProperties.TestingForwarder config;
  private final HttpForwarderService httpForwarderService;

  public TestingForwarder(ForwarderProperties forwarderProperties, HttpForwarderService httpForwarder, MeterRegistry meterRegistry) {
    super(0, meterRegistry);

    if (forwarderProperties.getTestingForwarder() != null) {
      config = forwarderProperties.getTestingForwarder();
    } else {
      config = null;
    }

    this.httpForwarderService = httpForwarder;
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
      throws TransmitException, InvalidDataException, IgnoredConnectionException {
    List<NameValuePair> postData = new ArrayList<>();
    data.forEach((key, value) -> postData.add(new BasicNameValuePair(key, URLDecoder.decode(value, StandardCharsets.UTF_8))));
    try {
      httpForwarderService.transmitByPostRequest(URI.create(config.getUrl()), new UrlEncodedFormEntity(postData), true);
    } catch (UnsupportedEncodingException e) {
      throw new InvalidDataException("Invalid encoding of POST request data: " + e);
    }
  }

  @Override
  Logger getLogger() {
    return LOGGER;
  }

  @Override
  public String getForwarderName() {
    return "Testing";
  }
}
