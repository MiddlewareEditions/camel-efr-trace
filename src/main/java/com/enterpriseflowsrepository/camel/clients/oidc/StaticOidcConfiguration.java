package com.enterpriseflowsrepository.camel.clients.oidc;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Immutable configuration class for OIDC authentication, populated from application properties. <br/>
 * This class implements the {@link OidcConfiguration} interface and provides a static factory method.
 * @param hostname OIDC hostname.
 * @param realm OIDC realm.
 * @param grantType OIDC grant type.
 * @param clientId OIDC client ID.
 * @param clientSecret OIDC client secret.
 * @param username OIDC username (optional, required for "password" grant type).
 * @param password OIDC password (optional, required for "password" grant type).
 */
public record StaticOidcConfiguration(
    @NotNull String hostname,
    @NotNull String realm,
    @NotNull GrantType grantType,
    @NotNull String clientId,
    @NotNull String clientSecret,
    @Nullable String username,
    @Nullable String password
) implements OidcConfiguration {

  /**
   * Create a StaticOidcConfiguration instance by reading the necessary properties from the configuration source.
   * @return A new instance of StaticOidcConfiguration populated with values from the configuration.
   */
  @Contract("-> new")
  public static @NotNull StaticOidcConfiguration fromProperties() {
    Config config = ConfigProvider.getConfig();
    String hostname = config.getValue("efr.oidc.hostname", String.class);
    String realm = config.getValue("efr.oidc.realm", String.class);
    GrantType grantType = config.getValue("efr.oidc.grant-type", GrantType.class);
    String clientId = config.getValue("efr.oidc.client-id", String.class);
    String clientSecret = config.getValue("efr.oidc.client-secret", String.class);
    String username = config.getOptionalValue("efr.oidc.username", String.class).orElse(null);
    String password = config.getOptionalValue("efr.oidc.password", String.class).orElse(null);
    return new StaticOidcConfiguration(hostname, realm, grantType, clientId, clientSecret, username, password);
  }

  @Override
  public @NotNull String getHostnameOIDC() {
    return hostname;
  }

  @Override
  public @NotNull String getAuthRealm() {
    return realm;
  }

  @Override
  public @Nullable String getAuthUsername() {
    return username;
  }

  @Override
  public @Nullable String getAuthPassword() {
    return password;
  }

  @Override
  public @NotNull GrantType getGrantType() {
    return grantType;
  }

  @Override
  public @NotNull String getClientId() {
    return clientId;
  }

  @Override
  public @NotNull String getClientSecret() {
    return clientSecret;
  }
}
