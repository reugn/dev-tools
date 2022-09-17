package com.github.reugn.devtools.services;

import java.io.IOException;

public interface JsonService {

    String clearSpaces(String json) throws IOException;

    String format(String json) throws IOException;
}
