package com.enterpriseflowsrepository.camel.traces;

import com.enterpriseflowsrepository.api.traces.beans.Message;
import com.enterpriseflowsrepository.api.traces.beans.Trace;
import com.enterpriseflowsrepository.camel.TraceLevel;
import org.apache.camel.Exchange;
import org.eclipse.microprofile.config.ConfigProvider;
import org.jetbrains.annotations.NotNull;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Utility class to parse traces.
 */
public final class TracesHelper {
  private TracesHelper() {}

  public static @NotNull Trace traceFromExchange(@NotNull Exchange exchange, @NotNull TraceLevel status) {
    var config = ConfigProvider.getConfig();

    Trace trace = new Trace()
        .state(status.toStatus());

    //TODO ...

    trace.setMessage(new Message()
        .body(exchange.getIn().getBody(String.class))
        .id(UUID.randomUUID().toString())
        .created(OffsetDateTime.now())
        .level(status.toStatus())
    );

    return trace;
  }

}
