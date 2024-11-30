package com.github.reugn.devtools.async;

import com.github.reugn.devtools.models.HttpResponse;

@FunctionalInterface
public interface HttpResponseRunnable {

    void run(HttpResponse response);
}
