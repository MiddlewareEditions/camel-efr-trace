package com.enterpriseflowsrepository.camel.clients.bean;

import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

@Data
public class PartInfrastructure implements JSONConvertible {

  private String hostname;
  private String datacenter;
  private String instance = "unknown";

  @Override
  public @NotNull JSONObject toJSON() {
    JSONObject json = new JSONObject();
    if(hostname != null)
      json.put("hostname", hostname);
    if(datacenter != null)
      json.put("datacenter", datacenter);
    json.put("instance", instance);
    return json;
  }
}
