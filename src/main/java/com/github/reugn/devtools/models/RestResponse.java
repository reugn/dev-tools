package com.github.reugn.devtools.models;

public class RestResponse {
    private int status;
    private String body;
    private String headers;
    private long time;

    public RestResponse(int status, String body, String headers, long time) {
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
