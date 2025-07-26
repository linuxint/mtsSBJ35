package com.devkbil.mtssbj.common.masking;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.Arrays;

/**
 * SLF4J 로그 출력 시 VO/DTO의 @Mask 필드를 자동으로 마스킹하는 AOP
 */
@Aspect
@Component
public class MaskingLogAop {
    // SLF4J Logger의 info/debug/warn/error 등 메서드에 적용
    @Pointcut("execution(* org.slf4j.Logger+.*(..))")
    public void loggerMethods() {}

    @Around("loggerMethods()")
    public Object aroundLogger(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        boolean changed = false;
        Object[] newArgs = Arrays.copyOf(args, args.length);
        for (int i = 0; i < args.length; i++) {
            Object arg = args[i];
            if (arg != null && isMaskingTarget(arg)) {
                newArgs[i] = MaskingLogUtil.toMaskedLogString(arg);
                changed = true;
            }
        }
        if (changed) {
            return joinPoint.proceed(newArgs);
        } else {
            return joinPoint.proceed();
        }
    }

    // VO/DTO 객체인지, @Mask 어노테이션이 붙은 필드가 있는지 검사
    private boolean isMaskingTarget(Object obj) {
        Class<?> clazz = obj.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Mask.class)) {
                return true;
            }
        }
        return false;
    }
} 