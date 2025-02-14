package com.devkbil.mtssbj.develop.ratelimit;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.distributed.BucketProxy;
import io.github.bucket4j.distributed.jdbc.BucketTableSettings;
import io.github.bucket4j.distributed.jdbc.SQLProxyConfiguration;
import io.github.bucket4j.oracle.OracleSelectForUpdateBasedProxyManager;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

import javax.sql.DataSource;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * API 호출을 제어하는 레이트 리미터 클래스
 * - Bucket4j 라이브러리를 사용하여 API Rate Limiting을 구현
 */
@Slf4j
@Component
public class APIRateLimiter {

    private static final int CAPACITY = 3; // 버킷의 최대 요청 수
    private static final int REFILL_AMOUNT = 3; // 리필 요청량
    private static final Duration REFILL_DURATION = Duration.ofSeconds(5); // 리필 주기

    private final OracleSelectForUpdateBasedProxyManager<Long> proxyManager; // Oracle DB 기반 버킷 매니저
    private final ConcurrentMap<String, BucketProxy> buckets = new ConcurrentHashMap<>();

    public APIRateLimiter(DataSource dataSource) {
        //var tableSettings = BucketTableSettings.getDefault();
        var tableSettings = BucketTableSettings.customSettings("tbl_bucket", "bkno", "state");
        var sqlProxyConfiguration = SQLProxyConfiguration.builder()
                .withTableSettings(tableSettings)
                .build(dataSource);
        proxyManager = new OracleSelectForUpdateBasedProxyManager<>(sqlProxyConfiguration);
    }

    /**
     * API 키에 해당하는 버킷을 조회하거나 새 버킷 생성
     *
     * @param apiKey 고유 API 키
     * @return 버킷 객체
     */
    private BucketProxy getOrCreateBucket(String apiKey) {
        return buckets.computeIfAbsent(apiKey, key -> {
            Long bucketId = (long) key.hashCode(); // 키 해시값으로 버킷 ID 생성
            var bucketConfiguration = createBucketConfiguration(); // 버킷 설정 생성
            return proxyManager.builder().build(bucketId, bucketConfiguration); // 버킷 생성
        });
    }

    /**
     * 버킷 설정 생성
     *
     * @return BucketConfiguration 객체
     */
    private BucketConfiguration createBucketConfiguration() {
        return BucketConfiguration.builder()
                .addLimit(Bandwidth.builder()
                        .capacity(CAPACITY)
                        .refillIntervally(REFILL_AMOUNT, REFILL_DURATION)
                        .build())
                .build();
    }

    /**
     * 특정 API 키의 요청을 소비 (레이트 리미팅 적용)
     *
     * @param apiKey 제한을 적용할 API 키
     * @return 요청 소비 여부
     */
    public boolean tryConsume(String apiKey) {
        BucketProxy bucket = getOrCreateBucket(apiKey);
        boolean consumed = bucket.tryConsume(1);
        log.info("API Key: {}, Consumed: {}, Time: {}", apiKey, consumed, LocalDateTime.now());
        return consumed;
    }
}
