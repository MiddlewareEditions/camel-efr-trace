package com.enterpriseflowsrepository.camel.clients.bean;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Collection;

/**
 * Interface for objects that can be converted to JSON.
 */
public interface JSONConvertible {

  /**
   * Convert this object to a JSON representation.
   * @return A JSONObject representing this object. Must not be null.
   */
  @NotNull JSONObject toJSON();

  /**
   * Convert a list of JSONConvertible items to a JSONArray.
   * @param list The list of items to convert. Must not be null.
   * @return A JSONArray containing the JSON representation of each item in the list.
   */
  static @NotNull JSONArray toArray(@NotNull Collection<? extends JSONConvertible> list) {
    JSONArray array = new JSONArray();
    for (JSONConvertible item : list) {
      array.put(item.toJSON());
    }
    return array;
  }

}
