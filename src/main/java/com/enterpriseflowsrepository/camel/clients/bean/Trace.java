package com.enterpriseflowsrepository.camel.clients.bean;

import com.enterpriseflowsrepository.camel.TraceLevel;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

@Data
public class Trace implements JSONConvertible {

  private String environment;
  private TraceLevel state;

  private final PartMessage message = new PartMessage();
  private final PartRoute route = new PartRoute();
  private final PartInfrastructure infrastructure = new PartInfrastructure();
  private PartException exception;
  private final List<KeyValue> business = new ArrayList<>();

  @Override
  public @NotNull JSONObject toJSON() {
    JSONObject json = new JSONObject();
    json.put("environment", environment);
    json.put("state", state.toStatus());
    json.put("message", message.toJSON());
    json.put("route", route.toJSON());
    json.put("infrastructure", infrastructure.toJSON());
    if(exception != null) {
      json.put("exception", exception.toJSON());
    }
    json.put("business", JSONConvertible.toArray(business));
    return json;
  }
}
