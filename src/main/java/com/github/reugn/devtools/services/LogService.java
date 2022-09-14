package com.github.reugn.devtools.services;

import java.util.stream.Stream;

public interface LogService {

    Stream<String> logStream(String type, int delayBottom, int delayTop, int limit);
}
