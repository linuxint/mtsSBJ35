package com.devkbil.mtssbj.common.log;

import ch.qos.logback.classic.spi.ILoggingEvent;

import org.springframework.boot.json.JsonWriter;
import org.springframework.boot.logging.structured.StructuredLogFormatter;

import java.nio.charset.Charset;

/**
 * 커스텀 로그 포매터로, 로그 이벤트를 구조화된 JSON 출력으로 포맷합니다.
 * 이는 잘 구조화된 로그 항목을 제공하여 로깅 및 모니터링 도구와 통합하는 데 유용합니다.
 */
public class MyStructuredLoggingFormatter implements StructuredLogFormatter<ILoggingEvent> {

    // 애플리케이션 세부정보를 위한 상수 (정적이고 유지보수가 쉽도록 재사용 가능)
    private static final String APPLICATION_NAME = "StructuredLoggingDemo";
    private static final String APPLICATION_VERSION = "1.0.0-SNAPSHOT";
    private static final String NODE_HOSTNAME = "node-1";
    private static final String NODE_IP = "10.0.0.7";

    /**
     * 로그 이벤트를 바이트 배열로 포맷하며, 문자셋 인코딩을 사용합니다.
     * 기본 StructuredLogFormatter 구현에 위임합니다.
     *
     * @param event   로그 이벤트
     * @param charset 사용할 문자셋
     * @return 바이트 배열로 포맷된 로그 항목
     */
    @Override
    public byte[] formatAsBytes(ILoggingEvent event, Charset charset) {
        return StructuredLogFormatter.super.formatAsBytes(event, charset);
    }

    /**
     * 로그 이벤트를 위해 구조화된 JSON 출력을 생성하는 Writer.
     * 시간, 레벨, 쓰레드 이름, 추가 애플리케이션 메타데이터와 같은 키-값 쌍을 포함합니다.
     */
    private final JsonWriter<ILoggingEvent> writer = JsonWriter.<ILoggingEvent>of((members) -> {
        // 표준 로그 이벤트 상세정보 추가
        members.add("time", ILoggingEvent::getInstant); // 로그 이벤트 타임스탬프
        members.add("level", ILoggingEvent::getLevel); // 로그 수준 (INFO, DEBUG 등)
        members.add("thread", ILoggingEvent::getThreadName); // 로그를 발생시킨 쓰레드 이름
        members.add("message", ILoggingEvent::getFormattedMessage); // 실제 로그 메시지

        // 애플리케이션별 메타데이터
        members.add("application").usingMembers((application) -> {
            application.add("name", APPLICATION_NAME); // 애플리케이션 이름
            application.add("version", APPLICATION_VERSION); // 애플리케이션 버전
        });

        // 노드별 메타데이터 (예: 호스트 이름과 IP 주소)
        members.add("node").usingMembers((node) -> {
            node.add("hostname", NODE_HOSTNAME); // 노드의 호스트 이름
            node.add("ip", NODE_IP); // 노드의 IP 주소
        });
    }).withNewLineAtEnd();

    /**
     * 로그 이벤트를 JSON 문자열 표현으로 포맷합니다.
     *
     * @param event 로그 이벤트
     * @return 문자열로 포맷된 로그 항목
     */
    @Override
    public String format(ILoggingEvent event) {
        return this.writer.writeToString(event);
    }
}
