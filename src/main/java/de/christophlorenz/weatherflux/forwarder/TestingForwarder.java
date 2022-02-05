package de.christophlorenz.weatherflux.forwarder;

import de.christophlorenz.weatherflux.config.ForwarderProperties;
import de.christophlorenz.weatherflux.config.InfluxProperties;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import retrofit2.http.HTTP;

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
    return (config != null);
  }

  @Override
  public void forward(Map<String, String> data) {
    CloseableHttpClient client = HttpClients.createDefault();
    try {
      HttpPost post = new HttpPost(config.getUrl());
      List<NameValuePair> postData = new ArrayList<NameValuePair>();
      data.forEach((key, value) -> postData.add(new BasicNameValuePair(key, URLDecoder.decode(value, StandardCharsets.UTF_8))));
      post.setEntity(new UrlEncodedFormEntity(postData));
      HttpResponse response = client.execute(post);
      if (response.getStatusLine().getStatusCode() <= 199
          || response.getStatusLine().getStatusCode() >= 400) {
        LOGGER.warn(
            "Could not successfully forward data to " + config.getUrl() + " because of status="
                + response.getStatusLine());
      }
    } catch (HttpHostConnectException e) {
      // Ignore. because normally, there's no one to receive the forwarded data
      // LOGGER.warn("Cannot forward data to " + config.getUrl() + ": " + e);
    } catch (Exception e) {
      LOGGER.warn("Cannot forward data to " + config.getUrl() + ": " + e, e);
    } finally {
      try {
        client.close();
      } catch (IOException e) {
        LOGGER.warn("Cannot close client to " + config.getUrl() + ": " + e, e);
      }
    }
  }
}
