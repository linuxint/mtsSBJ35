package com.devkbil.mtssbj.manager;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.Property;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

import lombok.extern.slf4j.Slf4j;

/**
 * SpringCalendarExtractorTxt 클래스는 iCalendar (.ics) 파일 데이터를 다운로드, 파싱 및 처리하는 작업을 수행합니다.
 * 이 클래스는 이벤트를 날짜별로 그룹화하고, 프로그램 이름 및 버전과 같은 유용한 정보를 추출하며,
 * 결과를 콘솔과 파일에 출력합니다.
 * <p>
 * 주요 기능:
 * - 제공된 URL의 .ics 파일 다운로드
 * - iCalendar 데이터를 파싱하여 DTSTART, UID, SUMMARY 같은 이벤트 구성 요소 추출
 * - DTSTART 값 기준으로 이벤트를 날짜별로 그룹화
 * - SUMMARY 속성에서 프로그램 이름 및 버전을 추출하고 처리
 * - 특정 날짜 범위 내 이벤트 필터링 및 서식화된 출력
 */
@Slf4j
public class SpringCalendarExtractorTxt {

    // URL
    public static final String CALENDAR_URL = "https://calendar.spring.io/ical";

    // 날짜 포맷
    public static final String FULL_DATE_TIME_FORMAT = "yyyyMMdd'T'HHmmss'Z'";
    public static final String DATE_ONLY_FORMAT = "yyyyMMdd";

    // 출력 템플릿 (순서 정렬: UID, 날짜, 프로그램명, 버전, 기타 정보, 이벤트 제목)
    public static final String OUTPUT_TEMPLATE = "%-40s %-12s %-40s %-30s %-40s\n";
    public static final String HEADER_TEMPLATE = "%-40s %-12s %-40s %-30s %-40s";

    // 헤더
    public static final String HEADER_UID = "UID";
    public static final String HEADER_DATE = "Date";
    public static final String HEADER_PROGRAM = "Program";
    public static final String HEADER_VERSION = "Version";
    public static final String HEADER_TOPIC_TITLE = "Topic Title";

    // 기본값 (기본 출력 값)
    public static final String DEFAULT_PROGRAM_NAME = " ";
    public static final String DEFAULT_VERSION = " ";
    public static final String DEFAULT_EXTRA = " ";

    // 선 구분
    public static final String LINE_SEPARATOR = "-".repeat(140); // 보기 정리

    public static final char topLeft = '\u250C'; // ┌
    public static final char topRight = '\u2510'; // ┐
    public static final char bottomLeft = '\u2514'; // └
    public static final char bottomRight = '\u2518'; // ┘
    public static final char horizontal = '\u2500'; // ─
    public static final char vertical = '\u2502'; // │
    public static final char middleTop = '\u252C'; // ┬
    public static final char middleBottom = '\u2534'; // ┴
    public static final char middleLeft = '\u251C'; // ├
    public static final char middleRight = '\u2524'; // ┤
    public static final char center = '\u253C'; // ┼

    /**
     * URL에서 iCalendar (.ics) 파일을 다운로드.
     *
     * @param url URL 링크
     * @return InputStream (다운로드된 파일의 스트림)
     * @throws Exception 다운로드 실패 시 예외
     */
    private static InputStream downloadICalFile(String url) throws Exception {
        HttpURLConnection connection = (HttpURLConnection)new URL(url).openConnection();
        connection.setRequestMethod("GET");
        connection.connect();

        if (connection.getResponseCode() != 200) {
            throw new RuntimeException("Failed to download iCalendar file. HTTP Response Code: " + connection.getResponseCode());
        }

        return connection.getInputStream();
    }

