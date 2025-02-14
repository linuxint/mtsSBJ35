package com.devkbil.mtssbj.config;

import jakarta.annotation.PostConstruct;

import lombok.extern.slf4j.Slf4j;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

@EnableCaching
@Configuration
@Slf4j
public class CacheConfig {

    private final CacheManager cacheManager;

    public CacheConfig(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @PostConstruct
    public void loadData() {
        log.debug("Preloading cache data...");
        cacheManager.getCache("exampleCache").put("preloadedKey", "preloadedValue");
    }

}
