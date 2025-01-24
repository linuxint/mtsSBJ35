package com.devkbil.mtssbj.common.util;

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
 * fasterxml.jackson maven
 *
 * <pre>
 *   <!-- json -->
 *   <!-- https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-databind -->
 *   <dependency>
 *     <groupId>com.fasterxml.jackson.core</groupId>
 *     <artifactId>jackson-databind</artifactId> <version>2.11.2</version>
 *   </dependency>
 * </pre>
 */
public class JsonUtil {
    public static final String[] DEFAULT_EXCLUDE = {"password", "pwd", "userPwd", "currentPwd", "newPwd", "oldPwd"};
    private static final ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.setSerializationInclusion(Include.NON_NULL);
        mapper.addMixIn(Object.class, MyMixIn.class);
        mapper.setFilterProvider(getFilterProvider(DEFAULT_EXCLUDE));
    }

    private static FilterProvider getFilterProvider(String... excludeWords) {
        SimpleFilterProvider simpleFilterProvider = new SimpleFilterProvider();
        FilterProvider filterProvider = simpleFilterProvider//
                .addFilter("myMixIn", SimpleBeanPropertyFilter.serializeAllExcept(excludeWords));
        return filterProvider;
    }

    private static ObjectMapper getMapper() {
        return getMapper(null, null);
    }

    private static ObjectMapper getMapper(PropertyNamingStrategy pns) {
        return getMapper(null, pns);
    }

    private static ObjectMapper getMapper(FilterProvider filterProvider) {
        return getMapper(filterProvider, null);
    }

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

    @JsonFilter("myMixIn")
    public class MyMixIn {
        //
    }

}
