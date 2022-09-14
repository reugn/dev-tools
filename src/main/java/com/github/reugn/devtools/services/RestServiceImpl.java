package com.github.reugn.devtools.services;

import com.github.reugn.devtools.async.ExceptionRunnable;
import com.github.reugn.devtools.async.ResponseRunnable;
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

public class RestServiceImpl implements RestService {

    private static final Logger log = LogManager.getLogger(RestServiceImpl.class);
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();

    private final List<Request> requestHistoryList = new LinkedList<>();
    private final int defaultConnectTimeout = 60000;
    private RestAPIController restAPIController;

    @Override
    public void addToRequestHistory(List<Request> requests) {
        requestHistoryList.addAll(requests);
    }

    @Override
    public void removeFromRequestHistory(Request request) {
        requestHistoryList.remove(request);
    }

    @Override
    public void clearRequestHistory() {
        requestHistoryList.clear();
    }

    @Override
    public List<Request> getRequestHistory() {
        return requestHistoryList;
    }

    private RestResponse request(Request request) throws IOException {
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

    private void addToHistory(Request request) {
        if (requestHistoryList.stream().filter(r -> r.equals(request)).findFirst().isEmpty()) {
            requestHistoryList.add(request);
            Platform.runLater(() -> restAPIController.getHistoryListView().getItems().add(request));
        }
    }

    @Override
    public void requestAsync(Request request, ResponseRunnable onComplete, ExceptionRunnable onError) {
        executor.execute(() -> {
            try {
                RestResponse response = request(request);
                if (onComplete != null)
                    onComplete.run(response);
            } catch (Exception e) {
                log.error("HTTP request failed", e);
                if (onError != null)
                    onError.run(e);
            }
        });
    }

    @Override
    public void registerController(RestAPIController controller) {
        restAPIController = controller;
    }

    private void setRequestBody(OutputStream os, String body) throws IOException {
        OutputStreamWriter outStreamWriter = new OutputStreamWriter(os, StandardCharsets.UTF_8);
        outStreamWriter.write(body);
        outStreamWriter.flush();
        outStreamWriter.close();
        os.close();
    }

    private String getResponseBody(InputStream is) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(is));
        String line;
        StringBuilder builder = new StringBuilder();
        while ((line = in.readLine()) != null) {
            builder.append(line);
        }
        in.close();
        return builder.toString();
    }

    private String getResponseHeaders(Map<String, List<String>> fields) {
        return fields.entrySet().stream()
                .map(e -> {
                    if (e.getKey() == null) {
                        return String.join(", ", e.getValue());
                    }
                    return e.getKey() + ": " + String.join(", ", e.getValue());
                })
                .collect(Collectors.joining("\n"));
    }

    private URL buildURL(String uri) throws MalformedURLException {
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
