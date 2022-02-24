package de.christophlorenz.jweatherflux.forwarder;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.function.Function;
import org.apache.http.StatusLine;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
//TODO Refactor into service package
public class HttpForwarderService {

  private static final Logger LOGGER = LoggerFactory.getLogger(HttpForwarderService.class);

  /**
   * Emits a GET request with the full URL, including query string
   * @param uri the URI which will be requested with GET
   */
  public void transmitByGetRequest(URI uri, Function<String, Boolean> successIndicatorFunction) throws TransmitException, InvalidDataException, AuthorizationException {
    try (CloseableHttpClient client = HttpClients.createDefault();) {
      HttpGet get = new HttpGet(uri);

      String responseBody;
      StatusLine statusLine;
      try (CloseableHttpResponse response = client.execute(get)) {
        responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8)
            .replaceAll("\\n", "");
        statusLine = response.getStatusLine();
      } catch (Exception e) {
        throw new TransmitException("Cannot forward data to " + uri.getHost() + ": " + e);
      }

      int statusCode = statusLine.getStatusCode();
      if (statusCode == 401) {
        throw new AuthorizationException("Could not successfully forward data to " + uri.getHost()
            + " because of status Unauthorized");
      }

      if (statusCode <= 199 || statusCode >= 400) {
        throw new TransmitException(
            "Could not successfully forward data to " + uri + " because of status="
                + statusLine + ", body=" + responseBody);
      }


      if (!successIndicatorFunction.apply(responseBody)) {
        throw new InvalidDataException(
            "Could not successfully forward data to " + get + " because of response='"
                + responseBody + "'");
      }
      LOGGER.debug("Successfully forwarded data to " + uri.getHost() + " with status=" + statusCode + ", response body=" + responseBody );
    } catch (IOException e) {
      throw new TransmitException("Cannot forward data to " + uri.getHost() + ": " + e);
    }
  }

  public void transmitByPostRequest(URI uri, UrlEncodedFormEntity postEntity, boolean ignoreConnectionErrors)
      throws TransmitException, IgnoredConnectionException {
    try (CloseableHttpClient client = HttpClients.createDefault();) {
      HttpPost post = new HttpPost(uri);
      post.setEntity(postEntity);
      try(CloseableHttpResponse response = client.execute(post)) {
        if (response.getStatusLine().getStatusCode() <= 199
            || response.getStatusLine().getStatusCode() >= 400) {
          LOGGER.warn(
              "Could not successfully forward data to " + uri.getHost()+ " because of status="
                  + response.getStatusLine());
        }
      } catch (Exception e) {
        if (ignoreConnectionErrors) {
          throw new IgnoredConnectionException();
        }

        throw new TransmitException("Cannot forward data to " + uri.getHost() + ": " + e);
      }

    } catch (IOException e) {
      if (ignoreConnectionErrors) {
        throw new IgnoredConnectionException();
      }

      throw new TransmitException("Cannot forward data to " + uri.getHost() + ": " + e);
    }
  }
}
