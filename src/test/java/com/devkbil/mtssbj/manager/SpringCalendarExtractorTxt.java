package com.devkbil.mtssbj.manager;

import lombok.extern.slf4j.Slf4j;

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

import static com.devkbil.mtssbj.manager.SpringCalendarConstants.*;

@Slf4j
public class SpringCalendarExtractorTxt {

    @Test
    public void main() {
        try {
            // Step 1: URL에서 .ics 파일 다운로드
            InputStream inputStream = downloadICalFile(CALENDAR_URL);

            // Step 2: iCalendar 파싱
            Calendar calendar = parseICalFile(inputStream);

            // Step 3: 날짜별로 이벤트를 그룹화
            Map<LocalDate, StringBuilder> eventsByDate = groupEventsByDate(calendar);

            // Step 4: 오늘 기준 5일 전 이후 일정만 출력
            printGroupedEventsByDate(eventsByDate, LocalDate.now().minusDays(5));

        } catch (Exception e) {
            log.error("An error occurred: {}", e.getMessage(), e);
        }
    }

    /**
     * URL에서 iCalendar (.ics) 파일을 다운로드.
     *
     * @param url URL 링크
     * @return InputStream (다운로드된 파일의 스트림)
     * @throws Exception 다운로드 실패 시 예외
     */
    private static InputStream downloadICalFile(String url) throws Exception {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("GET");
        connection.connect();

        if (connection.getResponseCode() != 200) {
            throw new RuntimeException("Failed to download iCalendar file. HTTP Response Code: " + connection.getResponseCode());
        }

        return connection.getInputStream();
    }

    /**
     * InputStream으로부터 iCalendar 데이터를 파싱
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
     * VEVENT 데이터를 날짜별로 그룹화
     *
     * @param calendar iCalendar 객체
     * @return Map (날짜별로 그룹화된 이벤트)
     */
    private static Map<LocalDate, StringBuilder> groupEventsByDate(Calendar calendar) {
        Map<LocalDate, StringBuilder> eventsByDate = new TreeMap<>();

        // Date formatters
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

        return new String[] { programName, version };
    }

    /**
     * 버전인지 판별하는 메서드: 숫자, 문자, 점(.), 하이픈(-)에 괄호 포함 가능
     * ex) 4.28.0.RELEASE, 1.13.11, 1.14.4 (Enterprise) 등이 버전으로 인식
     */
    private static boolean isVersion(String text) {
    /*
     - 숫자.숫자.숫자 형태를 우선적으로 처리 (점으로 구분됨)
     - RELEASE, BETA, ALPHA와 같은 접미사와 괄호 포함 허용
     */
        return text.matches("^(\\d+(\\.\\d+)*)([-A-Za-z0-9.]*)?(\\s*\\(.*\\))?$");    }

    /**
     * 결과를 화면과 result.txf 파일로 출력
     *
     * @param eventsByDate 날짜별로 그룹화된 이벤트 Map
     * @param startDate    시작 기준 날짜 (오늘 - 5일)
     */
    private static void printGroupedEventsByDate(Map<LocalDate, StringBuilder> eventsByDate, LocalDate startDate) {
        StringBuilder outputBuilder = new StringBuilder();

        // Header 추가
        outputBuilder.append(String.format(HEADER_TEMPLATE, HEADER_UID, HEADER_DATE, HEADER_PROGRAM, HEADER_VERSION, HEADER_TOPIC_TITLE))
                .append(System.lineSeparator())
                .append(LINE_SEPARATOR)
                .append(System.lineSeparator());

        // 이벤트 데이터 추가
        eventsByDate.entrySet().stream()
                .filter(entry -> !entry.getKey().isBefore(startDate))
                .forEach(entry -> outputBuilder.append(entry.getValue()).append(System.lineSeparator()));

        // 화면에 출력
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
}