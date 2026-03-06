package com.enterpriseflowsrepository.camel;

import lombok.Getter;
import lombok.Setter;
import org.apache.camel.Category;
import org.apache.camel.Consumer;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.spi.UriEndpoint;
import org.apache.camel.spi.UriParam;
import org.apache.camel.spi.UriPath;
import org.apache.camel.support.DefaultEndpoint;
import org.jetbrains.annotations.NotNull;

/**
 * Defines the endpoint itself. It holds metadata and configuration.
 */
@UriEndpoint(
    firstVersion = "0.0.1",
    scheme = "efr-trace",
    title = "EFR Trace",
    syntax="efr-trace:status",
    category = {Category.CLOUD, Category.MONITORING},
    producerOnly = true
)
@Getter @Setter
public class EfrTraceEndpoint extends DefaultEndpoint {

  @UriPath(name = "status", description = "Le niveau de trace souhaité (ex: INFO, ERROR)")
  private TraceLevel level;

  @UriParam(defaultValue = "true", description = "If true, the trace is marked as TECHNICAL. Otherwise, will be marked as BUSINESS.")
  private boolean technical = true;

  @UriParam(description = "Current step of operation.")
  private String step;

  /**
   * Create a new {@link EfrTraceEndpoint} instance.
   * @param uri the URI of the endpoint, as defined in the Camel route. Must not be null.
   * @param component the component associated with this endpoint. Must not be null.
   * @param level the level of the trace to be sent. Must not be null.
   */
  public EfrTraceEndpoint(@NotNull String uri, @NotNull EfrTraceComponent component, @NotNull TraceLevel level) {
    super(uri, component);
    this.level = level;
  }

  @Override
  public @NotNull EfrTraceProducer createProducer() {
    return new EfrTraceProducer(this);
  }

  @Override
  public Consumer createConsumer(Processor processor) {
    throw new UnsupportedOperationException("The 'efr-trace' can only be used as output (Producer)");
  }

  /**
   * Get the step of the trace, either from the endpoint configuration or from the exchange properties. If both are null, return a default value.
   * @param exchange The exchange from which to get the step property if not defined in the endpoint. Must not be null.
   * @return The step of the trace, as defined in the endpoint or in the exchange properties, or a default value if both are null.
   */
  public @NotNull String getStepOrProperty(@NotNull Exchange exchange) {
    if(step != null) return step;
    String inProps = exchange.getProperty("step", String.class);
    if(inProps != null) return inProps;
    return "Unspecified " + level;
  }
}
