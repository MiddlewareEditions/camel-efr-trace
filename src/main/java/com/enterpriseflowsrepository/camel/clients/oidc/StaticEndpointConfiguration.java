package com.enterpriseflowsrepository.camel.clients.oidc;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * Immutable configuration class for EFR endpoint.
 * @param hostname EFR hostname.
 */
public record StaticEndpointConfiguration(
    @NotNull String hostname
) {

  /**
   * Create a StaticEndpointConfiguration instance by reading the necessary properties from the configuration source.
   * @return A new instance of StaticEndpointConfiguration populated with values from the configuration.
   */
  @Contract("-> new")
  public static @NotNull StaticEndpointConfiguration fromProperties() {
    Config config = ConfigProvider.getConfig();
    String rawHost = config.getValue("efr.hostname", String.class);
    String hostname = rawHost.endsWith("/") ? rawHost.substring(0, rawHost.length() - 1) : rawHost;
    return new StaticEndpointConfiguration(hostname);
  }
}
