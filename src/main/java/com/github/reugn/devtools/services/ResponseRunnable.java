package com.github.reugn.devtools.services;

import com.github.reugn.devtools.models.RestResponse;

@FunctionalInterface
public interface ResponseRunnable {

	public void run(RestResponse response);
}
