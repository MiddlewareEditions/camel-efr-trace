package com.enterpriseflowsrepository.camel.clients;

import com.enterpriseflowsrepository.camel.clients.bean.Trace;
import com.enterpriseflowsrepository.camel.clients.oidc.*;
import com.enterpriseflowsrepository.camel.exceptions.EfrApiException;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.function.Consumer;

/**
 * A client to send traces to EFR.
 */
@RequiredArgsConstructor
public class TracesClient {

  private static final Logger LOG = org.slf4j.LoggerFactory.getLogger(TracesClient.class);

  private final StaticEndpointConfiguration config;
  private final HttpClient client;
  private final Consumer<HttpRequest.Builder> requestCustomizer;

  /**
   * Send one trace to EFR.
   * @param trace The trace to be sent to EFR. Must not be null.
   */
  public void sendTrace(@NotNull Trace trace) {
    var builder = HttpRequest.newBuilder()
        .uri(URI.create(config.hostname() + "/api/traces"))
        .header("Content-Type", "application/json")
        .POST(HttpRequest.BodyPublishers.ofString(trace.toJSON().toString()));

    // Auth
    requestCustomizer.accept(builder);

    HttpRequest request = builder.build();

    try {
      LOG.debug("Sending trace to EFR.");
      LOG.debug("- URL: '{}'.", request.uri());
      LOG.debug("- Method: '{}'.", request.method());
      LOG.debug("- Headers: '{}'.", request.headers());
      LOG.debug("- Body: '{}'.", trace.toJSON());

      HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
      if(response.statusCode() >= 300) {
        LOG.error("Failed to send trace. Status code: {}, Response body: {}", response.statusCode(), response.body());
        throw new EfrApiException(response.statusCode());
      }

    } catch (IOException | InterruptedException e) {
      throw new EfrApiException(e);
    }
  }

  /**
   * Initialize a new TracesClient instance with the default configuration.
   * @return A new instance of TracesClient ready to use.
   */
  public static @NotNull TracesClient initialize() {
    var config = StaticOidcConfiguration.fromProperties();
    var host = StaticEndpointConfiguration.fromProperties();
    var tokenProvider = new JavaTokenProvider(config);

    HttpClient client = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(10))
        .sslContext(EmptyTrustManager.getEmptySSLContext()) // ignore security
        .followRedirects(HttpClient.Redirect.NORMAL)
        .build();

    return new TracesClient(
        host,
        client,
        new OidcHandler(tokenProvider)
    );
  }

}
