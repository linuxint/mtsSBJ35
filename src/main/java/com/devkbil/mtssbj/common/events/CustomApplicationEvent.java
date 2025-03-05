package com.devkbil.mtssbj.common.events;

import org.springframework.context.ApplicationEvent;

/**
 * CustomApplicationEvent는 Spring의 {@link ApplicationEvent}를 확장한 하위 클래스입니다.
 * 이 이벤트는 애플리케이션의 이벤트 시스템을 통해 사용자 정의 메시지와 같은 애플리케이션 관련 정보를
 * 브로드캐스트하는 데 사용할 수 있습니다.
 *
 * 이 클래스는 이벤트의 소스와 함께 사용자 정의 메시지를 캡슐화합니다.
 */
public class CustomApplicationEvent extends ApplicationEvent {

    private final String message;

    /**
     * 새로운 CustomApplicationEvent를 생성합니다.
     *
     * @param source 이벤트가 처음 발생한 객체 (null이 아니어야 함)
     * @param message 이 이벤트와 연관된 사용자 정의 메시지
     */
    public CustomApplicationEvent(Object source, String message) {
        super(source);
        this.message = message;
    }

    /**
     * 이 이벤트와 연관된 사용자 정의 메시지를 가져옵니다.
     *
     * @return String 형식의 사용자 정의 메시지
     */
    public String getMessage() {
        return message;
    }
}
