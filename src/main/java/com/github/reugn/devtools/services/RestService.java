package com.github.reugn.devtools.services;

import com.github.reugn.devtools.controllers.RestAPIController;
import com.github.reugn.devtools.models.Request;
import com.github.reugn.devtools.models.RestResponse;
import javafx.application.Platform;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class RestService {

    public static final List<Request> REQ_HISTORY_LIST = new LinkedList<>();
    private static final Logger log = LogManager.getLogger(RestService.class);
    private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor();
    private static final int defaultConnectTimeout = 60000;
    private static RestAPIController CONTROLLER;

    private RestService() {
    }

    public static void addToHistoryReqList(List<Request> reqs) {
        REQ_HISTORY_LIST.addAll(reqs);
    }

    private static RestResponse request(Request request) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) buildURL(request.getUrl()).openConnection();
        conn.setRequestMethod(request.getMethod());
        for (Map.Entry<String, String> header : request.getHeaders().entrySet()) {
            conn.setRequestProperty(header.getKey(), header.getValue());
        }
        conn.setConnectTimeout(defaultConnectTimeout);
        conn.setInstanceFollowRedirects(false);

        String responseBody;
        int status;
        Instant start = Instant.now();
        if (!request.getBody().isEmpty()) {
            conn.setDoOutput(true);
            setRequestBody(conn.getOutputStream(), request.getBody());
        }
        try {
            status = conn.getResponseCode();
            responseBody = getResponseBody(conn.getInputStream());
        } catch (IOException e) {
            status = conn.getResponseCode();
            responseBody = getResponseBody(conn.getErrorStream());
        }
        Instant finish = Instant.now();
        long time = Duration.between(start, finish).toMillis();

        String responseHeaders = getResponseHeaders(conn.getHeaderFields());
        conn.disconnect();

        addToHistory(request);
        return new RestResponse(status, responseBody, responseHeaders, time);
    }

    private static void addToHistory(Request request) {
        if (REQ_HISTORY_LIST.stream().filter(r -> r.equals(request)).findFirst().isEmpty()) {
            REQ_HISTORY_LIST.add(request);
            Platform.runLater(() -> CONTROLLER.getHistoryListView().getItems().add(request));
        }
    }

    public static void requestAsync(Request request, ResponseRunnable onComplete, ExceptionRunnable onError) {
        EXECUTOR.execute(() -> {
            try {
                RestResponse response = RestService.request(request);
                if (onComplete != null)
                    onComplete.run(response);
            } catch (Exception e) {
                log.error("HTTP request failed", e);
                if (onError != null)
                    onError.run(e);
            }
        });
    }

    public static List<Request> getReqHistory() {
        return REQ_HISTORY_LIST;
    }

    public static void registerController(RestAPIController controller) {
        CONTROLLER = controller;
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
        StringBuilder builder = new StringBuilder();
        while ((line = in.readLine()) != null) {
            builder.append(line);
        }
        in.close();
        return builder.toString();
    }

    private static String getResponseHeaders(Map<String, List<String>> fields) {
        return fields.entrySet().stream()
                .map(e -> {
                    if (e.getKey() == null) {
                        return String.join(", ", e.getValue());
                    }
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
