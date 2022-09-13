package com.github.reugn.devtools.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;

public final class JsonService {

    private static final ObjectMapper mapper = new ObjectMapper();
    private static final ObjectMapper formatMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    private JsonService() {
    }

    public static String clearSpaces(String json) throws IOException {
        return mapper.readTree(json).toString();
    }

    public static String format(String json) throws IOException {
        return formatMapper.writerWithDefaultPrettyPrinter().writeValueAsString(mapper.readValue(json, Object.class));
    }
}
