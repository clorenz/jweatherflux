package de.christophlorenz.jweatherflux.config;

import de.christophlorenz.jweatherflux.forwarder.WetterCom;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.forwarders")
public class ForwarderProperties {

  private TestingForwarder testingForwarder;
  private Wunderground wunderground;
  private WetterCom wetterCom;

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

  public WetterCom getWetterCom() {
    return wetterCom;
  }

  public void setWetterCom(WetterCom wetterCom) {
    this.wetterCom = wetterCom;
  }

  @Override
  public String toString() {
    return "ForwarderProperties{" +
        "testingForwarder=" + testingForwarder +
        ", wunderground=" + wunderground +
        ", wetterCom=" + wetterCom +
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

  protected static String mask(String stringToMask) {
    return stringToMask.replaceAll(".", "*");
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
          ", key='" + mask(key) + '\'' +
          ", period=" + period +
          '}';
    }
  }

  public static class WetterCom {
    private String url;
    private String userId;
    private String idKennwort;
    private int interval;

    public String getUrl() {
      return url;
    }

    public void setUrl(String url) {
      this.url = url;
    }

    public String getUserId() {
      return userId;
    }

    public void setUserId(String userId) {
      this.userId = userId;
    }

    public String getIdKennwort() {
      return idKennwort;
    }

    public void setIdKennwort(String idKennwort) {
      this.idKennwort = idKennwort;
    }

    public int getInterval() {
      return interval;
    }

    public void setInterval(int interval) {
      this.interval = interval;
    }

    @Override
    public String toString() {
      return "WetterCom{" +
          "url='" + url + '\'' +
          "userId='" + userId + '\'' +
          ", idKennwort='" + mask(idKennwort) + '\'' +
          ", interval=" + interval +
          '}';
    }
  }
}
