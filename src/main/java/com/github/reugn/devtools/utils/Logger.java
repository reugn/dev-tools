package com.github.reugn.devtools.utils;

public interface Logger {

    default void debug(String msg) {
        System.out.println(msg);
    }
}
