package de.christophlorenz.jweatherflux.forwarder;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.function.Function;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class HttpForwarderService {

  private static final Logger LOGGER = LoggerFactory.getLogger(HttpForwarderService.class);

  /**
   * Emits a GET request with the full URL, including query string
   * @param uri the URI which will be requested with GET
   */
  public void transmitByGetRequest(URI uri, Function<String, Boolean> successIndicatorFunction) throws TransmitException, InvalidDataException, AuthorizationException {
    try (CloseableHttpClient client = HttpClients.createDefault();) {
      HttpGet get = new HttpGet(uri);

      try (CloseableHttpResponse response = client.execute(get)) {
        String responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8)
            .replaceAll("\\n", "");

        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode == 401) {
          throw new AuthorizationException("Could not successfully forward data to " + uri.getHost()
              + " because of status Unauthorized");
        }

        if (statusCode <= 199 || statusCode >= 400) {
          throw new TransmitException(
              "Could not successfully forward data to " + uri + " because of status="
                  + response.getStatusLine() + ", body=" + responseBody);
        }


        if (!successIndicatorFunction.apply(responseBody)) {
          throw new InvalidDataException(
              "Could not successfully forward data to " + get + " because of response='"
                  + responseBody + "'");
        }
        LOGGER.debug("Successfully forwarded data to " + uri.getHost() + " with status=" + statusCode + ", response body=" + responseBody );
      } catch (Exception e) {
        LOGGER.warn("Cannot forward data to " + uri.getHost() + ": " + e, e);
      }
    } catch (HttpHostConnectException e) {
      throw new TransmitException("Cannot forward data to " + uri.getHost() + ": " + e);
    } catch (Exception e) {
      throw new InvalidDataException("Cannot forward data to " + uri.getHost() + ": " + e, e);
    }
  }
}
