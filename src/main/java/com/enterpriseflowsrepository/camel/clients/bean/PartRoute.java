package com.enterpriseflowsrepository.camel.clients.bean;

import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

@Data
public class PartRoute implements JSONConvertible {

  private String name;
  private String version;
  private String id;
  private String step;
  private String description;

  @Override
  public @NotNull JSONObject toJSON() {
    JSONObject json = new JSONObject();
    if(name != null)
      json.put("name", name);
    if(version != null)
      json.put("version", version);
    if(id != null)
      json.put("id", id);
    if(step != null)
      json.put("step", step);
    if(description != null)
      json.put("description", description);
    return json;
  }
}
