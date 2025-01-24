package com.devkbil.mtssbj.config.environment;

/**
 * 환경별 구성을 나타내는 인터페이스.
 * 이 인터페이스는 메시지나 캐싱 메커니즘과 같은 환경별 구성을
 * 검색하기 위한 메서드를 제공합니다.
 */
public interface EnvConfiguration {

    String getMessage();

    String cache();
}
