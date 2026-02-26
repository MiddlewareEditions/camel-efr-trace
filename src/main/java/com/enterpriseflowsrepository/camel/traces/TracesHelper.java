package com.enterpriseflowsrepository.camel.traces;

import com.enterpriseflowsrepository.api.traces.beans.*;
import com.enterpriseflowsrepository.api.traces.beans.Exception;
import com.enterpriseflowsrepository.camel.EfrTraceEndpoint;
import org.apache.camel.Exchange;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.regex.PatternSyntaxException;

/**
 * Utility class to parse traces.
 */
public final class TracesHelper {
  private TracesHelper() {}

  private static final Logger LOG = LoggerFactory.getLogger(TracesHelper.class);

  /**
   * Find a value in the exchange, first looking in properties, then in headers. Returns null if not found.
   * @param exchange The exchange to look into.
   * @param key The key to look for.
   * @return The value found, or null if not found.
   */
  public static @Nullable String findValue(@NotNull Exchange exchange, @NotNull String key) {
    String inProperty = exchange.getProperty(key, String.class);
    if(inProperty != null) return inProperty;
    return exchange.getIn().getHeader(key, String.class);
  }

  /**
   * Find a value in the exchange, first looking in properties, then in headers. Returns defaultValue if not found.
   * @param exchange The exchange to look into.
   * @param key The key to look for.
   * @param defaultValue The default value to return if not found.
   * @return The value found, or defaultValue if not found.
   */
  public static @NotNull String findValue(@NotNull Exchange exchange, @NotNull String key, @NotNull String defaultValue) {
    return Objects.requireNonNullElse(findValue(exchange, key), defaultValue);
  }

  /** Find a value in the exchange, first looking in properties, then in headers. Returns the result of defaultSupplier if not found.
   * @param exchange The exchange to look into.
   * @param key The key to look for.
   * @param defaultSupplier The supplier of the default value to return if not found.
   * @return The value found, or the result of defaultSupplier if not found.
   */
  public static @NotNull String findValue(@NotNull Exchange exchange, @NotNull String key, @NotNull Supplier<String> defaultSupplier) {
    return Objects.requireNonNullElseGet(findValue(exchange, key), defaultSupplier);
  }

  /**
   * Parse an Exchange into a Trace object, using the endpoint configuration and environment properties.
   * @param exchange The exchange to parse.
   * @param endpoint The endpoint configuration to use for parsing.
   * @return The parsed Trace object.
   */
  public static @NotNull Trace traceFromExchange(@NotNull Exchange exchange, @NotNull EfrTraceEndpoint endpoint) {
    TracesEnvironnement config = new TracesEnvironnement();

    Trace trace = new Trace()
        .state(endpoint.getLevel().toStatus())
        .environment(config.getEnvironment());

    // Headers and body
    trace.setMessage(new Message()
        .body(exchange.getIn().getBody(String.class))
        .id(UUID.randomUUID().toString())
        .created(OffsetDateTime.now())
        .level(endpoint.getLevel().toStatus())
        .headers(readHeaders(exchange, config.getMaxHeadersSize()))
    );

    // Route context
    trace.setRoute(new Route()
        .id(config.getRouteId())
        .name(config.getRouteName())
        .description(findValue(exchange, "description"))
        .step(endpoint.getStepOrProperty(exchange))
        .version(config.getRouteVersion())
    );

    // "Hardware" context
    trace.setInfrastructure(new Infrastructure()
        .datacenter(config.getRouteDatacenter())
        .hostname(exchange.getContext().resolveLanguage("simple")
            .createExpression("${hostname}")
            .evaluate(exchange, String.class))
        .instance(config.getApplicationName()));

    // Business references
    trace.setBusiness(readBusinessValues(exchange, config.getBusinessRule()));

    // Exception context
    trace.setException(readException(exchange, config));

    return trace;
  }

  private static @NotNull List<Key> readHeaders(@NotNull Exchange exchange, int sizeMax) {
    List<Key> headers = new ArrayList<>();
    for (var entry : exchange.getIn().getHeaders().entrySet()) {
      if(entry.getValue() == null) {
        headers.add(new Key().name(entry.getKey()));
        continue;
      }

      String value = entry.getValue().toString();
      if (value.length() > sizeMax)
        value = value.substring(0, sizeMax);
      headers.add(new Key().name(entry.getKey()).value(value));
    }
    return headers;
  }

  private static @NotNull List<Data> readBusinessValues(@NotNull Exchange exchange, @Nullable String businessRule) {
    List<Data> values = new ArrayList<>();
    if (businessRule == null || businessRule.isEmpty()) return values;

    // Each rule is on format "key=valueRef"
    for (String rule : businessRule.split(",")) {
      String[] parts = rule.split("=");
      if (parts.length != 2) continue;

      String key = parts[0];
      String valueRef = parts[1];

      // Try in properties first, in headers second. Escape the value to avoid JSON issues.
      String value = exchange.getProperty(valueRef, String.class);
      if (value == null)
        value = exchange.getIn().getHeader(valueRef, String.class);

      // Add data entry
      if (value != null) {
        values.add(new Data().name(key).value(value));
      }
    }

    return values;
  }

  private static @Nullable Exception readException(@NotNull Exchange exchange, @NotNull TracesEnvironnement config) {
    if (exchange.getProperty(Exchange.EXCEPTION_CAUGHT) == null)
      return null;
    var javaException = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, java.lang.Exception.class);

    // Stacktrace + exception class name
    int maxStackSize = config.getMaxStackTraceSize();
    String rawStack = ExceptionUtils.getStackTrace(javaException);
    if (rawStack.length() > maxStackSize)
      rawStack = rawStack.substring(0, maxStackSize - 3) + "...";
    Exception output = new Exception().stacktrace(rawStack)
        .when(OffsetDateTime.now())
        .className(javaException.getClass().getName());

    // Exception message (details)
    String exMessage = findValue(exchange, "exception-message", javaException::getMessage);
    output.setDetail(exMessage);

    // Exception code
    String exCode = findValue(exchange, "exception-code");
    output.setCode(exCode);
    overrideWithCustomErrorCode(output, config);
    if(output.getCode() == null) {
      String defaultCode = javaException.getClass().getSimpleName();
      LOG.warn("No custom error code found for exception. Using default code '{}'.", defaultCode);
      output.setCode(defaultCode);
    }

    return output;
  }

  private static void overrideWithCustomErrorCode(@NotNull Exception exception, @NotNull TracesEnvironnement config) {
    for(String property: config.getPropertyNames()) {
      // Only keep one specific format
      if(!property.matches("traces\\.custom-error-codes\\.(.+)")) continue;
      LOG.info("Found property: '{}'.", property);

      // Test regex
      String contentToTest = config.getValue(property);
      try {
        if (exception.getDetail().matches(contentToTest)) {
          String[] parts = property.split("\\.");
          String code = parts[parts.length - 1];
          exception.setCode(code);
          LOG.info("Property applied to route. Code is '{}'.", code);
          return;
        }
      } catch (PatternSyntaxException e) {
        LOG.warn("Invalid regex in property <{}>: {}. Regex is: <{}>.", property, e.getMessage(), contentToTest);
      }
    }
    // No custom error code.
  }

}
