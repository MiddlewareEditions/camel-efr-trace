package com.enterpriseflowsrepository.camel;

import org.apache.camel.Endpoint;
import org.apache.camel.spi.annotations.Component;
import org.apache.camel.support.DefaultComponent;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * The <b>component</b> represents the URI scheme.
 */
@Component("efr-trace") // URI prefix
public class EfrTraceComponent extends DefaultComponent {

  @Override
  protected @NotNull Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) throws Exception {
    // Validate the trace level
    TraceLevel level = TraceLevel.fromString(remaining);

    // New endpoint.
    EfrTraceEndpoint endpoint = new EfrTraceEndpoint(uri, this, level);

    // Injects parameters (for instance "?param=value") into the endpoint.
    setProperties(endpoint, parameters);

    return endpoint;
  }
}
