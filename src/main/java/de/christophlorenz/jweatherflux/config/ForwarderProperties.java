package de.christophlorenz.jweatherflux.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.forwarders")
public class ForwarderProperties {

  private TestingForwarder testingForwarder;
  private Wunderground wunderground;

  public void setTestingForwarder(TestingForwarder testingForwarder) {
    this.testingForwarder = testingForwarder;
  }

  public TestingForwarder getTestingForwarder() {
    return testingForwarder;
  }

  public Wunderground getWunderground() {
    return wunderground;
  }

  public void setWunderground(
      Wunderground wunderground) {
    this.wunderground = wunderground;
  }

  @Override
  public String toString() {
    return "ForwarderProperties{" +
        "testingForwarder=" + testingForwarder +
        ", wunderground=" + wunderground +
        '}';
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

  public static class Wunderground {
    private String url;
    private String id;
    private String key;
    private int period;

    public String getUrl() {
      return url;
    }

    public void setUrl(String url) {
      this.url = url;
    }

    public String getId() {
      return id;
    }

    public void setId(String id) {
      this.id = id;
    }

    public String getKey() {
      return key;
    }

    public void setKey(String key) {
      this.key = key;
    }

    public int getPeriod() {
      return period;
    }

    public void setPeriod(int period) {
      this.period = period;
    }

    @Override
    public String toString() {
      return "Wunderground{" +
          "url='" + url + '\'' +
          ", id='" + id + '\'' +
          ", key='" + key + '\'' +
          ", period=" + period +
          '}';
    }
  }
}
