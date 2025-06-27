package com.devkbil.mtssbj;

import java.util.function.Consumer;
import java.util.Locale;

public class ConsumerTest {
    public static void main(String[] args) {
        Consumer<String> stringConsumer = (input) -> System.out.println(input.toLowerCase(Locale.ROOT));
        stringConsumer.accept("Java2s.com");
    }
}