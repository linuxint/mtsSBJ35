package com.devkbil.mtssbj.config;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

import java.util.Arrays;

import lombok.extern.slf4j.Slf4j;

/**
 * Spring의 캐싱 지원을 활성화하고 사용자 지정 캐시 초기화를 제공하는 구성 클래스입니다.
 * 이 클래스는 @EnableCaching을 사용하여 주석 기반 캐시 관리를 활성화합니다.
 * 또한 Spring 컨텍스트에서 구성 클래스로 나타내기 위해 @Configuration을 사용합니다.
 * <p>
 * 이 클래스는 애플리케이션 수명 주기의 생성 이후 단계에서 특정 캐시 항목을 미리 로드합니다.
 */
@EnableCaching
@Configuration
@Slf4j
public class CacheConfig {

    private CacheManager cacheManager;

    /**
     * 애플리케이션의 캐싱 지원을 위한 {@link CacheManager} 빈을 구성하고 제공합니다.
     * 미리 정의된 캐시 이름과 사용자 지정 캐시 사양으로 {@link CaffeineCacheManager}를 초기화합니다.
     *
     * @return 구성된 {@link CacheManager} 인스턴스
     */
    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();

        // 캐시 이름 등록
        cacheManager.setCacheNames(Arrays.asList("boardNotices",  // 게시판 공지사항 캐시
            "boardList",     // 게시판 목록 캐시
            "boardDetail",   // 개별 게시판 게시물 캐시
            "alertCount",    // 알림 카운트 캐시
            "exampleCache"   // 레거시 캐시
        ));

        // 다양한 유형에 대한 서로 다른 TTL로 캐시 설정 구성
        // 형식: initialCapacity, maximumSize, expireAfterWrite, recordStats
        cacheManager.setCacheSpecification("initialCapacity=100,maximumSize=1000,expireAfterWrite=30m,recordStats");
        this.cacheManager = cacheManager; // 캐시 매니저를 필드에 저장
        return cacheManager;
    }

    /**
     * Spring ApplicationReadyEvent를 수신하면 미리 로드된 데이터로 캐시를 초기화합니다.
     * 이 메서드는 애플리케이션 컨텍스트가 완전히 초기화되고 준비되면 실행됩니다.
     * <p>
     * 구성된 CacheManager를 통해 특정 캐시인 'exampleCache'를 식별합니다. 캐시가 존재하는 경우,
     * 메서드는 미리 정의된 키-값 쌍으로 캐시를 채웁니다. 캐시가 존재하지 않거나 CacheManager가
     * 제대로 초기화되지 않은 경우 적절한 로그 메시지가 기록되어 문제를 표시합니다.
     * <p>
     * 이 메서드의 목적은 애플리케이션 시작 직후 특정 캐시 데이터가 미리 로드되어
     * 즉시 사용할 수 있도록 하는 것입니다.
     */
    @EventListener(ApplicationReadyEvent.class)
    public void initializeCache() {
        log.debug("ApplicationReadyEvent 수신, 캐시 데이터 로드 시작...");
        if (cacheManager == null) {
            log.error("캐시 관리자가 초기화되지 않았습니다.");
            return;
        }

        Cache cache = cacheManager.getCache("exampleCache");
        if (cache != null) {
            cache.put("preloadedKey", "preloadedValue");
            log.info("캐시 'exampleCache'가 데이터로 초기화되었습니다.");
        } else {
            log.warn("'exampleCache' 캐시를 찾을 수 없습니다.");
        }
    }
}