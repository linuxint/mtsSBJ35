package com.devkbil.mtssbj.manager;

public class SpringCalendarConstants {

    // URL
    public static final String CALENDAR_URL = "https://calendar.spring.io/ical";

    // Date formats
    public static final String FULL_DATE_TIME_FORMAT = "yyyyMMdd'T'HHmmss'Z'";
    public static final String DATE_ONLY_FORMAT = "yyyyMMdd";

    // Output templates (순서 정렬: UID, 날짜, 프로그램명, 버전, 기타 정보, 이벤트 제목)
    public static final String OUTPUT_TEMPLATE = "%-40s %-12s %-40s %-30s %-40s\n";
    public static final String HEADER_TEMPLATE = "%-40s %-12s %-40s %-30s %-40s";

    // Headers
    public static final String HEADER_UID = "UID";
    public static final String HEADER_DATE = "Date";
    public static final String HEADER_PROGRAM = "Program";
    public static final String HEADER_VERSION = "Version";
    public static final String HEADER_TOPIC_TITLE = "Topic Title";

    // Default values (기본 출력 값)
    public static final String DEFAULT_PROGRAM_NAME = " ";
    public static final String DEFAULT_VERSION = " ";
    public static final String DEFAULT_EXTRA = " ";

    // Line separator
    public static final String LINE_SEPARATOR = "-".repeat(140); // 뷰 정리

    public static final char topLeft = '\u250C';    // ┌
    public static final char topRight = '\u2510';   // ┐
    public static final char bottomLeft = '\u2514'; // └
    public static final char bottomRight = '\u2518';// ┘
    public static final char horizontal = '\u2500'; // ─
    public static final char vertical = '\u2502';   // │
    public static final char middleTop = '\u252C';  // ┬
    public static final char middleBottom = '\u2534';// ┴
    public static final char middleLeft = '\u251C';  // ├
    public static final char middleRight = '\u2524'; // ┤
    public static final char center = '\u253C';      // ┼

}