package com.devkbil.mtssbj.common.log;

import lombok.Getter;

/**
 * 로깅에서 MDC(Mapped Diagnostic Context) 항목을 관리하는 데 사용되는 사전 정의 키 집합을 나타냅니다.
 * 이 열거형은 컨텍스처 로깅(Contextual Logging) 목적으로 MDC에 설정할 수 있는 일반적으로 사용되는 키를 정의합니다.
 * 각 키는 컨텍스트 정보를 식별하는 특정 문자열 값과 연결됩니다.
 * <p>
 * MDC(Mapped Diagnostic Context)는 SLF4J 및 Logback과 같은 로깅 프레임워크에서 제공하는 기능으로,
 * 로그 메시지에 컨텍스트 정보를 첨부할 수 있게 하여 디버깅 및 로그 추적을 용이하게 합니다.
 * <p>
 * 열거형 설명:
 * - TRX_ID: 특정 트랜잭션을 추적하는 데 사용되는 트랜잭션 ID 키를 나타냅니다.
 * - USER_IP: 요청을 수행한 사용자의 IP 주소를 저장하는 사용자 IP 주소 키를 나타냅니다.
 * - CORRELATION_ID: 분산 시스템에서 요청을 추적하는 데 일반적으로 사용되는 상관 ID 키를 나타냅니다.
 * - USER_NAME: 인증된 사용자의 이름을 저장하는 사용자 이름 키를 나타냅니다.
 * - USER_ID: 인증된 사용자의 고유 식별자를 저장하는 사용자 ID 키를 나타냅니다.
 * - USER_NM: 사용자 이름을 저장하기 위한 또 다른 키를 나타내며, 대체 명명 규칙으로 자주 사용됩니다.
 * <p>
 * 이 enum은 각 키와 연결된 문자열 값을 검색할 수 있는 `getKey` 메서드를 제공합니다.
 */
@Getter
public enum MDCKey {
    /**
     * 트랜잭션 ID를 식별하는 MDC 키.
     * 각 트랜잭션의 고유한 식별자를 추적하는 데 사용됩니다.
     */
    TRX_ID("trxId"),

    /**
     * 사용자 IP 주소를 식별하는 MDC 키.
     * 요청을 보낸 클라이언트의 IP 주소를 저장합니다.
     */
    USER_IP("userip"),

    /**
     * 상관 관계 ID를 식별하는 MDC 키.
     * HTTP 헤더에서 제공되며, 분산 시스템에서 요청을 추적하는 데 사용됩니다.
     */
    CORRELATION_ID("X-Correlation-Id"),

    /**
     * 사용자 이름을 식별하는 MDC 키.
     * 로그인한 사용자의 표시 이름을 저장합니다.
     */
    USER_NAME("userName"),

    /**
     * 사용자 ID를 식별하는 MDC 키.
     * 로그인한 사용자의 고유 식별자를 저장합니다.
     */
    USER_ID("userid"),

    /**
     * 사용자 이름을 식별하는 대체 MDC 키.
     * USER_NAME의 대체 표현으로 사용됩니다.
     */
    USER_NM("usernm");

    /**
     * MDC에서 사용되는 실제 키 값.
     * 이 값은 MDC에 컨텍스트 정보를 저장하고 검색하는 데 사용됩니다.
     */
    private final String key;

    /**
     * MDCKey 열거형 상수를 초기화하는 생성자.
     *
     * @param key MDC에서 사용될 실제 키 값
     */
    MDCKey(String key) {
        this.key = key;
    }
}
