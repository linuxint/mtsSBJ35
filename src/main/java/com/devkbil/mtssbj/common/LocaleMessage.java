package com.devkbil.mtssbj.common;

import lombok.RequiredArgsConstructor;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Configuration;

import java.util.Locale;

@Configuration
@RequiredArgsConstructor
public class LocaleMessage {

    MessageSource messageSource;

    public String getMessage(String key) {
        return messageSource.getMessage(key, null, Locale.KOREA);
    }

    public String getMessage(String key, Object[] objs) {
        return messageSource.getMessage(key, objs, Locale.getDefault());

    }
}
