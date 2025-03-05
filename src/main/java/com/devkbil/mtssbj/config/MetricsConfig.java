package com.devkbil.mtssbj.config;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.cache.CaffeineCacheMetrics;
import io.micrometer.core.instrument.config.MeterFilter;

import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

/**
 * Micrometer 메트릭 설정
 * Spring Boot Actuator를 위한 캐시 모니터링 메트릭을 구성
 */
@Configuration
public class MetricsConfig {

    private final CacheConfig cacheConfig;
    private final CacheManager cacheManager;

    public MetricsConfig(@Lazy CacheConfig cacheConfig, CacheManager cacheManager) {
        this.cacheConfig = cacheConfig;
        this.cacheManager = cacheManager;
    }

    /**
     * 메트릭에 공통 태그를 구성하고 애플리케이션의 CacheManager에서 관리되는 모든 캐시에 대해
     * 캐시 메트릭을 등록.
     *
     * 이 메서드는 MeterRegistry를 사용자 정의하며, CacheManager에서 제공하는 모든 캐시 이름을
     * 반복적으로 순회하며, 각 캐시가 CaffeineCache의 인스턴스인 경우, 해당 캐시에 특정한
     * 모니터링 메트릭을 연결합니다. 메트릭은 공통 태그를 포함하며, 캐시 히트/미스 통계와 같은
     * 세부적인 통찰력을 제공합니다.
     *
     * @return MeterRegistry를 사용자 정의하여 공통 태그를 포함하고 모니터링 캐시에 대한
     *         캐시 메트릭을 등록하는 MeterRegistryCustomizer 인스턴스를 반환.
     */
    @Bean
    MeterRegistryCustomizer<MeterRegistry> metricsCommonTags() {
        return registry -> {
            // 각 Caffeine 캐시에 대해 캐시 메트릭 등록
            cacheManager.getCacheNames().forEach(cacheName -> {
                CaffeineCache caffeineCache = (CaffeineCache)cacheManager.getCache(cacheName);
                if (caffeineCache != null) {
                    // 코드 수정: 키-값 태그 추가
                    CaffeineCacheMetrics.monitor(
                        registry,
                        caffeineCache.getNativeCache(),
                        cacheName
                    );
                    registry.config().commonTags("cacheName", cacheName); // 키-값 쌍으로 태그 설정
                }
            });
        };
    }

    @Bean
    public MeterRegistryCustomizer<MeterRegistry> metricsRegistryCustomizer() {
        return registry -> registry.config()
            .commonTags("application", "my-app") // 모든 메트릭에 공통 태그 추가
            .meterFilter(MeterFilter.ignoreTags("cacheName")) // 충돌 원인이 되는 태그 무시
            .meterFilter(MeterFilter.renameTag("cache.puts", "cacheName", "cache.name")) // 태그 이름 변경
            .meterFilter(MeterFilter.deny(id -> {
                // 중복 메트릭 방지
                if ("cache.size".equals(id.getName()) && id.getTags().stream().anyMatch(tag -> tag.getKey().equals("cacheName"))) {
                    return true; // 특정 메트릭의 등록 제외
                }
                return false;
            }));
    }

}