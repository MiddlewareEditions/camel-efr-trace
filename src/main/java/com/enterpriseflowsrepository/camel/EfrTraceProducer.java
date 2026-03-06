package com.enterpriseflowsrepository.camel;

import com.enterpriseflowsrepository.camel.clients.TracesClient;
import com.enterpriseflowsrepository.camel.clients.bean.Trace;
import com.enterpriseflowsrepository.camel.traces.TracesHelper;
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

  private final TracesClient client;

  /**
   * Create a new EfrTraceProducer instance with the given endpoint. <br/>
   * The producer will use the endpoint configuration to create traces and send them to EFR.
   * @param endpoint The endpoint associated with this producer. Must not be null.
   */
  public EfrTraceProducer(EfrTraceEndpoint endpoint) {
    super(endpoint);
    this.endpoint = endpoint;
    this.client = TracesClient.initialize();
  }

  @Override
  public void process(@NotNull Exchange exchange) {
    LOG.info("Sending trace to EFR with status {}.", endpoint.getLevel());

    // Create a trace
    Trace trace = TracesHelper.traceFromExchange(exchange, endpoint);

    //TODO accumulate traces before sending them.

    // Send to EFR
    client.sendTrace(trace);
  }
}