package com.my.demo;

public class App {
    public String getGreeting() {
        return "Hello world from my-demo";
    }

    public static void main(String[] args) {
        System.out.println(new App().getGreeting());
    }
}