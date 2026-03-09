package com.enterpriseflowsrepository.camel.clients.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

/**
 * Simple key-value pair class that can be converted to JSON.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class KeyValue implements JSONConvertible {

  private String name;
  private String value;

  /**
   * Constructor with only the name (value can be set later).
   * @param name The name of the key-value pair. Must not be null.
   */
  public KeyValue(String name) {
    this.name = name;
  }

  @Override
  public @NotNull JSONObject toJSON() {
    return new JSONObject().put("name", name).put("value", value);
  }
}
