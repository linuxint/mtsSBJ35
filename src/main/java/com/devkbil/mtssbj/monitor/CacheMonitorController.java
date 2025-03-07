package com.devkbil.mtssbj.monitor;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.stats.CacheStats;

import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 캐시 통계를 모니터링하는 컨트롤러
 * 캐시 적중률 및 기타 메트릭스를 확인하는 엔드포인트를 제공
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/monitor/cache")
public class CacheMonitorController {

    private final CacheManager cacheManager;

    /**
     * 모든 캐시의 통계 정보를 가져옴
     * @return 캐시 통계를 포함하는 맵
     */
    @GetMapping("/stats")
    public Map<String, Map<String, Object>> getCacheStatistics() {
        Map<String, Map<String, Object>> stats = new HashMap<>();

        cacheManager.getCacheNames().forEach(cacheName -> {
            CaffeineCache cache = (CaffeineCache)cacheManager.getCache(cacheName);
            if (cache != null) {
                Cache<Object, Object> nativeCache = cache.getNativeCache();
                CacheStats cacheStats = nativeCache.stats();

                Map<String, Object> cacheMetrics = new HashMap<>();
                cacheMetrics.put("size", nativeCache.estimatedSize());
                cacheMetrics.put("hitCount", cacheStats.hitCount());
                cacheMetrics.put("missCount", cacheStats.missCount());
                cacheMetrics.put("hitRate", cacheStats.hitRate());
                cacheMetrics.put("missRate", cacheStats.missRate());
                cacheMetrics.put("evictionCount", cacheStats.evictionCount());

                stats.put(cacheName, cacheMetrics);

                // 캐시 성능 기록
                log.info("Cache '{}' statistics: hit rate = {}, size = {}",
                    cacheName,
                    String.format("%.2f", cacheStats.hitRate() * 100) + "%",
                    nativeCache.estimatedSize());
            }
        });

        return stats;
    }
}