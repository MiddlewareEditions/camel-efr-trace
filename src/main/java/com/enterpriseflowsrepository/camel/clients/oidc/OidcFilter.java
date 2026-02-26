package com.enterpriseflowsrepository.camel.clients.oidc;

import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientRequestFilter;
import jakarta.ws.rs.core.HttpHeaders;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

/**
 * Filter to add the OIDC access token to outgoing requests.
 */
@RequiredArgsConstructor
public class OidcFilter implements ClientRequestFilter {

  private final TokenProvider tokenProvider;

  @Override
  public void filter(@NotNull ClientRequestContext request) {
    // Récupère le token (depuis le cache ou via un nouvel appel)
    String token = tokenProvider.getAccessToken();
    request.getHeaders().add(HttpHeaders.AUTHORIZATION, "Bearer " + token);
  }

}
