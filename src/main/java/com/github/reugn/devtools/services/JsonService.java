package com.github.reugn.devtools.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;

public final class JsonService {

    private JsonService() {
    }

    public static String clearSpaces(String json) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readTree(json).toString();
    }

    public static String format(String json) throws IOException {
        ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(mapper.readValue(json, Object.class));
    }
}
