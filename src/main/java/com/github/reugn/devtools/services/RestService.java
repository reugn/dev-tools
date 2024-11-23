package com.github.reugn.devtools.services;

import com.github.reugn.devtools.async.ExceptionRunnable;
import com.github.reugn.devtools.async.HttpResponseRunnable;
import com.github.reugn.devtools.controllers.RestAPIController;
import com.github.reugn.devtools.models.HttpRequest;

import java.util.List;

public interface RestService {

    void addToRequestHistory(List<HttpRequest> requests);

    void removeFromRequestHistory(HttpRequest request);

    void clearRequestHistory();

    List<HttpRequest> getRequestHistory();

    void request(HttpRequest request, HttpResponseRunnable onComplete, ExceptionRunnable onError);

    void registerController(RestAPIController controller);
}
