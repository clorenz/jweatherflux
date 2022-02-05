package de.christophlorenz.weatherflux.config;

import java.util.HashMap;
import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.forwarders")
public class ForwarderProperties {

  private TestingForwarder testingForwarder;

  public void setTestingForwarder(TestingForwarder testingForwarder) {
    this.testingForwarder = testingForwarder;
  }

  public TestingForwarder getTestingForwarder() {
    return testingForwarder;
  }

  public static class TestingForwarder {
    private String url;

    public String getUrl() {
      return url;
    }

    public void setUrl(String url) {
      this.url = url;
    }

    @Override
    public String toString() {
      return "TestingForwarder{" +
          "url='" + url + '\'' +
          '}';
    }
  }
}
