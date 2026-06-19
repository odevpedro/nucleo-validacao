package com.empresa.nucleovalidacao.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonUtils {

    private static final Logger log = LoggerFactory.getLogger(JsonUtils.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private JsonUtils() {
    }

    public static String toJson(Object value) {
        if (value == null) return null;
        if (value instanceof String s) return s;
        try {
            return MAPPER.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            log.warn("Erro ao converter para JSON: {}", e.getMessage());
            return value.toString();
        }
    }

    @SuppressWarnings("unchecked")
    public static Object parseJson(String json) {
        if (json == null || json.isBlank()) return null;
        try {
            return MAPPER.readValue(json, Object.class);
        } catch (JsonProcessingException e) {
            log.warn("Erro ao parsear JSON: {}", e.getMessage());
            return json;
        }
    }
}
