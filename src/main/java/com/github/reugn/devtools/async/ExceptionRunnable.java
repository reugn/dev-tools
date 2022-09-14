package com.github.reugn.devtools.async;

@FunctionalInterface
public interface ExceptionRunnable {

    void run(Exception e);
}
