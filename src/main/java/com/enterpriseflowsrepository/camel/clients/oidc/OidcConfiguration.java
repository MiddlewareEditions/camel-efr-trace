package com.enterpriseflowsrepository.camel.clients.oidc;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URI;

/**
 * Defines the configuration properties required for OIDC authentication.
 */
public interface OidcConfiguration {

  /**
   * Get the URL of the Keycloak token endpoint. For example: {@code https://keycloak.example.com/realms/myrealm/protocol/openid-connect/token}.
   * @return a valid URL.
   */
  @NotNull String getHostnameOIDC();

  /**
   * Get the authentication realm. This is typically the Keycloak realm name that the client belongs to.
   * @return a non-null realm ID.
   */
  @NotNull String getAuthRealm();

  /**
   * Get the authentication username. This is required for the "password" grant type, but can be null for "client_credentials" grant type.
   * @return the username for authentication, or null if not applicable.
   */
  @Nullable String getAuthUsername();

  /**
   * Get the authentication password. This is required for the "password" grant type, but can be null for "client_credentials" grant type.
   * @return the password for authentication, or null if not applicable.
   */
  @Nullable String getAuthPassword();

  /**
   * Get the grant type to be used for authentication. This determines how the access token will be obtained from Keycloak. <br/>
   * - "password": The client will use the Resource Owner Password Credentials grant, which requires a username and password. <br/>
   * - "client_credentials": The client will use the Client Credentials grant, which only requires a client ID and secret. <br/>
   * @return the grant type to be used for authentication.
   */
  @NotNull GrantType getGrantType();

  /**
   * Get the client ID registered in Keycloak for this application.<br/>
   * This is required for both "password" and "client_credentials" grant types, as it identifies the client application to Keycloak.
   * @return the client ID for authentication.
   */
  @NotNull String getClientId();

  /**
   * Get the client secret registered in Keycloak for this application.<br/>
   * This is required for both "password" and "client_credentials" grant types, as it identifies the client application to Keycloak.
   * @return the client secret for authentication.
   */
  @NotNull String getClientSecret();

  /**
   * Construct the form body for the OIDC token request based on the configured grant type and credentials.
   * @return a URL-encoded form string to be sent in the body of the token request to Keycloak.
   */
  default @NotNull String getAuthForm() {
    return switch (getGrantType()) {
      case PASSWORD -> String.format("grant_type=password&client_id=%s&client_secret=%s&username=%s&password=%s",
          getClientId(), getClientSecret(), getAuthUsername(), getAuthPassword());
      case CLIENT_CREDENTIALS -> String.format("grant_type=client_credentials&client_id=%s&client_secret=%s",
          getClientId(), getClientSecret());
    };
  }

  /**
   * Construct the full URI for the OIDC token endpoint based on the configured hostname and realm.
   * @return a URI pointing to the Keycloak token endpoint for this client configuration.
   */
  default @NotNull URI getAuthURI() {
    boolean addSlash = !getHostnameOIDC().endsWith("/");
    String keycloakUrl = getHostnameOIDC() + (addSlash ? "/" : "") + "realms/" + getAuthRealm() + "/protocol/openid-connect/token";
    return URI.create(keycloakUrl);
  }

  /**
   * Enum representing the supported OIDC grant types for authentication.
   */
  enum GrantType {

    /**
     * The Resource Owner Password Credentials grant, which requires a username and password, on top of a client ID and secret.
     */
    PASSWORD,

    /**
     * The Client Credentials grant, which only requires a client ID and secret.
     */
    CLIENT_CREDENTIALS
  }
}
