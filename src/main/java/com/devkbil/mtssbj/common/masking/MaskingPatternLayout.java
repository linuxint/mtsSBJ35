package com.devkbil.mtssbj.common.masking;

import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Logback용 커스텀 패턴 레이아웃 클래스.
 * 로그 메시지에서 민감한 정보를 마스킹 처리하는 기능을 제공합니다.
 * 정규식 패턴을 사용하여 여러 줄의 로그 메시지에서 지정된 패턴과 일치하는 정보를 마스킹합니다.
 */
public class MaskingPatternLayout extends PatternLayout {

    private Pattern multilinePattern;
    private final List<String> maskPatterns = new ArrayList<>();

    /**
     * 마스킹할 패턴을 추가합니다.
     * 여러 패턴을 추가하면 '|'로 결합되어 하나의 정규식 패턴으로 컴파일됩니다.
     *
     * @param maskPattern 마스킹할 정규식 패턴
     */
    public void addMaskPattern(String maskPattern) {
        maskPatterns.add(maskPattern);
        multilinePattern = Pattern.compile(maskPatterns.stream().collect(Collectors.joining("|")), Pattern.MULTILINE);
    }

    /**
     * 로깅 이벤트에 마스킹을 적용하여 레이아웃을 수행합니다.
     * 부모 클래스의 doLayout 결과에 마스킹 처리를 추가로 적용합니다.
     *
     * @param event 로깅 이벤트
     * @return 마스킹이 적용된 로그 메시지
     */
    @Override
    public String doLayout(ILoggingEvent event) {
        return maskMessage(super.doLayout(event));
    }

    /**
     * logback masking 처리된 문자열 반환
     *
     * @param message 로그에 찍힌 전체 메시지 라인
     * @return String 개인정보 masking 처리하여 문자열 라인 반환
     */
    private String maskMessage(String message) {

        if (multilinePattern == null) {
            return message;
        }

        StringBuilder sb = new StringBuilder(message); // 로그에 찍힌 메세지 라인 StringBuilder에 담기
        Matcher matcher = multilinePattern.matcher(sb); // Matcher >> logback maskPattern 정규식 패턴

        while (matcher.find()) {
            IntStream.rangeClosed(1, matcher.groupCount()).forEach(group -> {
                if (matcher.group(group) != null) {
                    IntStream.range(matcher.start(group), matcher.end(group)).forEach(
                            i -> sb.setCharAt(i, '*')
                    );
                }
            });
        }
        return sb.toString();
    }
}