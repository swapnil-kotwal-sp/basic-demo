package com.my.demo1;

import com.my.demo.MyLogger;

public class App1 {
    private static MyLogger _log = MyLogger.getLogger(App1.class);

    public String getGreeting() {
        _log.debug(() -> "Hello world from my-demo1");
        return "Hello world from my-demo1";
    }

    public static void main(String[] args) {
        System.out.println(new App1().getGreeting());
        _log.debug(() -> "Hello world from my-demo1");
    }
}