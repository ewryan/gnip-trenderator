package com.gnip;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

public class JSONUtils {
    private static ObjectMapper objectMapper = new ObjectMapper();

    public static JsonNode parseLine(String json) {
        try {
            return objectMapper.readTree(json);
        } catch (Exception e) {
            throw new RuntimeException("Error reading Json.  Line was: " + json + ".", e);
        }
    }

}
