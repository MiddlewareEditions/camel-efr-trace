package com.enterpriseflowsrepository.camel;

import lombok.Getter;
import lombok.Setter;
import org.apache.camel.Consumer;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.spi.UriEndpoint;
import org.apache.camel.spi.UriParam;
import org.apache.camel.support.DefaultEndpoint;
import org.jetbrains.annotations.NotNull;

/**
 * Defines the endpoint itself. It holds metadata and configuration.
 */
@UriEndpoint(firstVersion = "1.0.0", scheme = "efr-trace", title = "EFR Trace", syntax="efr-trace:status", producerOnly = true)
@Getter @Setter
public class EfrTraceEndpoint extends DefaultEndpoint {

  private final transient TraceLevel level;

  @UriParam(defaultValue = "true", description = "If true, the trace is marked as TECHNICAL. Otherwise, will be marked as BUSINESS.")
  private boolean technical;

  @UriParam(description = "Current step of operation.")
  private String step;

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
    throw new UnsupportedOperationException("efr-trace ne peut être utilisé qu'en sortie (Producer)");
  }

  public @NotNull String getStepOrProperty(@NotNull Exchange exchange) {
    if(step != null) return step;
    String inProps = exchange.getProperty("step", String.class);
    if(inProps != null) return inProps;
    return "Unspecified " + level;
  }
}
