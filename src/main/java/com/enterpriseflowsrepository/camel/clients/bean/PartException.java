package com.enterpriseflowsrepository.camel.clients.bean;

import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.time.OffsetDateTime;

@Data
public class PartException implements JSONConvertible {

  private String code = "unknown";
  private String detail;
  private String stackTrace;
  private OffsetDateTime when;
  private String className;

  @Override
  public @NotNull JSONObject toJSON() {
    JSONObject json = new JSONObject();
    json.put("code", code);
    if(detail != null)
      json.put("detail", detail);
    if(stackTrace != null)
      json.put("stacktrace", stackTrace);
    if(when != null)
      json.put("when", when.toString());
    if(className != null)
      json.put("className", className);
    return json;
  }
}
