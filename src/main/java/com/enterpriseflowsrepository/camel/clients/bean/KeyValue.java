package com.enterpriseflowsrepository.camel.clients.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KeyValue implements JSONConvertible {

  private String name;
  private String value;

  public KeyValue(String name) {
    this.name = name;
  }

  @Override
  public @NotNull JSONObject toJSON() {
    return new JSONObject().put("name", name).put("value", value);
  }
}
