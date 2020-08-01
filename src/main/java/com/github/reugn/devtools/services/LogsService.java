package com.github.reugn.devtools.services;

import com.github.reugn.devtools.utils.LogFaker;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.function.Supplier;
import java.util.stream.Stream;

public final class LogsService {

    private LogsService() {
    }

    private static final LogFaker faker = new LogFaker();

    private static final String apacheFormat = "%s - %s [%s] \"%s %s %s\" %d %d \"%s\" \"%s\"";
    private static final String log4jFormat = "%s [%s] %s %s - %s";
    private static final String RFC3164Format = "<%d>%s %s %s[%d]: %s";
    private static final String RFC5424Format = "<%d>%d %s %s %s %d ID%d %s %s";

    public static Stream<String> logStream(String type,
                                           int delayBottom,
                                           int delayTop,
                                           int limit) {
        Supplier<String> func = getFormatByName(type);
        return Stream.iterate(0, i -> i + 1)
                .limit(limit > 0 ? limit : Integer.MAX_VALUE)
                .map(i -> {
                    long sleepTime = faker.randInt(delayBottom, delayTop);
                    try {
                        Thread.sleep(sleepTime);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return func.get();
                });
    }

    private static String ApacheLog() {
        return String.format(
                apacheFormat,
                faker.IPv4Address(),
                faker.randInt(100, 10000),
                time("HH:mm:ss.SSS"),
                faker.HTTPMethod(),
                faker.URL(),
                faker.HTTPVersion(),
                faker.StatusCode(),
                faker.randInt(100, 100000),
                faker.Domain(),
                faker.UserAgent()
        );
    }

    private static String Log4jLog() {
        return String.format(
                log4jFormat,
                time("HH:mm:ss.SSS"),
                faker.ThreadName(),
                faker.LogLevel(),
                faker.Domain() + ".Application",
                faker.Message(128)
        );
    }

    private static String RFC3164Log() {
        return String.format(
                RFC3164Format,
                faker.randInt(0, 191),
                time("yyyy-MM-dd'T'HH:mm:ss'Z'"),
                faker.Username(),
                "evntslog",
                faker.randInt(1, 10000),
                faker.Message(32)
        );
    }

    private static String RFC5424Log() {
        return String.format(
                RFC5424Format,
                faker.randInt(0, 191),
                faker.randInt(1, 3),
                time("yyyy-MM-dd'T'HH:mm:ss'Z'"),
                faker.Domain(),
                "evntslog",
                faker.randInt(1, 10000),
                faker.randInt(1, 1000),
                "-",
                faker.Message(32)
        );
    }

    private static String time(String pattern) {
        DateFormat formatter = new SimpleDateFormat(pattern);
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        return formatter.format(new Date());
    }

    private static Supplier<String> getFormatByName(String name) {
        switch (name) {
            case "Apache Common":
                return LogsService::ApacheLog;
            case "Log4j Default":
                return LogsService::Log4jLog;
            case "RFC3164":
                return LogsService::RFC3164Log;
            case "RFC5424":
                return LogsService::RFC5424Log;
        }
        return LogsService::ApacheLog;
    }
}
