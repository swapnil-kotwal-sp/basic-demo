package com.my.demo2;

public class App2 {
    public String getGreeting() {
        return "Hello world from my-demo2";
    }

    public static void main(String[] args) {
        System.out.println(new App2().getGreeting());
    }
}