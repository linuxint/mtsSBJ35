package com.devkbil.mtssbj.common;

import jakarta.annotation.PostConstruct;

import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Service
public class MsgService {
    private final Map<String, String> cache = new HashMap<>();

    @PostConstruct
    public void load() {
        Properties props = new Properties();
        try (InputStream is = getClass().getResourceAsStream("/messages/message.properties")) {
            props.load(is);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        props.forEach((k, v) -> cache.put(k.toString(), v.toString()));
    }

    // Enum으로 바로 메시지 반환
    public String get(MsgConstant msg) {
        return cache.getOrDefault(msg.key(), msg.key());
    }

    // 정적 접근 허용
    private static MsgService instance;
    @PostConstruct
    public void initStatic() {
        instance = this;
    }
    public static String msg(MsgConstant msg) {
        return instance.get(msg);
    }
}