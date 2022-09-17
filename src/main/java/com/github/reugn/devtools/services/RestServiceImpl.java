package com.github.reugn.devtools.services;

import com.github.reugn.devtools.async.ExceptionRunnable;
import com.github.reugn.devtools.async.ResponseRunnable;
import com.github.reugn.devtools.controllers.RestAPIController;
import com.github.reugn.devtools.models.Request;
import com.github.reugn.devtools.models.RestResponse;
import com.google.common.base.Joiner;
import javafx.application.Platform;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.util.LinkedList;
import java.util.List;

public class RestServiceImpl implements RestService {

    private static final Logger log = LogManager.getLogger(RestServiceImpl.class);

    private final List<Request> requestHistoryList = new LinkedList<>();
    private final HttpClient httpClient = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).build();
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

    @Override
    public void request(Request request, ResponseRunnable onComplete, ExceptionRunnable onError) {
        final Instant start = Instant.now();
        httpClient.sendAsync(
                request.toHTTPRequest(),
                HttpResponse.BodyHandlers.ofString()
        ).whenComplete((r, e) -> {
            if (e != null) {
                log.error("HTTP request failed", e);
                onError.run(e);
            } else {
                Instant finish = Instant.now();
                long time = Duration.between(start, finish).toMillis();
                addToHistory(request);
                RestResponse response = new RestResponse(
                        r.statusCode(),
                        r.body(),
                        Joiner.on("\n").withKeyValueSeparator(": ").join(r.headers().map()),
                        time
                );
                onComplete.run(response);
            }
        });
    }

    private void addToHistory(Request request) {
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
