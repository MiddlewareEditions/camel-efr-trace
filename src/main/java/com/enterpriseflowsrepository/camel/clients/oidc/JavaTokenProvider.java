package com.enterpriseflowsrepository.camel.clients.oidc;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;

/**
 * A TokenProvider implementation that fetches tokens from Keycloak using Java's built-in HttpClient. <br/>
 * The token is cached until it expires, to avoid unnecessary calls to Keycloak.
 */
public class JavaTokenProvider implements TokenProvider {

  private final URI tokenUrl;
  private final String authFormString;

  private final HttpClient httpClient;

  // Token cache
  private transient String cachedToken;
  private transient Instant expiryTime;

  /**
   * Create a new JavaTokenProvider instance with the given Keycloak configuration. <br/>
   * The token will be fetched on demand and cached until it expires.
   * @param oidcConfiguration The OIDC configuration containing the necessary parameters to authenticate with Keycloak. Must not be null.
   */
  public JavaTokenProvider(@NotNull OidcConfiguration oidcConfiguration) {
    this.tokenUrl = oidcConfiguration.getAuthURI();
    this.authFormString = oidcConfiguration.getAuthForm();

    // One instance of HTTP-client and JSON-mapper.
    this.httpClient = HttpClient.newHttpClient();
  }

  @Override
  public synchronized @NotNull String getAccessToken() {
    // On vérifie si le token est null ou s'il expire dans moins de 10 secondes (marge de sécurité)
    if (cachedToken == null || Instant.now().plusSeconds(10).isAfter(expiryTime)) {
      fetchNewToken();
    }
    return cachedToken;
  }

  private void fetchNewToken() {
    // Create the request
    HttpRequest request = HttpRequest.newBuilder()
        .uri(tokenUrl)
        .header("Content-Type", "application/x-www-form-urlencoded")
        .POST(HttpRequest.BodyPublishers.ofString(authFormString))
        .build();

    try {
      // Call keycloak
      HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
      if (response.statusCode() != 200) {
        throw new RuntimeException("Could not authenticate to Keycloak. Code: " + response.statusCode() + ".");
      }

      // Parse response
      JSONObject json = new JSONObject(response.body());
      this.cachedToken = json.getString("access_token");

      // Compute expiry time based on "expires_in" field (in seconds)
      long expiresIn = json.getLong("expires_in");
      this.expiryTime = Instant.now().plusSeconds(expiresIn);
    } catch (Exception e) {
      throw new RuntimeException("Impossible de récupérer le jeton OIDC", e);
    }
  }

}
