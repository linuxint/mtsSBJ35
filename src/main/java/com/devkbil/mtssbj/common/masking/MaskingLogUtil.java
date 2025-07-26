package com.devkbil.mtssbj.common.masking;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.lang.reflect.Field;

public class MaskingLogUtil {
    public static String toMaskedLogString(Object obj) {
        if (obj == null) return "null";
        try {
            Class<?> clazz = obj.getClass();
            Object maskedObj = clazz.getDeclaredConstructor().newInstance();
            for (Field field : clazz.getDeclaredFields()) {
                field.setAccessible(true);
                Object value = field.get(obj);
                if (field.isAnnotationPresent(Mask.class) && value instanceof String strVal) {
                    Mask mask = field.getAnnotation(Mask.class);
                    String masked = MaskingUtil.maskingOf(mask, strVal);
                    field.set(maskedObj, masked);
                } else {
                    field.set(maskedObj, value);
                }
            }
            // JSON 문자열로 변환
            return new ObjectMapper().writeValueAsString(maskedObj);
        } catch (Exception e) {
            return obj.toString();
        }
    }
} 