package com.devkbil.mtssbj.develop.ratelimit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 레이트 리미팅 대상 서비스
 * - 각 메서드는 호출 횟수가 @RateLimit 어노테이션을 통해 제한됩니다.
 */
@Service
@Slf4j
public class RateLimitingService {

    /**
     * 첫 번째 서비스를 실행 (someUniqueKey1 기준으로 호출 제한)
     *
     * @return 호출 성공 메시지 반환
     */
    @RateLimit(key = "someUniqueKey1")
    public String run1() {
        return "요청 성공";
    }

    /**
     * 두 번째 서비스를 실행 (someUniqueKey2 기준으로 호출 제한)
     *
     * @return 호출 성공 메시지 반환
     */
    @RateLimit(key = "someUniqueKey2")
    public String run2() {
        return "요청 성공";
    }
}