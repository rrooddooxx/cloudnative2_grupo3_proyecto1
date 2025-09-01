package com.cn2g3.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtils {

  private JsonUtils() {}

  public static <T> String mapToJson(T obj) {
    String result = "";
    try {
      ObjectMapper mapper = new ObjectMapper();
      result = mapper.writeValueAsString(obj);
    } catch (Exception ex) {
      System.out.println("JsonUtils | Error serializing to JSON");
    }
    return result;
  }
}
