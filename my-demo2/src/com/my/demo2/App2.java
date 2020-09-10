package com.my.demo2;

import com.my.demo.MyLogger;

public class App2 {
    private static MyLogger _log = MyLogger.getLogger(App2.class);

    public String getGreeting() {
        _log.debug(() -> "Hello world from my-demo");
        return "Hello world from my-demo2";
    }

    public static void main(String[] args) {
        _log.debug(() -> "Hello world from my-demo");
        System.out.println(new App2().getGreeting());
    }
}