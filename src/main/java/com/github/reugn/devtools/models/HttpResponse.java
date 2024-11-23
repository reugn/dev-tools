package com.github.reugn.devtools.models;

public class HttpResponse {

    private final int status;
    private final String body;
    private final String headers;
    private final long time;

    public HttpResponse(int status, String body, String headers, long time) {
        this.status = status;
        this.body = body;
        this.headers = headers;
        this.time = time;
    }

    public int getStatus() {
        return status;
    }

    public String getBody() {
        return body;
    }

    public String getHeaders() {
        return headers;
    }

    public long getTime() {
        return time;
    }
}