    /**
     * VEVENT 데이터를 날짜별로 그룹화합니다.
     *
     * @param calendar iCalendar 객체
     * @return Map (날짜별로 그룹화된 이벤트)
     */
    private static Map<LocalDate, StringBuilder> groupEventsByDate(Calendar calendar) {
        Map<LocalDate, StringBuilder> eventsByDate = new TreeMap<>();

        // 날짜 형식 지정
        DateTimeFormatter fullDateTimeFormatter = DateTimeFormatter.ofPattern(FULL_DATE_TIME_FORMAT);
        DateTimeFormatter dateOnlyFormatter = DateTimeFormatter.ofPattern(DATE_ONLY_FORMAT);

        for (Component component : calendar.getComponents(Component.VEVENT)) {
            Optional<Property> dtstart = component.getProperty(Property.DTSTART);
            if (dtstart.isPresent()) {
                String dtstartValue = dtstart.get().getValue();
                LocalDate eventDate;

                try {
                    LocalDateTime dateTime = LocalDateTime.parse(dtstartValue, fullDateTimeFormatter);
                    eventDate = dateTime.toLocalDate();
                } catch (Exception e) {
                    eventDate = LocalDate.parse(dtstartValue, dateOnlyFormatter);
                }

                Optional<Property> uid = component.getProperty(Property.UID);
                String eventUID = uid.map(Property::getValue).orElse("");

                Optional<Property> summary = component.getProperty(Property.SUMMARY);
                String eventSummary = summary.map(Property::getValue).orElse("");

                // 이벤트 분석
                String[] parsedDetails = parseEventDetails(eventSummary);
                String programName = parsedDetails[0];
                String version = parsedDetails[1];

                // 날짜별로 이벤트를 그룹화
                eventsByDate.computeIfAbsent(eventDate, k -> new StringBuilder())
                    .append(String.format(OUTPUT_TEMPLATE, eventUID, eventDate, programName, version, eventSummary));
            }
        }
        return eventsByDate;
    }

    /**
     * InputStream으로부터 iCalendar 데이터를 파싱합니다.
     *
     * @param inputStream .ics 파일 스트림
     * @return Calendar 객체
     * @throws ParserException 파싱 실패
     * @throws IOException 파일 읽기 실패
     */
    private static Calendar parseICalFile(InputStream inputStream) throws ParserException, IOException {
        CalendarBuilder calendarBuilder = new CalendarBuilder();
        return calendarBuilder.build(inputStream);
    }

    /**
     * 이벤트 SUMMARY 문자열에서 프로그램 이름과 버전을 추출합니다.
     * 버전 정보(예: 서머틱 버전 번호 또는 유사한 패턴)를 식별하고 프로그램 이름과 분리합니다.
     * 'Enterprise' 형식으로 제공된 경우 이를 조정합니다.
     *
     * @param eventSummary 이벤트 요약 문자열로, 프로그램 이름 및 버전 정보를 포함할 가능성이 높습니다.
     * @return 첫 번째 요소는 프로그램 이름이고, 두 번째 요소는 버전인 문자열 배열
     */
    private static String[] parseEventDetails(String eventSummary) {
        String programName = DEFAULT_PROGRAM_NAME;   // 기본값: 프로그램 이름
        String version = DEFAULT_VERSION;           // 기본값: 버전

        // 공백으로 나뉜 토큰
        String[] tokens = eventSummary.trim().split("\\s+");

        if (tokens.length == 1) {  // 단어가 하나뿐인 경우
            if (isVersion(tokens[0])) {
                version = tokens[0];
            } else {
                programName = tokens[0];
            }
        } else {
            // 여러 단어인 경우
            StringBuilder programBuilder = new StringBuilder();
            StringBuilder versionBuilder = new StringBuilder();
            boolean versionFound = false;

            for (int i = tokens.length - 1; i >= 0; i--) {  // 뒤에서부터 탐색
                String token = tokens[i];

                if (!versionFound && isVersion(token)) {
                    versionBuilder.insert(0, token + " ");
                    versionFound = true;
                } else if (versionFound && token.startsWith("(") && token.endsWith(")")) {
                    // 버전에 괄호 처리가 추가로 있을 경우
                    versionBuilder.insert(0, token + " ");
                } else {
                    // 프로그램 이름
                    programBuilder.insert(0, token + " ");
                }
            }

            programName = programBuilder.toString().trim();
            version = versionBuilder.toString().trim();
        }

        // '(Enterprise)'가 버전에 포함되어야 함
        if (programName.endsWith("(Enterprise)")) {
            programName = programName.replace("(Enterprise)", "").trim();
            version += " (Enterprise)";
        }

        return new String[] {programName, version};
    }

