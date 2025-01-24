package com.devkbil.mtssbj.develop.ratelimit;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 레이트 리미팅 처리를 위한 AOP Aspect 클래스
 * - @RateLimit 어노테이션이 붙은 메서드의 호출 횟수를 제한합니다.
 */
@Aspect
@Component
public class RateLimitingAspect {

    private final APIRateLimiter apiRateLimiter;

    @Autowired
    public RateLimitingAspect(APIRateLimiter apiRateLimiter) {
        this.apiRateLimiter = apiRateLimiter;
    }

    /**
     * @param joinPoint 실행 중인 메서드 정보
     * @param rateLimit @RateLimit 어노테이션 정보
     * @return 메서드 실행 결과 반환
     * @throws Throwable 제한 초과 시 예외 발생
     * @RateLimit 어노테이션이 적용된 메서드의 실행 전후로 호출 제한 동작을 적용
     */
    @Around("@annotation(rateLimit)")
    public Object rateLimit(ProceedingJoinPoint joinPoint, RateLimit rateLimit) throws Throwable {
        if (apiRateLimiter.tryConsume(rateLimit.key())) {
            return joinPoint.proceed();
        } else {
            throw new RuntimeException("Rate limit exceeded for key: " + rateLimit.key()); // 호출 제한 초과 예외 발생
        }
    }
}
