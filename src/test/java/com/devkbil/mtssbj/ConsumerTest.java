package com.devkbil.mtssbj;

import java.util.function.Consumer;

public class ConsumerTest {
    public static void main(String[] args) {
        Consumer<String> stringConsumer = (input) -> System.out.println(input.toLowerCase());
        stringConsumer.accept("Java2s.com");
    }
}