    /**
     * 주어진 텍스트가 버전 패턴과 일치하는지 확인합니다.
     * 패턴은 점으로 구분된 숫자 버전(예: X.Y.Z)을 지원하며,
     * RELEASE, BETA, ALPHA와 같은 접미사나 괄호로 둘러싸인 버전도 허용합니다.
     *
     * @param text 버전 패턴과 비교할 문자열
     * @return {@code true} 텍스트가 버전 패턴과 일치하면; {@code false} 그렇지 않으면
     */
    private static boolean isVersion(String text) {
    /*
     - 숫자.숫자.숫자 형태를 우선적으로 처리 (점으로 구분됨)
     - RELEASE, BETA, ALPHA와 같은 접미사와 괄호 포함 허용
     */
        return text.matches("^(\\d+(\\.\\d+)*)([-A-Za-z0-9.]*)?(\\s*\\(.*\\))?$");
    }

    /**
     * 결과를 콘솔과 result.txf 파일로 출력합니다.
     *
     * @param eventsByDate 날짜별로 그룹화된 이벤트 Map
     * @param startDate    기준 시작 날짜 (오늘 - 5일)
     */
    private static void printGroupedEventsByDate(Map<LocalDate, StringBuilder> eventsByDate, LocalDate startDate) {
        StringBuilder outputBuilder = new StringBuilder();

        // 헤더 추가
        outputBuilder.append(String.format(HEADER_TEMPLATE, HEADER_UID, HEADER_DATE, HEADER_PROGRAM, HEADER_VERSION, HEADER_TOPIC_TITLE))
            .append(System.lineSeparator())
            .append(LINE_SEPARATOR)
            .append(System.lineSeparator());

        // 이벤트 데이터 추가
        eventsByDate.entrySet().stream()
            .filter(entry -> !entry.getKey().isBefore(startDate))
            .forEach(entry -> outputBuilder.append(entry.getValue()).append(System.lineSeparator()));

        // 콘솔에 출력
        System.out.println(outputBuilder.toString());

        // 파일에 저장
        try {
            Path resultFile = Path.of("icalendarResult.txt"); // 현재 디렉토리의 result.txf 파일
            Files.writeString(resultFile, outputBuilder.toString()); // Java NIO Files를 이용해 파일에 쓰기
            log.info("Results successfully saved to result.txf");
        } catch (IOException e) {
            log.error("An error occurred while writing to result.txf: {}", e.getMessage(), e);
        }
    }

    /**
     * Main 메서드는 iCalendar 이벤트를 처리합니다. 다음 단계를 실행합니다:
     * 1. 지정된 URL에서 iCalendar (.ics) 파일 다운로드
     * 2. 다운로드된 iCalendar 파일을 파싱하여 캘린더 이벤트 추출
     * 3. 이벤트를 날짜별로 그룹화하고 구조화된 형식으로 정리
     * 4. 현재 날짜를 기준으로 최근 5일 동안의 이벤트 필터링 후 출력
     * <p>
     * 이 과정에서 발생하는 예외 처리 및 로그 출력 수행.
     * <p>
     * 전제 조건:
     * - 유효한 iCalendar 파일의 URL 필요
     * - iCalendar 파일은 표준 형식이어야 함
     * <p>
     * 후제 조건:
     * - 이벤트는 날짜별로 콘솔 출력 및 (파싱/필터링 성공 시) 저장됩니다.
     */
    @Test
    public void main() {
        try {
            // 1단계: URL에서 .ics 파일 다운로드
            InputStream inputStream = downloadICalFile(CALENDAR_URL);

            // 2단계: iCalendar 파싱
            Calendar calendar = parseICalFile(inputStream);

            // 3단계: 날짜별로 이벤트를 그룹화
            Map<LocalDate, StringBuilder> eventsByDate = groupEventsByDate(calendar);

            // 4단계: 오늘 기준 5일 전 이후 일정만 출력
            printGroupedEventsByDate(eventsByDate, LocalDate.now().minusDays(5));

        } catch (Exception e) {
            log.error("An error occurred: {}", e.getMessage(), e);
        }
    }
}