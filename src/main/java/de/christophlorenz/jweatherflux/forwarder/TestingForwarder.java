package de.christophlorenz.jweatherflux.forwarder;

import de.christophlorenz.jweatherflux.config.ForwarderProperties;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.http.NameValuePair;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class TestingForwarder implements Forwarder {

  private static final Logger LOGGER = LoggerFactory.getLogger(TestingForwarder.class);

  private final ForwarderProperties.TestingForwarder config;

  public TestingForwarder(ForwarderProperties forwarderProperties) {
    if (forwarderProperties.getTestingForwarder() != null) {
      config = forwarderProperties.getTestingForwarder();
    } else {
      config = null;
    }
  }

  @Override
  public boolean isActive() {
    return config != null && !config.getUrl().isBlank();
  }

  @Override
  public void forward(Map<String, String> data) {
    try (CloseableHttpClient client = HttpClients.createDefault();) {
      HttpPost post = new HttpPost(config.getUrl());
      List<NameValuePair> postData = new ArrayList<NameValuePair>();
      data.forEach((key, value) -> postData.add(new BasicNameValuePair(key, URLDecoder.decode(value, StandardCharsets.UTF_8))));
      post.setEntity(new UrlEncodedFormEntity(postData));
      try(CloseableHttpResponse response = client.execute(post)) {
        if (response.getStatusLine().getStatusCode() <= 199
            || response.getStatusLine().getStatusCode() >= 400) {
          LOGGER.warn(
              "Could not successfully forward data to " + config.getUrl() + " because of status="
                  + response.getStatusLine());
        }
      } catch (HttpHostConnectException | NoHttpResponseException ignore) {
        // ignore, since we normally don't have an active receiver for forwards
        return;
      } catch (Exception e) {
        LOGGER.warn("Cannot forward data to " + config.getUrl() + ": " + e, e);
      }
    } catch (HttpHostConnectException e) {
      // Ignore. because normally, there's no one to receive the forwarded data
      // LOGGER.warn("Cannot forward data to " + config.getUrl() + ": " + e);
    } catch (Exception e) {
      LOGGER.warn("Cannot forward data to " + config.getUrl() + ": " + e, e);
    }
  }
}
