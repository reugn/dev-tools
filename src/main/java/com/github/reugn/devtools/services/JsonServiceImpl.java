package com.github.reugn.devtools.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;

public final class JsonServiceImpl implements JsonService {

    private static final ObjectMapper mapper = new ObjectMapper();
    private static final ObjectMapper formatMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    @Override
    public String clearSpaces(String json) throws IOException {
        return mapper.readTree(json).toString();
    }

    @Override
    public String format(String json) throws IOException {
        return formatMapper.writerWithDefaultPrettyPrinter().writeValueAsString(mapper.readValue(json, Object.class));
    }
}
