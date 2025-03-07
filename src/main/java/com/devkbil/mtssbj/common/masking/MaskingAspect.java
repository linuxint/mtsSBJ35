package com.devkbil.mtssbj.common.masking;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 데이터 마스킹을 처리하는 Aspect 클래스.
 * {@link Mask} 어노테이션이 적용된 필드에 대해 자동으로 마스킹 처리를 수행합니다.
 * DTO나 List 형태의 응답 데이터에 대해 마스킹을 적용할 수 있습니다.
 */
@Aspect
@EnableAspectJAutoProxy
@Component
public class MaskingAspect {
    /**
     * 응답 객체에 마스킹을 적용하는 유틸리티 메소드.
     *
     * @param clazz    응답 객체의 클래스 타입
     * @param klass    List 타입인 경우 제네릭 타입
     * @param response 마스킹을 적용할 응답 객체
     * @param <T>      반환될 객체의 타입
     * @return 마스킹이 적용된 객체
     * @throws InvocationTargetException 리플렉션 호출 중 발생한 예외
     * @throws NoSuchMethodException     메소드를 찾을 수 없는 경우
     * @throws InstantiationException    객체 생성 실패 시
     * @throws IllegalAccessException    접근 권한이 없는 경우
     */
    private static <T> T applyMaskingUtil(Class<?> clazz, Class<?> klass, Object response)
        throws InvocationTargetException,
        NoSuchMethodException,
        InstantiationException,
        IllegalAccessException {
        if (response instanceof List) {
            return applyMaskingUtilForList(klass, response); // 마스킹 적용할 데이터가 List<?> 형태인 경우
        } else {
            return applyMaskingUtilForDto(clazz, response);
        }
    }

    /**
     * DTO 객체에 마스킹을 적용하는 메소드.
     * 객체의 필드 중 {@link Mask} 어노테이션이 적용된 String 타입 필드에 마스킹을 적용합니다.
     *
     * @param clazz DTO 클래스 타입
     * @param response 마스킹을 적용할 DTO 객체
     * @param <T> 반환될 DTO 타입
     * @return 마스킹이 적용된 DTO 객체
     * @throws NoSuchMethodException 생성자를 찾을 수 없는 경우
     * @throws InvocationTargetException 리플렉션 호출 중 발생한 예외
     * @throws InstantiationException 객체 생성 실패 시
     * @throws IllegalAccessException 접근 권한이 없는 경우
     */
    private static <T> T applyMaskingUtilForDto(Class<?> clazz, Object response)
        throws NoSuchMethodException,
        InvocationTargetException,
        InstantiationException,
        IllegalAccessException {
        Field[] fields = clazz.getDeclaredFields();
        Object responseDto = clazz.getDeclaredConstructor().newInstance();
        Arrays.stream(fields)
            .forEach(
                field -> {
                    field.setAccessible(true);
                    try {
                        Object fieldValue = field.get(response);
                        if (fieldValue instanceof String && field.isAnnotationPresent(Mask.class)) {
                            Mask mask = field.getAnnotation(Mask.class); // Mask 어노테이션을 가져옴
                            MaskingType maskingType = mask.type(); // 해당 어노테이션이 보유한 Enum 타입을 가져옴
                            String maskedValue = MaskingUtil.maskingOf(maskingType, (String)fieldValue); // 마스킹 적용
                            field.set(responseDto, maskedValue);
                        } else {
                            field.set(responseDto, fieldValue);
                        }
                    } catch (Exception e) {
                    }
                });
        return (T)responseDto;
    }

    /**
     * List 타입 응답에 마스킹을 적용하는 메소드.
     * List의 각 요소에 대해 마스킹을 적용합니다.
     *
     * @param klass List 요소의 클래스 타입
     * @param response 마스킹을 적용할 List 객체
     * @param <T> 반환될 List 타입
     * @return 마스킹이 적용된 List 객체
     * @throws InvocationTargetException 리플렉션 호출 중 발생한 예외
     * @throws NoSuchMethodException 메소드를 찾을 수 없는 경우
     * @throws InstantiationException 객체 생성 실패 시
     * @throws IllegalAccessException 접근 권한이 없는 경우
     */
    private static <T> T applyMaskingUtilForList(Class<?> klass, Object response)
        throws InvocationTargetException,
        NoSuchMethodException,
        InstantiationException,
        IllegalAccessException {
        List<Object> responseDtoList = new ArrayList<>();
        List<?> responseList = (List<?>)response;
        for (Object responseDto : responseList) {
            if (responseDto != null && responseDto.getClass().equals(klass)) {
                Object maskedResponseDto = applyMaskingUtilForDto(klass, responseDto);
                responseDtoList.add(maskedResponseDto);
            } else {
                responseDtoList.add(responseDto);
            }
        }
        return (T)responseDtoList;
    }

    /**
     * {@link ApplyMasking} 어노테이션이 적용된 메소드의 실행을 가로채서 마스킹을 처리합니다.
     *
     * @param joinPoint    AOP 조인 포인트
     * @param applyMasking 마스킹 적용 설정이 포함된 어노테이션
     * @return 마스킹이 적용된 응답 객체
     * @throws Throwable 마스킹 처리 중 발생할 수 있는 예외
     */
    @Around("@annotation(applyMasking)")
    public Object applyMaskingAspect(ProceedingJoinPoint joinPoint, ApplyMasking applyMasking)
        throws Throwable {
        Object[] args = joinPoint.getArgs();
        Object response = joinPoint.proceed();
        MaskingDto maskingOn = (MaskingDto)args[0];

        if (maskingOn.getDisableMaskingYn() == null && response != null) {
            // 필드 누락 시
            return applyMaskingUtil(applyMasking.typeValue(), applyMasking.genericTypeValue(), response);
        }
        if (maskingOn.getDisableMaskingYn().equals("Y")) {
            // 원본 데이터
            return response;
        } else {
            // 마스킹 적용
            if (response != null) {
                return applyMaskingUtil(
                    applyMasking.typeValue(), applyMasking.genericTypeValue(), response);
            }
        }
        return response;
    }
}