package com.github.reugn.devtools.async;

import com.github.reugn.devtools.models.RestResponse;

@FunctionalInterface
public interface ResponseRunnable {

    void run(RestResponse response);
}
