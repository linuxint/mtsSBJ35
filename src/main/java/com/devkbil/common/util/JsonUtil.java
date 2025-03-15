package com.devkbil.common.util;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.json.JsonMapper.Builder;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * JSON 직렬화 및 역직렬화를 처리하기 위한 유틸리티 클래스.
 * 객체를 JSON 형식으로 변환하거나 JSON 문자열을 다시 객체로 변환하는 메서드를 제공합니다.
 * 특정 필드를 포함하거나 제외하여 직렬화 과정을 제어하는 기능을 포함하고 있습니다.
 */
public class JsonUtil {
    /**
     * 기본적으로 제외할 필드 목록.
     * 비밀번호 관련 필드를 포함하며, 직렬화에서 제외됩니다.
     */
    public static final String[] DEFAULT_EXCLUDE = {"password", "pwd", "userPwd", "currentPwd", "newPwd", "oldPwd"};

    /**
     * Jackson 기반 ObjectMapper 객체.
     * 기본적으로 null 값을 포함하지 않으며, 특정 설정이 미리 적용되어 있다.
     */
    private static final ObjectMapper mapper = new ObjectMapper();

    static {
        /**
         * 초기 설정 블록:
         * 필터 설정 및 기본 직렬화 동작을 정의합니다.
         */
        mapper.setSerializationInclusion(Include.NON_NULL); // null 값 직렬화 제외
        mapper.addMixIn(Object.class, MyMixIn.class); // 기본 MixIn 클래스
        mapper.setFilterProvider(getFilterProvider(DEFAULT_EXCLUDE)); // 필터로 필드 제외
    }

    /**
     * 필드 제외 조건이 포함된 FilterProvider를 반환합니다.
     *
     * @param excludeWords 제외할 필드 이름 배열
     * @return 설정된 FilterProvider
     */
    private static FilterProvider getFilterProvider(String... excludeWords) {
        SimpleFilterProvider simpleFilterProvider = new SimpleFilterProvider();
        FilterProvider filterProvider = simpleFilterProvider//
                .addFilter("myMixIn", SimpleBeanPropertyFilter.serializeAllExcept(excludeWords));
        return filterProvider;
    }

    /**
     * 기본 ObjectMapper를 반환합니다.
     *
     * @return 기본 설정된 ObjectMapper
     */
    private static ObjectMapper getMapper() {
        return getMapper(null, null);
    }

    /**
     * 주어진 네이밍 전략(PropertyNamingStrategy)을 설정한 ObjectMapper를 반환합니다.
     *
     * @param pns 네이밍 전략
     * @return 설정된 ObjectMapper
     */
    private static ObjectMapper getMapper(PropertyNamingStrategy pns) {
        return getMapper(null, pns);
    }

    /**
     * 특정 FilterProvider가 설정된 ObjectMapper를 반환합니다.
     *
     * @param filterProvider 필터 제공자
     * @return 설정된 ObjectMapper
     */
    private static ObjectMapper getMapper(FilterProvider filterProvider) {
        return getMapper(filterProvider, null);
    }

    /**
     * FilterProvider와 네이밍 전략이 모두 설정된 ObjectMapper를 반환합니다.
     *
     * @param filterProvider 필터 제공자
     * @param pns            네이밍 전략
     * @return 설정된 ObjectMapper
     */
    private static ObjectMapper getMapper(FilterProvider filterProvider, PropertyNamingStrategy pns) {
        if (filterProvider == null) {
            filterProvider = getFilterProvider(DEFAULT_EXCLUDE);
        }
        if (pns == null) {
            pns = PropertyNamingStrategies.LOWER_CAMEL_CASE;
        }
        mapper.setFilterProvider(filterProvider);
        mapper.setPropertyNamingStrategy(pns);
        return mapper;
    }

