package com.github.reugn.devtools.services;

import com.github.reugn.devtools.utils.LogFaker;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.function.Supplier;
import java.util.stream.Stream;

public final class LogsService {

    private static final LogFaker faker = new LogFaker();
    private static final String apacheFormat = "%s - %s [%s] \"%s %s %s\" %d %d \"%s\" \"%s\"";
    private static final String log4jFormat = "%s [%s] %s %s - %s";
    private static final String RFC3164Format = "<%d>%s %s %s[%d]: %s";
    private static final String RFC5424Format = "<%d>%d %s %s %s %d ID%d %s %s";

    private LogsService() {
    }

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
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return func.get();
                });
    }

    private static String apacheLog() {
        return String.format(
                apacheFormat,
                faker.iPv4Address(),
                faker.randInt(100, 10000),
                time("HH:mm:ss.SSS"),
                faker.httpMethod(),
                faker.url(),
                faker.httpVersion(),
                faker.statusCode(),
                faker.randInt(100, 100000),
                faker.domain(),
                faker.userAgent()
        );
    }

    private static String log4JLog() {
        return String.format(
                log4jFormat,
                time("HH:mm:ss.SSS"),
                faker.threadName(),
                faker.logLevel(),
                faker.domain() + ".Application",
                faker.message(128)
        );
    }

    private static String rfc3164Log() {
        return String.format(
                RFC3164Format,
                faker.randInt(0, 191),
                time("yyyy-MM-dd'T'HH:mm:ss'Z'"),
                faker.username(),
                "evntslog",
                faker.randInt(1, 10000),
                faker.message(32)
        );
    }

    private static String rfc5424Log() {
        return String.format(
                RFC5424Format,
                faker.randInt(0, 191),
                faker.randInt(1, 3),
                time("yyyy-MM-dd'T'HH:mm:ss'Z'"),
                faker.domain(),
                "evntslog",
                faker.randInt(1, 10000),
                faker.randInt(1, 1000),
                "-",
                faker.message(32)
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
                return LogsService::apacheLog;
            case "Log4j Default":
                return LogsService::log4JLog;
            case "RFC3164":
                return LogsService::rfc3164Log;
            case "RFC5424":
                return LogsService::rfc5424Log;
        }
        return LogsService::apacheLog;
    }
}
