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

    /**
     * 지정된 APIRateLimiter를 사용하여 RateLimitingAspect 인스턴스를 생성합니다.
     * 이 Aspect는 @RateLimit 어노테이션이 붙은 메서드에 레이트 리미팅을 적용합니다.
     *
     * @param apiRateLimiter API 호출 토큰과 제한을 관리하여 레이트 리미팅을 적용하는 APIRateLimiter 인스턴스
     */
    @Autowired
    public RateLimitingAspect(APIRateLimiter apiRateLimiter) {
        this.apiRateLimiter = apiRateLimiter;
    }

    /**
     * <p>@RateLimit 어노테이션이 붙은 메서드에 레이트 리미팅을 적용하는 메서드입니다.
     * 지정된 키를 기준으로 레이트 리미터에서 토큰을 소모합니다.
     * 레이트 리밋 초과 시 예외가 발생합니다.</p>
     *
     * @param joinPoint 대상 메서드 호출을 나타내는 조인 포인트
     * @param rateLimit 레이트 리미팅 설정 정보를 포함하는 @RateLimit 어노테이션
     * @return 레이트 리밋이 허용하는 경우 메서드 실행 결과
     * @throws Throwable 레이트 리밋 초과 시 또는 대상 메서드에서 예외가 발생한 경우
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
