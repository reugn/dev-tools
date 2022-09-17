package com.github.reugn.devtools.models;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpRequest;
import java.time.Duration;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

public class Request {

    private Map<String, String> headers;
    private String method;
    private String url;
    private String body;
    private String datetime;

    private Request() {
    }

    public Request(String url, String method, Map<String, String> headers, String body) {
        this.method = method;
        this.url = url;
        this.headers = headers;
        this.body = body;
        this.datetime = new Date().toString();
    }

    public HttpRequest toHTTPRequest() {
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(buildURI())
                .method(method, HttpRequest.BodyPublishers.ofString(body))
                .timeout(Duration.ofSeconds(30L));
        if (headers.isEmpty()) {
            return requestBuilder.build();
        }
        return requestBuilder
                .headers(headers.entrySet().stream().flatMap(h ->
                        Stream.of(h.getKey(), h.getValue())).toArray(String[]::new))
                .build();
    }

    private URI buildURI() {
        try {
            return new URI(url.startsWith("http") ? url : "http://" + url);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getMethod() {
        return method;
    }

    public String getBody() {
        return body;
    }

    public String getUrl() {
        return url;
    }

    public String getDatetime() {
        return datetime;
    }

    @Override
    public String toString() {
        return method + " - " + url;
    }

    @Override
    public int hashCode() {
        return Objects.hash(url, method, body, headers);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Request req = (Request) obj;
        return Objects.equals(url, req.url) &&
                Objects.equals(method, req.method) &&
                Objects.equals(body, req.body) &&
                Objects.equals(headers, req.headers);
    }
}
