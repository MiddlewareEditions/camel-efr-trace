package com.enterpriseflowsrepository.camel.traces;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import org.jetbrains.annotations.NotNull;

/**
 * A way to properly get environment properties.
 */
public class TracesEnvironnement {

  private final Config config;

  /**
   * Initialize the environment getter.
   */
  public TracesEnvironnement() {
    config = ConfigProvider.getConfig();
  }

  /**
   * Get the environment name, as defined in the configuration.
   * @return the environment name.
   */
  public @NotNull String getEnvironment() {
    return getValue("environment");
  }

  public @NotNull String getRouteId() {
    return getValue("route.id");
  }

  public @NotNull String getRouteName() {
    return getValue("route.name");
  }

  public @NotNull String getRouteVersion() {
    return getValue("route.version", "latest");
  }

  public @NotNull String getRouteDatacenter() {
    return getValue("route.datacenter", "unspecified-datacenter");
  }

  public @NotNull String getBusinessRule() {
    return getValue("trace.business", "");
  }

  public @NotNull String getApplicationName() {
    return config.getOptionalValue("quarkus.application.name", String.class).orElse("camel-application");
  }

  public int getMaxHeadersSize() {
    return config.getOptionalValue("efr.traces.max-headers-size", Integer.class).orElse(4095);
  }

  public int getMaxStackTraceSize() {
    return config.getOptionalValue("efr.traces.max-stacktrace-size", Integer.class).orElse(4095);
  }

  public @NotNull String getValue(@NotNull String key) {
    return config.getValue(key, String.class);
  }

  private @NotNull String getValue(@NotNull String key, @NotNull String defaultValue) {
    return config.getOptionalValue(key, String.class).orElse(defaultValue);
  }

  public Iterable<String> getPropertyNames() {
    return config.getPropertyNames();
  }

}
