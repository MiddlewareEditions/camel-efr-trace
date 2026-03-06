package com.enterpriseflowsrepository.camel.clients.bean;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Collection;

public interface JSONConvertible {

  @NotNull JSONObject toJSON();

  static @NotNull JSONArray toArray(@NotNull Collection<? extends JSONConvertible> list) {
    JSONArray array = new JSONArray();
    for (JSONConvertible item : list) {
      array.put(item.toJSON());
    }
    return array;
  }

}
