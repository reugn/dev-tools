package com.github.reugn.devtools.services;

@FunctionalInterface
public interface ExceptionRunnable {

	public void run(Exception e);
}
