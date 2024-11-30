package com.github.reugn.devtools.services;

import com.github.reugn.devtools.async.ExceptionRunnable;
import com.github.reugn.devtools.async.HttpResponseRunnable;
import com.github.reugn.devtools.controllers.RestAPIController;
import com.github.reugn.devtools.models.HttpRequest;
import com.github.reugn.devtools.models.HttpResponse;
import com.google.common.base.Joiner;
import javafx.application.Platform;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.http.HttpClient;
import java.time.Duration;
import java.time.Instant;
import java.util.LinkedList;
import java.util.List;

public class RestServiceImpl implements RestService {

    private static final Logger log = LogManager.getLogger(RestServiceImpl.class);

    private final List<HttpRequest> requestHistoryList = new LinkedList<>();
    private final HttpClient httpClient = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).build();

    private RestAPIController restAPIController;

    @Override
    public void addToRequestHistory(List<HttpRequest> requests) {
        requestHistoryList.addAll(requests);
    }

    @Override
    public void removeFromRequestHistory(HttpRequest request) {
        requestHistoryList.remove(request);
    }

    @Override
    public void clearRequestHistory() {
        requestHistoryList.clear();
    }

    @Override
    public List<HttpRequest> getRequestHistory() {
        return requestHistoryList;
    }

    @Override
    public void request(HttpRequest request, HttpResponseRunnable onComplete, ExceptionRunnable onError) {
        final Instant start = Instant.now();
        httpClient.sendAsync(
                request.toHTTPRequest(),
                java.net.http.HttpResponse.BodyHandlers.ofString()
        ).whenComplete((r, e) -> {
            if (e != null) {
                log.error("HTTP request failed", e);
                onError.run(e);
            } else {
                Instant finish = Instant.now();
                long time = Duration.between(start, finish).toMillis();
                addToHistory(request);
                HttpResponse response = new HttpResponse(
                        r.statusCode(),
                        r.body(),
                        Joiner.on("\n").withKeyValueSeparator(": ").join(r.headers().map()),
                        time
                );
                onComplete.run(response);
            }
        });
    }

    private void addToHistory(HttpRequest request) {
        if (requestHistoryList.stream().filter(r -> r.equals(request)).findFirst().isEmpty()) {
            requestHistoryList.add(request);
            Platform.runLater(() -> restAPIController.getHistoryListView().getItems().add(request));
        }
    }

    @Override
    public void registerController(RestAPIController controller) {
        restAPIController = controller;
    }
}
