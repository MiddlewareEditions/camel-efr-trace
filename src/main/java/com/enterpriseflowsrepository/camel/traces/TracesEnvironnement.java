package com.enterpriseflowsrepository.camel.traces;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * A way to properly get environment properties.
 */
@ApiStatus.Internal
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

  /**
   * Get the route id, as defined in the configuration.
   * @return the route id.
   */
  public @NotNull String getRouteId() {
    return getValue("route.id");
  }

  /**
   * Get the route name, as defined in the configuration.
   * @return the route name.
   */
  public @NotNull String getRouteName() {
    return getValue("route.name");
  }

  /**
   * Get the route version, as defined in the configuration.
   * @return the route version.
   */
  public @NotNull String getRouteVersion() {
    return getValue("route.version", "latest");
  }

  /**
   * Get the route datacenter, as defined in the configuration.
   * @return the route datacenter.
   */
  public @NotNull String getRouteDatacenter() {
    return getValue("route.datacenter", "unspecified-datacenter");
  }

  /**
   * Get the business rule, as defined in the configuration.
   * @return the business rule.
   */
  public @NotNull String getBusinessRule() {
    return getValue("trace.business", "");
  }

  /**
   * Get the application name, as defined in the configuration.
   * @return the application name.
   */
  public @NotNull String getApplicationName() {
    return config.getOptionalValue("quarkus.application.name", String.class).orElse("camel-application");
  }

  /**
   * Get the maximum size of headers to be traced, as defined in the configuration.
   * @return the maximum size of headers to be traced, or {@code 4095} if not defined.
   */
  public int getMaxHeadersSize() {
    return config.getOptionalValue("efr.traces.max-headers-size", Integer.class).orElse(4095);
  }

  /**
   * Get the maximum size of body to be traced, as defined in the configuration.
   * @return the maximum size of body to be traced, or {@code 4095} if not defined.
   */
  public int getMaxStackTraceSize() {
    return config.getOptionalValue("efr.traces.max-stacktrace-size", Integer.class).orElse(4095);
  }

  /**
   * Get the value of a property, as defined in the configuration.
   * @param key the property key.
   * @return the property value.
   */
  public @NotNull String getValue(@NotNull String key) {
    return config.getValue(key, String.class);
  }

  private @NotNull String getValue(@NotNull String key, @NotNull String defaultValue) {
    return config.getOptionalValue(key, String.class).orElse(defaultValue);
  }

  /**
   * Get the names of all properties defined in the configuration.
   * @return the names of all properties defined in the configuration.
   */
  public @NotNull Iterable<String> getPropertyNames() {
    return config.getPropertyNames();
  }

}
