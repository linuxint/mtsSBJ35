package com.devkbil.mtssbj.common.security;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

public class XssSanitizer extends JsonDeserializer<String> {

    @Override
    public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String value = p.getValueAsString();
        if (value == null) return null;

        return sanitize(value);
    }

    private String sanitize(String input) {
        // XSS 제거 로직
        return input.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
    }
}