    /**
     * 객체를 JSON 문자열로 변환합니다.
     * 특정 필드를 제외할 수 있습니다.
     *
     * @param object       변환할 객체
     * @param excludeWords 제외할 필드 이름 배열
     * @return JSON 문자열
     */
    public static String toJson(Object object, String... excludeWords) {
        if (object == null) {
            return null;
        }
        List<String> combinedWordList = new ArrayList<>();
        List<String> excludeWordList = Arrays.asList(excludeWords);
        List<String> defaultExcludeWordList = Arrays.asList(DEFAULT_EXCLUDE);
        combinedWordList.addAll(excludeWordList);
        combinedWordList.addAll(defaultExcludeWordList);
        String[] combineExcludeWords = new String[combinedWordList.size()];
        combinedWordList.toArray(combineExcludeWords);
        try {
            return getMapper(getFilterProvider(combineExcludeWords)).writer().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 객체를 JSON 문자열로 변환합니다.
     * 특정 필드만 포함할 수 있습니다.
     *
     * @param object       변환할 객체
     * @param includeWords 포함할 필드 이름 배열
     * @return JSON 문자열
     */
    public static String toJsonInlcude(Object object, String... includeWords) {
        if (object == null) {
            return null;
        }
        SimpleFilterProvider simpleFilterProvider = new SimpleFilterProvider();
        FilterProvider filterProvider = simpleFilterProvider//
                .addFilter("myMixIn", SimpleBeanPropertyFilter.filterOutAllExcept(includeWords));
        try {
            return getMapper(filterProvider).writer().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 객체를 JSON 문자열로 변환합니다.
     *
     * @param object 변환할 객체
     * @return JSON 문자열
     */
    public static String toJson(Object object) {
        if (object == null) {
            return null;
        }
        try {
            return getMapper(getFilterProvider(DEFAULT_EXCLUDE)).writer().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 특정 Serialization View를 사용하여 객체를 JSON 문자열로 변환합니다.
     *
     * @param object            변환할 객체
     * @param serializationView 직렬화 뷰 클래스
     * @return JSON 문자열
     */
    public static String toJson(Object object, Class<?> serializationView) {
        if (object == null) {
            return null;
        }
        try {
            Builder builder = JsonMapper.builder().disable(MapperFeature.DEFAULT_VIEW_INCLUSION);
            return builder.build().writerWithView(serializationView).writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * JSON 문자열을 주어진 클래스 형식의 객체로 변환합니다.
     *
     * @param string    JSON 문자열
     * @param valueType 변환 대상 클래스
     * @param <T>       대상 타입
     * @return 변환된 객체
     */
    public static <T> T toObjectByClass(String string, Class<T> valueType) {
        if (string == null) {
            return null;
        }
        try {
            return getMapper().readValue(string, valueType);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * JSON 문자열을 주어진 클래스와 네이밍 전략에 맞는 객체로 변환합니다.
     *
     * @param string                  JSON 문자열
     * @param valueType               변환 대상 클래스
     * @param propertyNamingStrategies 네이밍 전략
     * @param <T>                     대상 타입
     * @return 변환된 객체
     */
    public static <T> T toObjectByClass(String string, Class<T> valueType,
                                        PropertyNamingStrategy propertyNamingStrategies) {
        if (string == null) {
            return null;
        }
        try {
            return getMapper(propertyNamingStrategies).readValue(string, valueType);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * JSON 문자열을 정의된 타입 참조(TypeReference)의 객체로 변환합니다.
     *
     * @param string        JSON 문자열
     * @param typeReference 타입 참조 객체
     * @param <T>           대상 타입
     * @return 변환된 객체
     */
    @SuppressWarnings("unchecked")
    public static <T> T toObject(String string, TypeReference<?> typeReference) {
        if (string == null) {
            return null;
        }
        try {
            return (T) getMapper().readValue(string, typeReference);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * JSON 문자열을 네이밍 전략을 포함한 타입 참조 객체로 변환합니다.
     *
     * @param string                  JSON 문자열
     * @param typeReference           타입 참조 객체
     * @param propertyNamingStrategies 네이밍 전략
     * @param <T>                     대상 타입
     * @return 변환된 객체
     */
    @SuppressWarnings("unchecked")
    public static <T> T toObject(String string, TypeReference<?> typeReference,
                                 PropertyNamingStrategy propertyNamingStrategies) {
        if (string == null) {
            return null;
        }
        try {
            return (T) getMapper(propertyNamingStrategies).readValue(string, typeReference);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * FilterProvider를 사용하여 객체를 JSON 문자열로 변환합니다.
     *
     * @param object  변환할 객체
     * @param filters 필터 제공자
     * @return JSON 문자열
     */
    public static String toJson(Object object, FilterProvider filters) {
        if (object == null) {
            return null;
        }
        try {
            if (filters != null) {
                return getMapper().writer(filters).writeValueAsString(object);
            } else {
                return getMapper().writer().writeValueAsString(object);
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * JSON 필드 필터링을 위한 MixIn 클래스.
     */
    @JsonFilter("myMixIn")
    public class MyMixIn {
        //
    }

}