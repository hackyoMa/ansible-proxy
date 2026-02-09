package com.github.ansibleproxy.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;

/**
 * Json
 *
 * @author hackyo
 * @since 1.0.0
 */
@Component
public class Json {

    public static JsonMapper jsonMapper;

    @Autowired
    public Json(JsonMapper jsonMapper) {
        Json.jsonMapper = jsonMapper;
    }

    public static <T> T parseObject(String json, Class<T> clazz) {
        return jsonMapper.readValue(json, clazz);
    }

    public static <T> List<T> parseArray(String json, Class<T> clazz) {
        return jsonMapper.readValue(json, jsonMapper.getTypeFactory().constructCollectionType(List.class, clazz));
    }

    public static String toJsonString(Object object) {
        return jsonMapper.writeValueAsString(object);
    }

}
