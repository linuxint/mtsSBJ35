package com.devkbil.mtssbj.develop.ratelimit;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 레이트 리미팅 테스트 컨트롤러
 * - 레이트 리미팅 기능을 테스트하기 위한 REST API 엔드포인트
 */
@RestController
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Rate Limiting API", description = "레이트 리미팅을 테스트하는 API")
public class RateLimitingController {

    private final RateLimitingService rateLimitingService;

    /**
     * 테스트 엔드포인트 1 (레이트 리밋 적용)
     *
     * @return 호출 성공 메시지
     */
    @Operation(summary = "테스트 엔드포인트 1", description = "someUniqueKey1 기준으로 호출 제한")
    @GetMapping("/test/1")
    public String test1() {
        return rateLimitingService.run1();
    }

    /**
     * 테스트 엔드포인트 2 (레이트 리밋 적용)
     *
     * @return 호출 성공 메시지
     */
    @Operation(summary = "테스트 엔드포인트 2", description = "someUniqueKey2 기준으로 호출 제한")
    @GetMapping("/test/2")
    public String test2() {
        return rateLimitingService.run2();
    }
}
