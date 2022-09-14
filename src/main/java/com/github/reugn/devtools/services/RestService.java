package com.github.reugn.devtools.services;

import com.github.reugn.devtools.async.ExceptionRunnable;
import com.github.reugn.devtools.async.ResponseRunnable;
import com.github.reugn.devtools.controllers.RestAPIController;
import com.github.reugn.devtools.models.Request;

import java.util.List;

public interface RestService {

    void addToRequestHistory(List<Request> requests);

    void removeFromRequestHistory(Request request);

    void clearRequestHistory();

    List<Request> getRequestHistory();

    void request(Request request, ResponseRunnable onComplete, ExceptionRunnable onError);

    void registerController(RestAPIController controller);
}
