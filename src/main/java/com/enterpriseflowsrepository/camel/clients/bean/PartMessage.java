package com.enterpriseflowsrepository.camel.clients.bean;

import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Class representing a {@code message} part of a trace.
 */
@Data
public class PartMessage implements JSONConvertible {

  private OffsetDateTime created = OffsetDateTime.now();
  private boolean technical = true;
  private String body = "";
  private String id = UUID.randomUUID().toString();
  private String correlationId;
  private final List<KeyValue> headers = new ArrayList<>();

  @Override
  public @NotNull JSONObject toJSON() {
    JSONObject json = new JSONObject();
    json.put("created", created.toString());
    json.put("id", id);
    json.put("type", technical ? "technical" : "business");
    if(correlationId != null)
      json.put("correlationId", correlationId);
    if(body != null)
      json.put("body", body);
    json.put("headers", JSONConvertible.toArray(headers));
    return json;
  }
}
