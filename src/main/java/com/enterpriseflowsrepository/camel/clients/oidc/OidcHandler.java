package com.enterpriseflowsrepository.camel.clients.oidc;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.net.http.HttpRequest;
import java.util.function.Consumer;

/**
 * Filter to add the OIDC access token to outgoing requests.
 */
@RequiredArgsConstructor
public class OidcHandler implements Consumer<HttpRequest.Builder> {

  private final TokenProvider tokenProvider;

  @Override
  public void accept(@NotNull HttpRequest.Builder request) {
    // Récupère le token (depuis le cache ou via un nouvel appel)
    String token = tokenProvider.getAccessToken();
    request.header("Authorization", "Bearer " + token);
  }

}
