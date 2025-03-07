package com.devkbil.mtssbj.common;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Configuration;

import java.util.Locale;

import lombok.RequiredArgsConstructor;

/**
 * 애플리케이션의 MessageSource에서 지정된 로케일과 메시지 키를 기반으로
 * 현지화된 메시지를 검색하는 구성 클래스입니다.
 */
@Configuration
@RequiredArgsConstructor
public class LocaleMessage {

    /**
     * 애플리케이션의 메시지 소스입니다.
     * 이 필드는 Spring의 의존성 주입을 통해 자동으로 초기화됩니다.
     */
    MessageSource messageSource;

    /**
     * 지정된 키를 사용하여 애플리케이션의 메시지 소스에서 현지화된 메시지를 검색합니다.
     * 한국어 로케일을 기본값으로 사용합니다.
     *
     * @param key 원하는 메시지의 키
     * @return 한국어 로케일에서 제공된 키에 해당하는 현지화된 메시지
     * @throws org.springframework.context.NoSuchMessageException 메시지를 찾을 수 없는 경우
     */
    public String getMessage(String key) {
        return messageSource.getMessage(key, null, Locale.KOREA);
    }

    /**
     * 지정된 키와 선택적 인자를 사용하여 현지화된 메시지를 검색합니다.
     * 시스템의 기본 로케일을 사용합니다.
     *
     * @param key  원하는 메시지의 키
     * @param objs 메시지 내의 플레이스홀더를 채울 객체 배열
     * @return 제공된 키와 인자에 해당하는 현지화된 메시지
     * @throws org.springframework.context.NoSuchMessageException 메시지를 찾을 수 없는 경우
     */
    public String getMessage(String key, Object[] objs) {
        return messageSource.getMessage(key, objs, Locale.getDefault());
    }
}
