package com.my.demo;

public class App {
    private static MyLogger _log = MyLogger.getLogger(App.class);


    public String getGreeting() {
        _log.debug(() -> "Hello world from my-demo1");
        return "Hello world from my-demo";
    }

    public static void main(String[] args) {
        System.out.println(new App().getGreeting());
        _log.debug(() -> "Hello world from my-demo");
    }
}