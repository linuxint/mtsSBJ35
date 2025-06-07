package com.devkbil.common;

import com.fasterxml.jackson.core.SerializableString;
import com.fasterxml.jackson.core.io.CharacterEscapes;
import com.fasterxml.jackson.core.io.SerializedString;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.text.translate.AggregateTranslator;
import org.apache.commons.text.translate.CharSequenceTranslator;
import org.apache.commons.text.translate.EntityArrays;
import org.apache.commons.text.translate.LookupTranslator;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

/**
 * HTML Escape 처리를 위한 사용자 정의 클래스.
 * XSS(Cross-Site Scripting) 공격 방지를 위해 JSON 직렬화 과정에서 특수 문자를 이스케이프 처리합니다.
 */
@Slf4j
@Component
public class HtmlCharacterEscapes extends CharacterEscapes {

    private final int[] asciiEscapes;
    private final CharSequenceTranslator translator;

    /**
     * HtmlCharacterEscapes 생성자.
     * XSS 방지를 위한 특수 문자 이스케이프 설정을 초기화합니다.
     */
    public HtmlCharacterEscapes() {
        log.debug("Initializing HTML character escapes for XSS prevention");

        // 1. XSS 방지 처리할 특수 문자 지정
        asciiEscapes = CharacterEscapes.standardAsciiEscapesForJSON();
        asciiEscapes['<'] = CharacterEscapes.ESCAPE_CUSTOM;
        asciiEscapes['>'] = CharacterEscapes.ESCAPE_CUSTOM;
        asciiEscapes['&'] = CharacterEscapes.ESCAPE_CUSTOM;
        asciiEscapes['\"'] = CharacterEscapes.ESCAPE_CUSTOM;
        asciiEscapes['('] = CharacterEscapes.ESCAPE_CUSTOM;
        asciiEscapes[')'] = CharacterEscapes.ESCAPE_CUSTOM;
        asciiEscapes['#'] = CharacterEscapes.ESCAPE_CUSTOM;
        asciiEscapes['\''] = CharacterEscapes.ESCAPE_CUSTOM;

        // 2. 사용자 정의 Escape 설정
        Map<CharSequence, CharSequence> customMap = new HashMap<>();
        customMap.put("(", "&#40;");
        customMap.put("\'", "&apos;");
        Map<CharSequence, CharSequence> customEscape = Collections.unmodifiableMap(customMap);

        // 3. XSS 방지 처리 특수 문자 인코딩 값 지정
        translator = new AggregateTranslator(
                new LookupTranslator(EntityArrays.BASIC_ESCAPE),  // <, >, &, " 는 여기에 포함됨
                new LookupTranslator(EntityArrays.ISO8859_1_ESCAPE),
                new LookupTranslator(EntityArrays.HTML40_EXTENDED_ESCAPE),
            new LookupTranslator(customEscape)
        );

        log.info("HTML character escapes initialized successfully");
    }

    /**
     * ASCII 문자에 대한 이스케이프 코드 배열을 반환합니다.
     *
     * @return ASCII 문자에 대한 이스케이프 코드 배열
     */
    @Override
    public int[] getEscapeCodesForAscii() {
        return asciiEscapes;
    }

    /**
     * 주어진 문자에 대한 이스케이프 시퀀스를 반환합니다.
     *
     * @param ch 이스케이프할 문자 코드
     * @return 이스케이프된 문자열
     */
    @Override
    public SerializableString getEscapeSequence(int ch) {
        String escaped = translator.translate(Character.toString((char) ch));
        return new SerializedString(escaped);
        // 참고 - 커스터마이징이 필요없다면 아래와 같이 Apache Commons Text에서 제공하는 메서드를 써도 된다.
        // return new SerializedString(StringEscapeUtils.escapeHtml4(Character.toString((char) ch)));
    }

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer customJsonEscape() {
        return builder -> {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.getFactory().setCharacterEscapes(new HtmlCharacterEscapes());
            builder.configure(objectMapper);
        };
    }
}
