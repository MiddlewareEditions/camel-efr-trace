package com.enterpriseflowsrepository.camel.traces;

import com.enterpriseflowsrepository.camel.EfrTraceEndpoint;
import com.enterpriseflowsrepository.camel.clients.bean.KeyValue;
import com.enterpriseflowsrepository.camel.clients.bean.PartException;
import com.enterpriseflowsrepository.camel.clients.bean.Trace;
import org.apache.camel.Exchange;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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

    Trace trace = new Trace();
    trace.setState(endpoint.getLevel());
    trace.setEnvironment(config.getEnvironment());

    // Headers and body
    trace.getMessage().setBody(exchange.getIn().getBody(String.class));
    readHeaders(exchange, config.getMaxHeadersSize()).forEach(h -> trace.getMessage().getHeaders().add(h));

    // Route context
    trace.getRoute().setId(config.getRouteId());
    trace.getRoute().setName(config.getRouteName());
    trace.getRoute().setDescription(findValue(exchange, "description"));
    trace.getRoute().setStep(endpoint.getStepOrProperty(exchange));
    trace.getRoute().setVersion(config.getRouteVersion());

    // "Hardware" context
    trace.getInfrastructure().setDatacenter(config.getRouteDatacenter());
    trace.getInfrastructure().setInstance(config.getApplicationName());
    trace.getInfrastructure().setHostname(exchange.getContext().resolveLanguage("simple")
        .createExpression("${hostname}")
        .evaluate(exchange, String.class));

    // Business references
    readBusinessValues(exchange, config.getBusinessRule()).forEach(trace.getBusiness()::add);

    // Exception context
    trace.setException(readException(exchange, config));

    return trace;
  }

  private static @NotNull List<KeyValue> readHeaders(@NotNull Exchange exchange, int sizeMax) {
    List<KeyValue> headers = new ArrayList<>();
    for (var entry : exchange.getIn().getHeaders().entrySet()) {
      if(entry.getValue() == null) {
        headers.add(new KeyValue(entry.getKey()));
        continue;
      }

      String value = entry.getValue().toString();
      if (value.length() > sizeMax)
        value = value.substring(0, sizeMax);
      headers.add(new KeyValue(entry.getKey(), value));
    }
    return headers;
  }

  private static @NotNull List<KeyValue> readBusinessValues(@NotNull Exchange exchange, @Nullable String businessRule) {
    List<KeyValue> values = new ArrayList<>();
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
        values.add(new KeyValue(key, value));
      }
    }

    return values;
  }

  private static @Nullable PartException readException(@NotNull Exchange exchange, @NotNull TracesEnvironnement config) {
    if (exchange.getProperty(Exchange.EXCEPTION_CAUGHT) == null)
      return null;
    var javaException = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, java.lang.Exception.class);

    // Stacktrace + exception class name
    int maxStackSize = config.getMaxStackTraceSize();
    String rawStack = getStackTrace(javaException);
    if (rawStack.length() > maxStackSize)
      rawStack = rawStack.substring(0, maxStackSize - 3) + "...";
    PartException output = new PartException();
    output.setStackTrace(rawStack);
    output.setWhen(OffsetDateTime.now());
    output.setClassName(javaException.getClass().getName());

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

  private static void overrideWithCustomErrorCode(@NotNull PartException exception, @NotNull TracesEnvironnement config) {
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

  /**
   * From {@code org.apache.commons.lang3.exception.ExceptionUtils}.
   * @param throwable throwable to get the stack trace from, may be null.
   * @return the stack trace as a String, empty string if throwable is null.
   */
  private static @NotNull String getStackTrace(@Nullable Throwable throwable) {
    if (throwable == null)
      return "";
    StringWriter sw = new StringWriter();
    throwable.printStackTrace(new PrintWriter(sw, true));
    return sw.toString();
  }
}
