package com.devkbil.mtssbj.file;

import org.springframework.util.ResourceUtils;

import java.io.FileNotFoundException;

public class ResourceTest {

    public ResourceTest() throws FileNotFoundException {
        ResourceUtils.getURL("classpath:logback");
    }
}
