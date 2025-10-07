package com.cn2g3.eventcrud2.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;

public class JsonUtils {

  public static <T> T mapToObject(String payload, Class<T> clazz) throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new Jdk8Module());
    mapper.enable(SerializationFeature.INDENT_OUTPUT);
    mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    return mapper.readValue(payload, clazz);
  }

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
