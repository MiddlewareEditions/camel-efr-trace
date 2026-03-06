package com.enterpriseflowsrepository.camel.clients;

import com.enterpriseflowsrepository.camel.clients.bean.Trace;
import com.enterpriseflowsrepository.camel.clients.oidc.JavaTokenProvider;
import com.enterpriseflowsrepository.camel.clients.oidc.OidcFilter;
import com.enterpriseflowsrepository.camel.clients.oidc.StaticOidcConfiguration;
import com.enterpriseflowsrepository.camel.exceptions.EfrApiException;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.net.URI;
import java.util.List;

/**
 * A client to send traces to EFR.
 */
@RequiredArgsConstructor
public class TracesClient {

  private static final Logger LOG = org.slf4j.LoggerFactory.getLogger(TracesClient.class);
  private final TracesApi api;

  /**
   * Send one trace to EFR.
   * @param trace The trace to be sent to EFR. Must not be null.
   */
  public void sendTrace(@NotNull Trace trace) {
    try {
      api.addTrace(trace);
    } catch (ApiException e) {
      LOG.error("API exception while sending trace: {}", e.getResponse().getStatus());
      throw new EfrApiException(e);
    } catch (ProcessingException e) {
      LOG.error("Processing exception while sending trace: {}.", e.getMessage());
      throw new EfrApiException(e);
    }
  }

  /**
   * Initialize a new TracesClient instance with the default configuration.
   * @return A new instance of TracesClient ready to use.
   */
  public static @NotNull TracesClient initialize() {
    var config = StaticOidcConfiguration.fromProperties();
    var tokenProvider = new JavaTokenProvider(config);

    var api = RestClientBuilder.newBuilder()
        .baseUri(URI.create("https://localhost:8080"))
        .register(new OidcFilter(tokenProvider))
        .build(TracesApi.class);
    return new TracesClient(api);
  }

}
