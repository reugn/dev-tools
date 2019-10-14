package com.github.reugn.devtools.services;

import com.github.reugn.devtools.models.RestResponse;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RestService {

    private RestService() {
    }

    private static int defaultConnectTimeout = 60000;

    public static RestResponse request(String requestMethod, String uri, Map<String, String> headers,
                                       String body) throws Exception {
        HttpURLConnection conn = (HttpURLConnection) buildURL(uri).openConnection();
        conn.setRequestMethod(requestMethod);
        for (Map.Entry<String, String> h : headers.entrySet()) {
            conn.setRequestProperty(h.getKey(), h.getValue());
        }
        conn.setConnectTimeout(defaultConnectTimeout);
        conn.setInstanceFollowRedirects(false);

        String responseBody = "";
        int status;
        long t1 = System.currentTimeMillis();
        if (!body.isEmpty()) {
            conn.setDoOutput(true);
            setRequestBody(conn.getOutputStream(), body);
        }
        try {
            status = conn.getResponseCode();
            responseBody = getResponseBody(conn.getInputStream());
        } catch (IOException e) {
            status = conn.getResponseCode();
            responseBody = getResponseBody(conn.getErrorStream());
        }
        long t2 = System.currentTimeMillis() - t1;

        String responseHeaders = getResponseHeaders(conn.getHeaderFields());
        conn.disconnect();
        return new RestResponse(status, responseBody, responseHeaders, t2);
    }

    private static void setRequestBody(OutputStream os, String body) throws IOException {
        OutputStreamWriter outStreamWriter = new OutputStreamWriter(os, StandardCharsets.UTF_8);
        outStreamWriter.write(body);
        outStreamWriter.flush();
        outStreamWriter.close();
        os.close();
    }

    private static String getResponseBody(InputStream is) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(is));
        String line;
        StringBuilder buff = new StringBuilder();
        while ((line = in.readLine()) != null) {
            buff.append(line);
        }
        in.close();
        return buff.toString();
    }

    private static String getResponseHeaders(Map<String, List<String>> fields) {
        return fields.entrySet().stream()
                .map(e -> {
                    if (e.getKey() == null)
                        return String.join(", ", e.getValue());
                    else
                        return e.getKey() + ": " + String.join(", ", e.getValue());
                })
                .collect(Collectors.joining("\n"));
    }

    private static URL buildURL(String uri) throws MalformedURLException {
        URL url;
        try {
            url = new URL(uri);
        } catch (MalformedURLException e) {
            if (uri.startsWith("http"))
                throw e;
            return buildURL("http://" + uri);
        }
        return url;
    }
}
