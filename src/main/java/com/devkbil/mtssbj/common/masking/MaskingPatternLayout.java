package com.devkbil.mtssbj.common.masking;

import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MaskingPatternLayout extends PatternLayout {

    private Pattern multilinePattern;
    private final List<String> maskPatterns = new ArrayList<>();

    public void addMaskPattern(String maskPattern) {
        maskPatterns.add(maskPattern);
        multilinePattern = Pattern.compile(maskPatterns.stream().collect(Collectors.joining("|")), Pattern.MULTILINE);
    }

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