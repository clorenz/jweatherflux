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
  private Awekas awekas;

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

  public Awekas getAwekas() {
    return awekas;
  }

  public void setAwekas(Awekas awekas) {
    this.awekas = awekas;
  }

  @Override
  public String toString() {
    return "ForwarderProperties{" +
        "testingForwarder=" + testingForwarder +
        ", wunderground=" + wunderground +
        ", wetterCom=" + wetterCom +
        ", awekas=" + awekas +
        '}';
  }

  public static class TestingForwarder {
    private String url;
    private int timeout=5;

    public String getUrl() {
      return url;
    }

    public void setUrl(String url) {
      this.url = url;
    }

    public int getTimeout() {
      return timeout;
    }

    public void setTimeout(int timeout) {
      this.timeout = timeout;
    }

    @Override
    public String toString() {
      return "TestingForwarder{" +
          "url='" + url + '\'' +
          ", timeout=" + timeout +
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
    private int timeout = 30;

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

    public int getTimeout() {
      return timeout;
    }

    public void setTimeout(int timeout) {
      this.timeout = timeout;
    }

    @Override
    public String toString() {
      return "Wunderground{" +
          "url='" + url + '\'' +
          ", id='" + id + '\'' +
          ", key='" + mask(key) + '\'' +
          ", period=" + period +
          ", timeout=" + timeout +
          '}';
    }
  }

  public static class WetterCom {
    private String url;
    private String userId;
    private String idKennwort;
    private int interval;
    private int timeout = 10;     // seconds

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

    public int getTimeout() {
      return timeout;
    }

    public void setTimeout(int timeout) {
      this.timeout = timeout;
    }

    @Override
    public String toString() {
      return "WetterCom{" +
          "url='" + url + '\'' +
          "userId='" + userId + '\'' +
          ", idKennwort='" + mask(idKennwort) + '\'' +
          ", interval=" + interval +
          ", timeout=" + timeout +
          '}';
    }
  }

  public static class Awekas {
    private String url;
    private String id;
    private String key;
    private int interval;
    private int timeout = 10;

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

    public int getInterval() {
      return interval;
    }

    public void setInterval(int interval) {
      this.interval = interval;
    }

    public int getTimeout() {
      return timeout;
    }

    public void setTimeout(int timeout) {
      this.timeout = timeout;
    }

    @Override
    public String toString() {
      return "Awekas{" +
          "url='" + url + '\'' +
          ", id='" + id + '\'' +
          ", key='" + key + '\'' +
          ", interval=" + interval +
          ", timeout=" + timeout +
          '}';
    }
  }
}
