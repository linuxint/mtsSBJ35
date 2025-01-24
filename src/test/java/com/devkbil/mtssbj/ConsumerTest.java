package com.devkbil.mtssbj;

import java.util.function.Consumer;

public class ConsumerTest {
    public static void main(String[] args) {
        Consumer<String> c = (x) -> System.out.println(x.toLowerCase());
        c.accept("Java2s.com");
    }
}
