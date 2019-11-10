package com.github.reugn.devtools.utils;

import com.google.common.net.InetAddresses;

import java.util.Random;

public class LogFaker {

    private static final String[] httpMethods = {"GET", "HEAD", "POST", "PUT", "DELETE", "PATCH"};

    private static final String[] httpVersions = {"HTTP/1.0", "HTTP/1.1", "HTTP/2.0"};

    private static final String[] UserAgents = {
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.169 Safari/537.36",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/13.0.2 Safari/605.1.15",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/77.0.3865.90 Safari/537.36",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.102 Safari/537.36 Edge/18.18362",
            "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:70.0) Gecko/20100101 Firefox/70.0",
            "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/77.0.3865.90 Safari/537.36",
            "Mozilla/5.0 (Linux; U; Android 4.3; en-us; SM-N900T Build/JSS15J) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30"
    };

    private static final String[] urls = {
            "/products/",
            "/services/",
            "/about_us.html",
            "/contact_us.html",
    };

    private static final String[] usernames = {
            "admin",
            "support",
            "user1",
            "user",
    };

    private static final String[] logLevels = {
            "TRACE",
            "DEBUG",
            "INFO",
            "WARN",
            "ERROR",
    };

    private static final String[] domainExtensions = {
            "com",
            "edu",
            "net",
            "org",
    };

    private PasswordGenerator generator = new PasswordGenerator.PasswordGeneratorBuilder()
            .withLowerChars(true).build();

    private Random rand = new Random();

    public String IPv4Address() {
        return InetAddresses.fromInteger(rand.nextInt()).getHostAddress();
    }

    public String HTTPMethod() {
        return getRandom(httpMethods);
    }

    public String HTTPVersion() {
        return getRandom(httpVersions);
    }

    public int StatusCode() {
        int result = 200;
        int r = rand.nextInt(10);
        if (r > 5 && r <= 7) {
            result = 404;
        } else if (r > 7) {
            result = randInt(201, 599);
        }
        return result;
    }

    public String Domain() {
        int subDomainLength = rand.nextInt(12);
        int domainLength = rand.nextInt(12);
        StringBuilder buff = new StringBuilder();
        if (subDomainLength > 0) buff.append(generator.generate(subDomainLength)).append(".");
        if (domainLength > 0) buff.append(generator.generate(domainLength)).append(".");
        buff.append(getRandom(domainExtensions));
        return buff.toString();
    }

    public String Message(int len) {
        return generator.generate(rand.nextInt(len));
    }

    public String ThreadName() {
        int next = rand.nextInt(12);
        if (next < 8) {
            return "Thread-" + next;
        }
        return "main";
    }

    public int randInt(int from, int to) {
        if (from >= to) throw new IllegalArgumentException("from > to");
        return rand.nextInt((to - from) + 1) + from;
    }

    public String URL() {
        return getRandom(urls);
    }

    public String UserAgent() {
        return getRandom(UserAgents);
    }

    public String Username() {
        return getRandom(usernames);
    }

    public String LogLevel() {
        return getRandom(logLevels);
    }

    private String getRandom(String[] list) {
        return list[rand.nextInt(list.length)];
    }
}
