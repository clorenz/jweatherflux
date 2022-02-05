package de.christophlorenz.weatherflux.forwarder;

import de.christophlorenz.weatherflux.config.ForwarderProperties;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class Wunderground implements Forwarder {

  private static final Logger LOGGER = LoggerFactory.getLogger(Wunderground.class);

  private final ForwarderProperties.Wunderground config;
  private long lastRunMillis = 0;

  public Wunderground(ForwarderProperties forwarderProperties) {
    if (forwarderProperties.getWunderground() != null) {
      config = forwarderProperties.getWunderground();
    } else {
      config = null;
    }
  }

  @Override
  public boolean isActive() {
    return config != null;
  }

  @Override
  public void forward(Map<String, String> data) {
    if (!isReadyToSend()) {
      long deltaInSeconds = (System.currentTimeMillis() - lastRunMillis) / 1000;
      long remainingSeconds = config.getPeriod() - deltaInSeconds;
      LOGGER.debug("Skipping request at least " + remainingSeconds + " seconds until " + new Date(System.currentTimeMillis() + (1000 * remainingSeconds)));
      return;
    }


    try (CloseableHttpClient client = HttpClients.createDefault();) {
      URIBuilder builder = new URIBuilder(config.getUrl());
      builder.setParameter("ID", config.getId());
      builder.setParameter("PASSWORD", config.getKey());
      builder.setParameter("dateutc",
          URLDecoder.decode(data.get("dateutc"), StandardCharsets.UTF_8));

      addData(builder, data, "winddir");
      addData(builder, data, "windspeedmph");
      addData(builder, data, "windgustmph");
      addData(builder, data, "humidity");
      addData(builder, data, "tempf");
      addData(builder, data, "rainin", "hourlyrainin");
      addData(builder, data, "dailyrainin");
      addData(builder, data, "baromin", "baromrelin");
      addData(builder, data, "solarradiation");
      addData(builder, data, "UV", "uv");
      addData(builder, data, "softwaretype", "stationtype");

      HttpGet get = new HttpGet(builder.build());
      try(CloseableHttpResponse response = client.execute(get)) {
        if (response.getStatusLine().getStatusCode() ==401 ) {
          LOGGER.warn("Could not successfully forward data to "+ config + " because of status Unauthorized");
          return;
        }

        if (response.getStatusLine().getStatusCode() <= 199
            || response.getStatusLine().getStatusCode() >= 400) {
          LOGGER.warn(
              "Could not successfully forward data to " + config.getUrl() + " because of status="
                  + response.getStatusLine());
          return;
        }
        String responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8).replaceAll("\\n", "");
        if (! ("success".equalsIgnoreCase(responseBody))) {
          LOGGER.error(
              "Could not successfully forward data to " + get + " because of response='"
                  + responseBody + "'");
          return;
        }
        LOGGER.debug("Successfully forwarded data to WeatherUnderground");
      } catch (Exception e) {
        LOGGER.warn("Cannot forward data to " + config.getUrl() + ": " + e, e);
      }

    } catch (InvalidDataException e) {
      LOGGER.error("Cannot forward data to " + config.getUrl() + " because of " + e);
    } catch (HttpHostConnectException e) {
      LOGGER.warn("Cannot forward data to " + config.getUrl() + ": " + e);
    } catch (Exception e) {
      LOGGER.warn("Cannot forward data to " + config.getUrl() + ": " + e, e);
    }

    lastRunMillis = System.currentTimeMillis();
  }

  private void addData(URIBuilder builder, Map<String, String> data, String unmappedKey) throws InvalidDataException {
    addData(builder, data, unmappedKey, unmappedKey);
  }

  private void addData(URIBuilder builder, Map<String, String> data, String wundergroundKey, String key) throws InvalidDataException {
    if (data.get(key)==null) {
      throw new InvalidDataException("No data found for key=" + key);
    }
    builder.setParameter(wundergroundKey, data.get(key));
  }

  private boolean isReadyToSend() {
    long deltaInSeconds = (System.currentTimeMillis() - lastRunMillis) / 1000;
    return (deltaInSeconds >= config.getPeriod());
  }
}
