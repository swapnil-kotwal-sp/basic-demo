package com.my.demo1;

public class App1 {
    public String getGreeting() {
        return "Hello world from my-demo1";
    }

    public static void main(String[] args) {
        System.out.println(new App1().getGreeting());
    }
}