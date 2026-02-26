package com.enterpriseflowsrepository.camel;

import org.apache.camel.Exchange;
import org.apache.camel.support.DefaultProducer;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Producer, implementing the logic of the {@link EfrTraceEndpoint endpoint}.<br/>
 * It will actually perform the trace-send action.
 */
public class EfrTraceProducer extends DefaultProducer {

  private static final Logger LOG = LoggerFactory.getLogger(EfrTraceProducer.class);
  private final EfrTraceEndpoint endpoint;

  public EfrTraceProducer(EfrTraceEndpoint endpoint) {
    super(endpoint);
    this.endpoint = endpoint;
  }

  @Override
  public void process(@NotNull Exchange exchange) throws Exception {
    String status = endpoint.getStatus();
    String body = exchange.getIn().getBody(String.class);

    // Ton comportement métier personnalisé
    String traceMessage = String.format("[EFR-TRACE - %s] - Message: %s", status.toUpperCase(), body);

    //TODO implement a real sending...

    switch (endpoint.getLevel()) {
      case INFO -> LOG.info(traceMessage);
      case WARNING -> LOG.warn(traceMessage);
      case ERROR -> LOG.error(traceMessage);
      case SUCCESS -> LOG.info("[SUCCESS] {}", traceMessage);
    }
  }
}