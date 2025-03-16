package com.devkbil.common.util;

import com.devkbil.mtssbj.schedule.DateVO;
import com.devkbil.mtssbj.schedule.MonthVO;

import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;
import java.util.Locale;

import lombok.extern.slf4j.Slf4j;

/**
 * 날짜 및 시간 관련 유틸리티 메서드를 제공하는 서비스 클래스입니다.
 * Java 8+ 날짜/시간 API를 사용하여 구현되었습니다.
 * 기존 DateUtil 클래스의 기능을 현대적인 방식으로 재구현하였습니다.
 */
@Slf4j
@Service
public class ModernDateUtil {

    // 자주 사용되는 날짜 형식 상수
    private static final String YYYY_MM_DD_BAR = "yyyy-MM-dd";
    private static final String YYYY_MM_DD_DOT = "yyyy.MM.dd";
    private static final String YYYY_MM_DD_SLASH = "yyyy/MM/dd";
    private static final String YYYY_MM_DD_HH_MM_SS_BAR = "yyyy-MM-dd HH:mm:ss";
    private static final String YYYY_MM_DD_HH_MM_SS_STRIP = "yyyyMMddHHmmss";
    private static final String YYYY_MM_DD_STRIP = "yyyyMMdd";
    private static final String HH_MM_SS = "HH:mm:ss";

    private static final Locale DEFAULT_LOCALE = Locale.KOREA;
    private static final ZoneId DEFAULT_ZONE_ID = ZoneId.systemDefault();

    private static final String[] DAY_ARR = new String[] {"일", "월", "화", "수", "목", "금", "토"};

    // 날짜 변환 유틸리티 메서드

    /**
     * Date 객체를 LocalDate 객체로 변환합니다.
     *
     * @param date 변환할 Date 객체
     * @return 변환된 LocalDate 객체
     */
    public static LocalDate toLocalDate(Date date) {
        if (date == null) {
            return null;
        }
        return date.toInstant().atZone(DEFAULT_ZONE_ID).toLocalDate();
    }

    /**
     * Date 객체를 LocalDateTime 객체로 변환합니다.
     *
     * @param date 변환할 Date 객체
     * @return 변환된 LocalDateTime 객체
     */
    public static LocalDateTime toLocalDateTime(Date date) {
        if (date == null) {
            return null;
        }
        return date.toInstant().atZone(DEFAULT_ZONE_ID).toLocalDateTime();
    }

    /**
     * LocalDate 객체를 Date 객체로 변환합니다.
     *
     * @param localDate 변환할 LocalDate 객체
     * @return 변환된 Date 객체
     */
    public static Date toDate(LocalDate localDate) {
        if (localDate == null) {
            return null;
        }
        return Date.from(localDate.atStartOfDay(DEFAULT_ZONE_ID).toInstant());
    }

    /**
     * LocalDateTime 객체를 Date 객체로 변환합니다.
     *
     * @param localDateTime 변환할 LocalDateTime 객체
     * @return 변환된 Date 객체
     */
    public static Date toDate(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return Date.from(localDateTime.atZone(DEFAULT_ZONE_ID).toInstant());
    }

    /**
     * 표준 날짜 형식(yyyy-MM-dd)을 반환합니다.
     *
     * @return 표준 날짜 형식 문자열
     */
    public static String getYyyyMMddBar() {
        return YYYY_MM_DD_BAR;
    }

    /**
     * 문자열을 기본 형식(yyyy-MM-dd)의 Date 객체로 변환합니다.
     *
     * @param dateStr 변환할 날짜 문자열
     * @return 변환된 Date 객체
     */
    public static Date convDate(String dateStr) {
        if (StringUtils.hasText(dateStr)) {
            try {
                LocalDate localDate = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern(YYYY_MM_DD_BAR));
                return toDate(localDate);
            } catch (DateTimeParseException e) {
                log.error("Failed to parse date string: {}", dateStr, e);
            }
        }
        return null;
    }

    /**
     * 문자열을 지정된 형식의 Date 객체로 변환합니다.
     *
     * @param dateStr 변환할 날짜 문자열
     * @param format 날짜 형식
     * @return 변환된 Date 객체
     */
    public static Date convDate(String dateStr, String format) {
        if (StringUtils.hasText(dateStr) && StringUtils.hasText(format)) {
            try {
                LocalDate localDate = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern(format));
                return toDate(localDate);
            } catch (DateTimeParseException e) {
                log.error("Failed to parse date string: {} with format: {}", dateStr, format, e);
            }
        }
        return null;
    }

    /**
     * MonthVO 객체의 월 값을 검증하고 필요한 경우 조정합니다.
     *
     * @param monthVO 검증할 MonthVO 객체
     * @return 검증 및 조정된 MonthVO 객체
     */
    public static MonthVO monthValid(MonthVO monthVO) {
        Assert.notNull(monthVO, "MonthVO must not be null");
        log.debug("Validating month: {}/{}", monthVO.getYear(), monthVO.getMonth());

        int year = Integer.parseInt(monthVO.getYear());
        int month = Integer.parseInt(monthVO.getMonth());

        if (month < 1) {
            month += 12;
            year -= 1;
        }
        if (month > 12) {
            month -= 12;
            year += 1;
        }

        monthVO.setYear(String.valueOf(year));
        monthVO.setMonth(String.valueOf(month));

        return monthVO;
    }

    /**
     * 현재 날짜를 Date 객체로 반환합니다.
     *
     * @return 현재 날짜의 Date 객체
     */
    public static Date getToday() {
        return toDate(LocalDate.now());
    }

    /**
     * 문자열을 Date 객체로 변환합니다.
     *
     * @param dateStr 변환할 날짜 문자열 (yyyy-MM-dd 형식)
     * @return 변환된 Date 객체
     */
    public static Date stringToDate(String dateStr) {
        if (!StringUtils.hasText(dateStr)) {
            return null;
        }

        try {
            LocalDate localDate = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern(YYYY_MM_DD_BAR));
            return toDate(localDate);
        } catch (DateTimeParseException e) {
            log.error("Failed to parse date string: {}", dateStr, e);
            return null;
        }
    }

    /**
     * Date 객체를 문자열로 변환합니다.
     *
     * @param date 변환할 Date 객체
     * @return 변환된 날짜 문자열 (yyyy-MM-dd 형식)
     */
    public static String date2Str(Date date) {
        if (date == null) {
            return "";
        }

        LocalDate localDate = toLocalDate(date);
        return localDate.format(DateTimeFormatter.ofPattern(YYYY_MM_DD_BAR));
    }

    /**
     * 문자열을 Date 객체로 변환합니다.
     *
     * @param dateStr 변환할 날짜 문자열 (yyyy-MM-dd 형식)
     * @return 변환된 Date 객체
     */
    public static Date str2Date(String dateStr) {
        if (!StringUtils.hasText(dateStr)) {
            return null;
        }

        try {
            // 날짜 형식 자동 감지 및 변환
            String cleanDateStr = dateStr.trim().replace("/", "-").replace(".", "-");

            // 하이픈이 없는 경우 (예: 20240315)
            if (!cleanDateStr.contains("-")) {
                if (cleanDateStr.length() == 8) {
                    cleanDateStr = cleanDateStr.substring(0, 4) + "-" + 
                                  cleanDateStr.substring(4, 6) + "-" + 
                                  cleanDateStr.substring(6, 8);
                }
            }

            LocalDate localDate = LocalDate.parse(cleanDateStr, DateTimeFormatter.ofPattern(YYYY_MM_DD_BAR));
            return toDate(localDate);
        } catch (DateTimeParseException e) {
            log.error("Failed to parse date string: {}", dateStr, e);
            return null;
        }
    }

    /**
     * Date 객체를 DateVO 객체로 변환합니다.
     *
     * @param date 변환할 Date 객체
     * @return 변환된 DateVO 객체
     */
    public static DateVO date2VO(Date date) {
        if (date == null) {
            return null;
        }

        LocalDate localDate = toLocalDate(date);
        DateVO dateVO = new DateVO();
        dateVO.setYear(localDate.getYear());
        dateVO.setMonth(localDate.getMonthValue());
        dateVO.setDay(localDate.getDayOfMonth());

        // 요일 설정 (1: 일요일, 2: 월요일, ..., 7: 토요일)
        int dayOfWeek = localDate.getDayOfWeek().getValue() % 7 + 1;
        dateVO.setWeek(String.valueOf(dayOfWeek));

        return dateVO;
    }

    /**
     * Date 객체에서 연도를 추출합니다.
     *
     * @param date 날짜 객체
     * @return 연도 값
     */
    public static Integer getYear(Date date) {
        if (date == null) {
            return null;
        }

        LocalDate localDate = toLocalDate(date);
        return localDate.getYear();
    }

    /**
     * Date 객체에서 월을 추출합니다.
     *
     * @param date 날짜 객체
     * @return 월 값 (1-12)
     */
    public static Integer getMonth(Date date) {
        if (date == null) {
            return null;
        }

        LocalDate localDate = toLocalDate(date);
        return localDate.getMonthValue();
    }

    /**
     * Date 객체에서 요일을 추출합니다.
     *
     * @param date 날짜 객체
     * @return 요일 값 (1: 일요일, 2: 월요일, ..., 7: 토요일)
     */
    public static Integer getDayOfWeek(Date date) {
        if (date == null) {
            return null;
        }

        LocalDate localDate = toLocalDate(date);
        // Java의 DayOfWeek는 1(월요일)부터 7(일요일)이므로 변환 필요
        return localDate.getDayOfWeek().getValue() % 7 + 1;
    }

    /**
     * Date 객체에서 해당 월의 몇 번째 주인지 계산합니다.
     *
     * @param date 날짜 객체
     * @return 해당 월의 주차 (1부터 시작)
     */
    public static Integer getWeekOfMonth(Date date) {
        if (date == null) {
            return null;
        }

        LocalDate localDate = toLocalDate(date);
        // 해당 월의 첫 날짜
        LocalDate firstDayOfMonth = localDate.withDayOfMonth(1);
        // 첫 날짜의 요일 (1: 월요일, ..., 7: 일요일)
        int firstDayOfWeek = firstDayOfMonth.getDayOfWeek().getValue();
        // 일요일을 1로 변환 (1: 일요일, 2: 월요일, ..., 7: 토요일)
        firstDayOfWeek = firstDayOfWeek % 7 + 1;

        // 현재 날짜의 일자
        int dayOfMonth = localDate.getDayOfMonth();

        // 주차 계산: (일자 + 첫 날짜의 요일 - 2) / 7 + 1
        // 첫 날짜가 일요일이면 첫 주, 아니면 첫 날짜의 요일에 따라 조정
        return (dayOfMonth + firstDayOfWeek - 2) / 7 + 1;
    }

    /**
     * 요일 인덱스에 해당하는 요일 문자열을 반환합니다.
     *
     * @param idx 요일 인덱스 (1: 일요일, 2: 월요일, ..., 7: 토요일)
     * @return 요일 문자열 ("일", "월", ..., "토")
     */
    public static String getWeekString(Integer idx) {
        if (idx == null || idx < 1 || idx > 7) {
            return "";
        }
        return DAY_ARR[idx - 1];
    }

    /**
     * 주의 첫 날짜(일요일)를 반환합니다.
     *
     * @param date 기준 날짜
     * @return 주의 첫 날짜
     */
    public static Date getFirstOfWeek(Date date) {
        if (date == null) {
            return null;
        }

        LocalDate localDate = toLocalDate(date);
        // 일요일이 주의 첫 날이므로 이전 일요일 또는 현재 날짜가 일요일이면 현재 날짜를 반환
        LocalDate firstDay = localDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
        return toDate(firstDay);
    }

    /**
     * 주의 마지막 날짜(토요일)를 반환합니다.
     *
     * @param date 기준 날짜
     * @return 주의 마지막 날짜
     */
    public static Date getLastOfWeek(Date date) {
        if (date == null) {
            return null;
        }

        LocalDate localDate = toLocalDate(date);
        // 토요일이 주의 마지막 날이므로 다음 토요일 또는 현재 날짜가 토요일이면 현재 날짜를 반환
        LocalDate lastDay = localDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY));
        return toDate(lastDay);
    }

    /**
     * 두 날짜 사이의 일수 차이를 계산합니다.
     *
     * @param date1 첫 번째 날짜
     * @param date2 두 번째 날짜
     * @return 일수 차이
     */
    public static Integer dateDiff(Date date1, Date date2) {
        if (date1 == null || date2 == null) {
            return null;
        }

        LocalDate localDate1 = toLocalDate(date1);
        LocalDate localDate2 = toLocalDate(date2);

        return (int) ChronoUnit.DAYS.between(localDate1, localDate2);
    }

    /**
     * 날짜에 일수를 더합니다.
     *
     * @param date 기준 날짜
     * @param days 더할 일수
     * @return 계산된 날짜
     */
    public static Date dateAdd(Date date, Integer days) {
        if (date == null || days == null) {
            return null;
        }

        LocalDate localDate = toLocalDate(date);
        return toDate(localDate.plusDays(days));
    }

    /**
     * 날짜에 월수를 더합니다.
     *
     * @param date 기준 날짜
     * @param months 더할 월수
     * @return 계산된 날짜
     */
    public static Date monthAdd(Date date, Integer months) {
        if (date == null || months == null) {
            return null;
        }

        LocalDate localDate = toLocalDate(date);
        return toDate(localDate.plusMonths(months));
    }

    /**
     * 기준 날짜에 추가 날짜의 일수를 더합니다.
     * 추가 날짜의 시간 정보는 무시하고 일수만 계산합니다.
     *
     * @param baseDate 기준 날짜
     * @param additionalDate 추가할 날짜 (일수로 변환됨)
     * @return 계산된 날짜
     */
    public static Date addDate(Date baseDate, Date additionalDate) {
        if (baseDate == null || additionalDate == null) {
            return null;
        }

        LocalDate localBaseDate = toLocalDate(baseDate);
        LocalDate localAdditionalDate = toLocalDate(additionalDate);

        // 1970-01-01부터의 일수 차이를 계산
        LocalDate epoch = LocalDate.of(1970, 1, 1);
        long daysToAdd = ChronoUnit.DAYS.between(epoch, localAdditionalDate);

        return toDate(localBaseDate.plusDays(daysToAdd));
    }

    /**
     * 날짜에 일수를 더합니다.
     *
     * @param originalDate 기준 날짜
     * @param daysToAdd 더할 일수
     * @return 계산된 날짜
     */
    public static Date addDay(Date originalDate, long daysToAdd) {
        if (originalDate == null) {
            return null;
        }

        LocalDate localDate = toLocalDate(originalDate);
        return toDate(localDate.plusDays(daysToAdd));
    }

    /**
     * 문자열 형태의 날짜에 일수를 더한 후 문자열로 반환합니다.
     *
     * @param basicDate 기준 날짜 문자열 (yyyy-MM-dd 형식)
     * @param addDay 더할 일수
     * @return 계산된 날짜 문자열 (yyyy-MM-dd 형식)
     */
    public static String addDay(String basicDate, int addDay) {
        if (!StringUtils.hasText(basicDate)) {
            return "";
        }

        try {
            // 날짜 형식 자동 감지 및 변환
            String cleanDateStr = basicDate.trim().replace("/", "-").replace(".", "-");

            // 하이픈이 없는 경우 (예: 20240315)
            if (!cleanDateStr.contains("-")) {
                if (cleanDateStr.length() == 8) {
                    cleanDateStr = cleanDateStr.substring(0, 4) + "-" + 
                                  cleanDateStr.substring(4, 6) + "-" + 
                                  cleanDateStr.substring(6, 8);
                }
            }

            LocalDate localDate = LocalDate.parse(cleanDateStr, DateTimeFormatter.ofPattern(YYYY_MM_DD_BAR));
            localDate = localDate.plusDays(addDay);

            return localDate.format(DateTimeFormatter.ofPattern(YYYY_MM_DD_BAR));
        } catch (DateTimeParseException e) {
            log.error("Failed to parse date string: {}", basicDate, e);
            return "";
        }
    }

    /**
     * 날짜 문자열의 형식을 확인하고 표준 형식으로 변환합니다.
     *
     * @param dateStr 확인할 날짜 문자열
     * @return 표준 형식으로 변환된 날짜 문자열 (yyyy-MM-dd)
     */
    public static String checkDateType(String dateStr) {
        if (!StringUtils.hasText(dateStr)) {
            return "";
        }

        String result = dateStr.trim();

        // 구분자 제거 (예: 2024-03-15 -> 20240315)
        if (result.contains("-") || result.contains("/") || result.contains(".")) {
            result = result.replace("-", "").replace("/", "").replace(".", "");
        }

        // 길이가 8이 아니면 유효하지 않은 날짜 형식
        if (result.length() != 8) {
            return "";
        }

        // 표준 형식으로 변환 (yyyy-MM-dd)
        return result.substring(0, 4) + "-" + result.substring(4, 6) + "-" + result.substring(6, 8);
    }

    /**
     * 지정된 연도와 월의 마지막 날짜를 반환합니다.
     *
     * @param year 연도
     * @param month 월 (1-12)
     * @return 해당 월의 마지막 날짜
     */
    public static int getEndDayOfMonth(int year, int month) {
        LocalDate date = LocalDate.of(year, month, 1);
        return date.lengthOfMonth();
    }

    /**
     * 날짜를 지정된 형식의 문자열로 변환합니다.
     *
     * @param date 변환할 날짜
     * @param format 날짜 형식
     * @return 형식화된 날짜 문자열
     */
    public static String formatDate(Date date, String format) {
        if (date == null || !StringUtils.hasText(format)) {
            return "";
        }

        LocalDate localDate = toLocalDate(date);
        return localDate.format(DateTimeFormatter.ofPattern(format));
    }

    /**
     * 현재 시스템의 GMT 날짜를 반환합니다.
     *
     * @return GMT 기준 현재 날짜
     */
    public static Date getGMTDate() {
        // TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
        // Date gmtDate = new Date();
        // TimeZone.setDefault(TimeZone.getTimeZone(DEFAULT_ZONE_ID.getId()));
        // return gmtDate;

        // 대체 구현: LocalDateTime을 사용하여 GMT 날짜 생성
        LocalDateTime gmtDateTime = LocalDateTime.now(ZoneId.of("GMT"));
        return toDate(gmtDateTime.toLocalDate());
    }

    /**
     * 현재 시스템의 GMT 시간을 반환합니다.
     *
     * @return GMT 기준 현재 시간 (시간)
     */
    public static int getGMTHour() {
        // 대체 구현: LocalDateTime을 사용하여 GMT 시간 구하기
        LocalDateTime gmtDateTime = LocalDateTime.now(ZoneId.of("GMT"));
        return gmtDateTime.getHour();
    }

    /**
     * 지정된 날짜의 GMT 오프셋을 밀리초 단위로 반환합니다.
     *
     * @param date 날짜
     * @return GMT 오프셋 (밀리초)
     */
    public static long getGMTOffset(Date date) {
        if (date == null) {
            return 0;
        }

        // 대체 구현: 시스템 기본 시간대의 오프셋 구하기
        return DEFAULT_ZONE_ID.getRules().getOffset(LocalDateTime.now()).getTotalSeconds() * 1000L;
    }

    /**
     * 월 인덱스에 해당하는 월 이름을 반환합니다.
     *
     * @param monthIndex 월 인덱스 (1-12)
     * @return 월 이름 (영문)
     */
    public static String getMonthName(int monthIndex) {
        if (monthIndex < 1 || monthIndex > 12) {
            return "";
        }

        return LocalDate.of(2000, monthIndex, 1)
                .format(DateTimeFormatter.ofPattern("MMMM", Locale.ENGLISH));
    }

    /**
     * 날짜의 월 이름을 반환합니다.
     *
     * @param date 날짜
     * @return 월 이름 (영문)
     */
    public static String getMonthName(Date date) {
        if (date == null) {
            return "";
        }

        LocalDate localDate = toLocalDate(date);
        return localDate.format(DateTimeFormatter.ofPattern("MMMM", Locale.ENGLISH));
    }

    /**
     * 서버의 GMT 오프셋을 시간 단위로 반환합니다.
     *
     * @return 서버의 GMT 오프셋 (시간)
     */
    public static int getServerGMTOffset() {
        // 대체 구현: 시스템 기본 시간대의 오프셋 구하기
        return DEFAULT_ZONE_ID.getRules().getOffset(LocalDateTime.now()).getTotalSeconds() / 3600;
    }

    /**
     * 현재 시간의 시간을 반환합니다.
     *
     * @return 현재 시간 (시간)
     */
    public static int getHour() {
        return LocalDateTime.now().getHour();
    }

    /**
     * 현재 시간의 분을 반환합니다.
     *
     * @return 현재 시간 (분)
     */
    public static int getMinute() {
        return LocalDateTime.now().getMinute();
    }

    /**
     * 현재 날짜의 연도를 반환합니다.
     *
     * @return 현재 연도
     */
    public static int getYear() {
        return LocalDate.now().getYear();
    }

    /**
     * 현재 날짜의 월을 반환합니다.
     *
     * @return 현재 월 (1-12)
     */
    public static int getMonth() {
        return LocalDate.now().getMonthValue();
    }

    /**
     * 현재 날짜의 일을 반환합니다.
     *
     * @return 현재 일
     */
    public static int getDate() {
        return LocalDate.now().getDayOfMonth();
    }

    /**
     * 날짜를 지정된 형식의 문자열로 변환합니다.
     *
     * @param outFormat 출력 형식
     * @param date 변환할 날짜
     * @return 형식화된 날짜 문자열
     */
    public static String getDateFormat(String outFormat, Date date) {
        if (date == null || !StringUtils.hasText(outFormat)) {
            return "";
        }

        try {
            LocalDateTime localDateTime = toLocalDateTime(date);
            return localDateTime.format(DateTimeFormatter.ofPattern(outFormat));
        } catch (Exception e) {
            log.error("Failed to format date: {} with format: {}", date, outFormat, e);
            return "";
        }
    }

    /**
     * 문자열 형태의 날짜를 다른 형식의 문자열로 변환합니다.
     *
     * @param inDate 입력 날짜 문자열
     * @param inFormat 입력 날짜 형식
     * @param outFormat 출력 날짜 형식
     * @return 변환된 날짜 문자열
     */
    public static String getDateFormat(String inDate, String inFormat, String outFormat) {
        if (!StringUtils.hasText(inDate) || !StringUtils.hasText(inFormat) || !StringUtils.hasText(outFormat)) {
            return "";
        }

        try {
            LocalDateTime localDateTime = LocalDateTime.parse(inDate, DateTimeFormatter.ofPattern(inFormat));
            return localDateTime.format(DateTimeFormatter.ofPattern(outFormat));
        } catch (DateTimeParseException e) {
            // LocalDate로 파싱 시도
            try {
                LocalDate localDate = LocalDate.parse(inDate, DateTimeFormatter.ofPattern(inFormat));
                return localDate.format(DateTimeFormatter.ofPattern(outFormat));
            } catch (DateTimeParseException ex) {
                log.error("Failed to parse date string: {} with format: {}", inDate, inFormat, ex);
                return "";
            }
        } catch (Exception e) {
            log.error("Failed to format date: {} with format: {}", inDate, outFormat, e);
            return "";
        }
    }

    /**
     * 날짜 문자열을 지정된 유형에 따라 형식화합니다.
     *
     * @param date 날짜 문자열
     * @param type 형식 유형 (1: yyyy-MM-dd, 2: yyyy/MM/dd, 3: yyyy.MM.dd)
     * @return 형식화된 날짜 문자열
     */
    public static String getDateFormat(String date, String type) {
        if (!StringUtils.hasText(date) || !StringUtils.hasText(type)) {
            return "";
        }

        // 날짜 문자열 정제
        String cleanDate = date.trim().replace("-", "").replace("/", "").replace(".", "");

        // 길이가 8이 아니면 유효하지 않은 날짜 형식
        if (cleanDate.length() != 8) {
            return "";
        }

        String year = cleanDate.substring(0, 4);
        String month = cleanDate.substring(4, 6);
        String day = cleanDate.substring(6, 8);

        // 유형에 따라 형식 지정
        switch (type) {
            case "1":
                return year + "-" + month + "-" + day;
            case "2":
                return year + "/" + month + "/" + day;
            case "3":
                return year + "." + month + "." + day;
            default:
                return year + "-" + month + "-" + day;
        }
    }

    /**
     * 날짜 문자열을 지정된 유형에 따라 형식화합니다. (getDateFormat의 대체 구현)
     *
     * @param date 날짜 문자열
     * @param type 형식 유형 (1: yyyy-MM-dd, 2: yyyy/MM/dd, 3: yyyy.MM.dd)
     * @return 형식화된 날짜 문자열
     */
    public static String getDateFormat2(String date, String type) {
        return getDateFormat(date, type);
    }

    /**
     * 시간 문자열을 지정된 유형에 따라 형식화합니다.
     *
     * @param time 시간 문자열 (HHmmss 형식)
     * @param type 형식 유형 (1: HH:mm:ss, 2: HH:mm)
     * @return 형식화된 시간 문자열
     */
    public static String getTimeFormat(String time, String type) {
        if (!StringUtils.hasText(time) || !StringUtils.hasText(type)) {
            return "";
        }

        // 시간 문자열 정제
        String cleanTime = time.trim().replace(":", "");

        // 길이가 6이 아니면 유효하지 않은 시간 형식
        if (cleanTime.length() != 6) {
            return "";
        }

        String hour = cleanTime.substring(0, 2);
        String minute = cleanTime.substring(2, 4);
        String second = cleanTime.substring(4, 6);

        // 유형에 따라 형식 지정
        switch (type) {
            case "1":
                return hour + ":" + minute + ":" + second;
            case "2":
                return hour + ":" + minute;
            default:
                return hour + ":" + minute + ":" + second;
        }
    }

    /**
     * 날짜 문자열에 일, 시간, 분을 더한 후 지정된 형식으로 반환합니다.
     *
     * @param inDate 입력 날짜 문자열
     * @param inFormat 입력 날짜 형식
     * @param outFormat 출력 날짜 형식
     * @param day 더할 일수
     * @param hour 더할 시간
     * @param minute 더할 분
     * @return 계산된 날짜 문자열
     */
    public static String getFormattedDateAdd(String inDate, String inFormat, String outFormat, int day, int hour, int minute) {
        if (!StringUtils.hasText(inDate) || !StringUtils.hasText(inFormat) || !StringUtils.hasText(outFormat)) {
            return "";
        }

        try {
            LocalDateTime localDateTime;
            try {
                // LocalDateTime으로 파싱 시도
                localDateTime = LocalDateTime.parse(inDate, DateTimeFormatter.ofPattern(inFormat));
            } catch (DateTimeParseException e) {
                // LocalDate로 파싱 시도
                LocalDate localDate = LocalDate.parse(inDate, DateTimeFormatter.ofPattern(inFormat));
                localDateTime = localDate.atStartOfDay();
            }

            // 일, 시간, 분 더하기
            localDateTime = localDateTime.plusDays(day).plusHours(hour).plusMinutes(minute);

            return localDateTime.format(DateTimeFormatter.ofPattern(outFormat));
        } catch (Exception e) {
            log.error("Failed to add date: {} with format: {}", inDate, inFormat, e);
            return "";
        }
    }

    /**
     * 날짜 문자열에 연도를 더한 후 지정된 형식으로 반환합니다.
     *
     * @param inDate 입력 날짜 문자열
     * @param inFormat 입력 날짜 형식
     * @param outFormat 출력 날짜 형식
     * @param year 더할 연도
     * @return 계산된 날짜 문자열
     */
    public static String getFormattedDateYearAdd(String inDate, String inFormat, String outFormat, int year) {
        if (!StringUtils.hasText(inDate) || !StringUtils.hasText(inFormat) || !StringUtils.hasText(outFormat)) {
            return "";
        }

        try {
            LocalDateTime localDateTime;
            try {
                // LocalDateTime으로 파싱 시도
                localDateTime = LocalDateTime.parse(inDate, DateTimeFormatter.ofPattern(inFormat));
            } catch (DateTimeParseException e) {
                // LocalDate로 파싱 시도
                LocalDate localDate = LocalDate.parse(inDate, DateTimeFormatter.ofPattern(inFormat));
                localDateTime = localDate.atStartOfDay();
            }

            // 연도 더하기
            localDateTime = localDateTime.plusYears(year);

            return localDateTime.format(DateTimeFormatter.ofPattern(outFormat));
        } catch (Exception e) {
            log.error("Failed to add years: {} with format: {}", inDate, inFormat, e);
            return "";
        }
    }

    /**
     * 날짜 문자열에 월을 더한 후 지정된 형식으로 반환합니다.
     *
     * @param inDate 입력 날짜 문자열
     * @param inFormat 입력 날짜 형식
     * @param outFormat 출력 날짜 형식
     * @param month 더할 월
     * @return 계산된 날짜 문자열
     */
    public static String getFormattedDateMonthAdd(String inDate, String inFormat, String outFormat, int month) {
        if (!StringUtils.hasText(inDate) || !StringUtils.hasText(inFormat) || !StringUtils.hasText(outFormat)) {
            return "";
        }

        try {
            LocalDateTime localDateTime;
            try {
                // LocalDateTime으로 파싱 시도
                localDateTime = LocalDateTime.parse(inDate, DateTimeFormatter.ofPattern(inFormat));
            } catch (DateTimeParseException e) {
                // LocalDate로 파싱 시도
                LocalDate localDate = LocalDate.parse(inDate, DateTimeFormatter.ofPattern(inFormat));
                localDateTime = localDate.atStartOfDay();
            }

            // 월 더하기
            localDateTime = localDateTime.plusMonths(month);

            return localDateTime.format(DateTimeFormatter.ofPattern(outFormat));
        } catch (Exception e) {
            log.error("Failed to add months: {} with format: {}", inDate, inFormat, e);
            return "";
        }
    }

    /**
     * 날짜 문자열에 시간을 더한 후 지정된 형식으로 반환합니다.
     *
     * @param inDate 입력 날짜 문자열
     * @param inFormat 입력 날짜 형식
     * @param outFormat 출력 날짜 형식
     * @param hour 더할 시간
     * @return 계산된 날짜 문자열
     */
    public static String getFormattedDateHourAdd(String inDate, String inFormat, String outFormat, int hour) {
        if (!StringUtils.hasText(inDate) || !StringUtils.hasText(inFormat) || !StringUtils.hasText(outFormat)) {
            return "";
        }

        try {
            LocalDateTime localDateTime;
            try {
                // LocalDateTime으로 파싱 시도
                localDateTime = LocalDateTime.parse(inDate, DateTimeFormatter.ofPattern(inFormat));
            } catch (DateTimeParseException e) {
                // LocalDate로 파싱 시도
                LocalDate localDate = LocalDate.parse(inDate, DateTimeFormatter.ofPattern(inFormat));
                localDateTime = localDate.atStartOfDay();
            }

            // 시간 더하기
            localDateTime = localDateTime.plusHours(hour);

            return localDateTime.format(DateTimeFormatter.ofPattern(outFormat));
        } catch (Exception e) {
            log.error("Failed to add hours: {} with format: {}", inDate, inFormat, e);
            return "";
        }
    }

    /**
     * 두 날짜 문자열 사이의 시간 차이를 밀리초 단위로 계산합니다.
     *
     * @param inDate1 첫 번째 날짜 문자열
     * @param inFormat1 첫 번째 날짜 형식
     * @param inDate2 두 번째 날짜 문자열
     * @param inFormat2 두 번째 날짜 형식
     * @return 두 날짜 사이의 시간 차이 (밀리초)
     */
    public static long getComputedDate(String inDate1, String inFormat1, String inDate2, String inFormat2) {
        if (!StringUtils.hasText(inDate1) || !StringUtils.hasText(inFormat1) || 
            !StringUtils.hasText(inDate2) || !StringUtils.hasText(inFormat2)) {
            return 0;
        }

        try {
            LocalDateTime localDateTime1;
            LocalDateTime localDateTime2;

            try {
                // LocalDateTime으로 파싱 시도
                localDateTime1 = LocalDateTime.parse(inDate1, DateTimeFormatter.ofPattern(inFormat1));
            } catch (DateTimeParseException e) {
                // LocalDate로 파싱 시도
                LocalDate localDate = LocalDate.parse(inDate1, DateTimeFormatter.ofPattern(inFormat1));
                localDateTime1 = localDate.atStartOfDay();
            }

            try {
                // LocalDateTime으로 파싱 시도
                localDateTime2 = LocalDateTime.parse(inDate2, DateTimeFormatter.ofPattern(inFormat2));
            } catch (DateTimeParseException e) {
                // LocalDate로 파싱 시도
                LocalDate localDate = LocalDate.parse(inDate2, DateTimeFormatter.ofPattern(inFormat2));
                localDateTime2 = localDate.atStartOfDay();
            }

            // 두 날짜 사이의 시간 차이 계산 (밀리초)
            return ChronoUnit.MILLIS.between(localDateTime1, localDateTime2);
        } catch (Exception e) {
            log.error("Failed to compute date difference: {} and {}", inDate1, inDate2, e);
            return 0;
        }
    }

    /**
     * 지정된 일수만큼 더한 날짜를 반환합니다.
     *
     * @param amount 더할 일수
     * @return 계산된 날짜 문자열 (yyyy-MM-dd 형식)
     */
    public static String getAddDate(int amount) {
        LocalDate localDate = LocalDate.now().plusDays(amount);
        return localDate.format(DateTimeFormatter.ofPattern(YYYY_MM_DD_BAR));
    }

    /**
     * 기준 날짜에 지정된 월수만큼 더한 날짜를 반환합니다.
     *
     * @param basicDate 기준 날짜 문자열 (yyyy-MM-dd 형식)
     * @param amount 더할 월수
     * @return 계산된 날짜 문자열 (yyyy-MM-dd 형식)
     */
    public static String getAddMonth(String basicDate, int amount) {
        if (!StringUtils.hasText(basicDate)) {
            return "";
        }

        try {
            // 날짜 형식 자동 감지 및 변환
            String cleanDateStr = basicDate.trim().replace("/", "-").replace(".", "-");

            // 하이픈이 없는 경우 (예: 20240315)
            if (!cleanDateStr.contains("-")) {
                if (cleanDateStr.length() == 8) {
                    cleanDateStr = cleanDateStr.substring(0, 4) + "-" + 
                                  cleanDateStr.substring(4, 6) + "-" + 
                                  cleanDateStr.substring(6, 8);
                }
            }

            LocalDate localDate = LocalDate.parse(cleanDateStr, DateTimeFormatter.ofPattern(YYYY_MM_DD_BAR));
            localDate = localDate.plusMonths(amount);

            return localDate.format(DateTimeFormatter.ofPattern(YYYY_MM_DD_BAR));
        } catch (DateTimeParseException e) {
            log.error("Failed to parse date string: {}", basicDate, e);
            return "";
        }
    }

    /**
     * 현재 날짜에 지정된 시간만큼 더한 날짜와 시간을 반환합니다.
     *
     * @param amount 더할 시간
     * @return 계산된 날짜와 시간 문자열 (yyyy-MM-dd HH:mm:ss 형식)
     */
    public static String getAddHour(int amount) {
        LocalDateTime localDateTime = LocalDateTime.now().plusHours(amount);
        return localDateTime.format(DateTimeFormatter.ofPattern(YYYY_MM_DD_HH_MM_SS_BAR));
    }

    /**
     * 현재 날짜에 지정된 월수만큼 더한 날짜를 반환합니다.
     *
     * @param amount 더할 월수
     * @return 계산된 날짜 문자열 (yyyy-MM-dd 형식)
     */
    public static String getAddMonth(int amount) {
        LocalDate localDate = LocalDate.now().plusMonths(amount);
        return localDate.format(DateTimeFormatter.ofPattern(YYYY_MM_DD_BAR));
    }

    /**
     * 지정된 연월의 마지막 날짜를 반환합니다.
     *
     * @param yearMonth 연월 문자열 (yyyy-MM 또는 yyyyMM 형식)
     * @return 해당 월의 마지막 날짜
     */
    public static int getLastDayOfMonth(String yearMonth) {
        if (!StringUtils.hasText(yearMonth)) {
            return 0;
        }

        try {
            // 연월 문자열 정제
            String cleanYearMonth = yearMonth.trim().replace("-", "");

            // 길이가 6이 아니면 유효하지 않은 연월 형식
            if (cleanYearMonth.length() != 6) {
                return 0;
            }

            int year = Integer.parseInt(cleanYearMonth.substring(0, 4));
            int month = Integer.parseInt(cleanYearMonth.substring(4, 6));

            LocalDate date = LocalDate.of(year, month, 1);
            return date.lengthOfMonth();
        } catch (Exception e) {
            log.error("Failed to get last day of month: {}", yearMonth, e);
            return 0;
        }
    }

    /**
     * 날짜 문자열을 지정된 형식으로 변환합니다.
     *
     * @param date 날짜 문자열
     * @param format 변환할 형식
     * @return 변환된 날짜 문자열
     */
    public static String getReqDate(String date, String format) {
        if (!StringUtils.hasText(date) || !StringUtils.hasText(format)) {
            return "";
        }

        try {
            // 날짜 형식 자동 감지 및 변환
            String cleanDateStr = date.trim().replace("/", "-").replace(".", "-");

            // 하이픈이 없는 경우 (예: 20240315)
            if (!cleanDateStr.contains("-")) {
                if (cleanDateStr.length() == 8) {
                    cleanDateStr = cleanDateStr.substring(0, 4) + "-" + 
                                  cleanDateStr.substring(4, 6) + "-" + 
                                  cleanDateStr.substring(6, 8);
                }
            }

            LocalDate localDate = LocalDate.parse(cleanDateStr, DateTimeFormatter.ofPattern(YYYY_MM_DD_BAR));
            return localDate.format(DateTimeFormatter.ofPattern(format));
        } catch (DateTimeParseException e) {
            log.error("Failed to parse date string: {}", date, e);
            return "";
        }
    }

    /**
     * 현재 날짜를 반환합니다.
     *
     * @return 현재 날짜 문자열 (yyyy-MM-dd 형식)
     */
    public static String getCurrentDate() {
        return LocalDate.now().format(DateTimeFormatter.ofPattern(YYYY_MM_DD_BAR));
    }

    /**
     * 현재 날짜를 지정된 형식으로 반환합니다.
     *
     * @param dateFormat 날짜 형식
     * @return 형식화된 현재 날짜 문자열
     */
    public static String getCurrentDate(String dateFormat) {
        if (!StringUtils.hasText(dateFormat)) {
            return getCurrentDate();
        }

        try {
            return LocalDate.now().format(DateTimeFormatter.ofPattern(dateFormat));
        } catch (Exception e) {
            log.error("Failed to format current date with format: {}", dateFormat, e);
            return "";
        }
    }

    /**
     * 입력 날짜를 다른 형식으로 변환합니다.
     *
     * @param inputDate 입력 날짜 문자열
     * @param inputFormat 입력 날짜 형식
     * @param outputFormat 출력 날짜 형식
     * @return 변환된 날짜 문자열
     */
    public static String getCurrentDate(String inputDate, String inputFormat, String outputFormat) {
        if (!StringUtils.hasText(inputDate) || !StringUtils.hasText(inputFormat) || !StringUtils.hasText(outputFormat)) {
            return "";
        }

        try {
            LocalDate localDate = LocalDate.parse(inputDate, DateTimeFormatter.ofPattern(inputFormat));
            return localDate.format(DateTimeFormatter.ofPattern(outputFormat));
        } catch (DateTimeParseException e) {
            log.error("Failed to parse date string: {} with format: {}", inputDate, inputFormat, e);
            return "";
        }
    }

    /**
     * Date 객체를 지정된 형식의 문자열로 변환합니다.
     *
     * @param date 변환할 날짜
     * @param dateFormat 날짜 형식
     * @return 형식화된 날짜 문자열
     */
    public static String getCurrentDate(Date date, String dateFormat) {
        if (date == null || !StringUtils.hasText(dateFormat)) {
            return "";
        }

        try {
            LocalDate localDate = toLocalDate(date);
            return localDate.format(DateTimeFormatter.ofPattern(dateFormat));
        } catch (Exception e) {
            log.error("Failed to format date: {} with format: {}", date, dateFormat, e);
            return "";
        }
    }

    /**
     * 날짜 문자열의 요일을 계산합니다.
     *
     * @param date 날짜 문자열 (yyyy-MM-dd 형식)
     * @return 요일 (1: 일요일, 2: 월요일, ..., 7: 토요일)
     */
    public static int dayOfWeek(String date) {
        if (!StringUtils.hasText(date)) {
            return 0;
        }

        try {
            // 날짜 형식 자동 감지 및 변환
            String cleanDateStr = date.trim().replace("/", "-").replace(".", "-");

            // 하이픈이 없는 경우 (예: 20240315)
            if (!cleanDateStr.contains("-")) {
                if (cleanDateStr.length() == 8) {
                    cleanDateStr = cleanDateStr.substring(0, 4) + "-" + 
                                  cleanDateStr.substring(4, 6) + "-" + 
                                  cleanDateStr.substring(6, 8);
                }
            }

            LocalDate localDate = LocalDate.parse(cleanDateStr, DateTimeFormatter.ofPattern(YYYY_MM_DD_BAR));
            // Java의 DayOfWeek는 1(월요일)부터 7(일요일)이므로 변환 필요
            return localDate.getDayOfWeek().getValue() % 7 + 1;
        } catch (DateTimeParseException e) {
            log.error("Failed to parse date string: {}", date, e);
            return 0;
        }
    }

    /**
     * 문자열을 기본 형식(yyyyMMddHHmmss)의 LocalDateTime으로 변환합니다.
     *
     * @param dateStr 변환할 날짜 문자열
     * @return 변환된 LocalDateTime 객체
     * @throws DateTimeParseException 날짜 형식이 잘못된 경우
     */
    public static LocalDateTime parseLocalDateTime(String dateStr) {
        Assert.hasText(dateStr, "Date string must not be empty");
        log.debug("Parsing date string: {}", dateStr);

        try {
            return LocalDateTime.parse(dateStr, DateTimeFormatter.ofPattern(YYYY_MM_DD_HH_MM_SS_STRIP));
        } catch (DateTimeParseException e) {
            log.error("Failed to parse date string: {}", dateStr, e);
            throw e;
        }
    }

    /**
     * 문자열을 지정된 형식의 LocalDateTime으로 변환합니다.
     *
     * @param dateStr 변환할 날짜 문자열
     * @param format 날짜 형식
     * @return 변환된 LocalDateTime 객체
     * @throws DateTimeParseException 날짜 형식이 잘못된 경우
     */
    public static LocalDateTime parseLocalDateTime(String dateStr, String format) {
        Assert.hasText(dateStr, "Date string must not be empty");
        Assert.hasText(format, "Format must not be empty");
        log.debug("Parsing date string: {} with format: {}", dateStr, format);

        try {
            return LocalDateTime.parse(dateStr, DateTimeFormatter.ofPattern(format));
        } catch (DateTimeParseException e) {
            log.error("Failed to parse date string: {} with format: {}", dateStr, format, e);
            throw e;
        }
    }

    /**
     * MonthVO 객체의 월 값을 검증하고 필요한 경우 조정합니다.
     * 이 메서드는 monthValid 메서드와 동일한 기능을 제공합니다.
     *
     * @param monthVO 검증할 MonthVO 객체
     * @return 검증 및 조정된 MonthVO 객체
     * @see #monthValid(MonthVO)
     */
    public static MonthVO validateMonthVO(MonthVO monthVO) {
        return monthValid(monthVO);
    }

    /**
     * 현재 날짜와 시간을 반환합니다.
     *
     * @return 현재 LocalDateTime 객체
     */
    public static LocalDateTime getCurrentLocalDateTime() {
        return LocalDateTime.now();
    }

    /**
     * 현재 날짜를 반환합니다.
     *
     * @return 현재 LocalDate 객체
     */
    public static LocalDate getCurrentLocalDate() {
        return LocalDate.now();
    }

    /**
     * LocalDate를 DateVO 객체로 변환합니다.
     *
     * @param date 변환할 LocalDate 객체
     * @return 변환된 DateVO 객체
     */
    public static DateVO localDateToVO(LocalDate date) {
        Assert.notNull(date, "Date must not be null");
        log.debug("Converting date to VO: {}", date);

        DateVO dateVO = new DateVO();
        dateVO.setYear(date.getYear());
        dateVO.setMonth(date.getMonthValue());
        dateVO.setDay(date.getDayOfMonth());
        dateVO.setWeek(String.valueOf(date.getDayOfWeek()));

        return dateVO;
    }

    /**
     * 두 LocalDate 사이의 일수 차이를 계산합니다.
     *
     * @param date1 첫 번째 날짜
     * @param date2 두 번째 날짜
     * @return 일수 차이
     */
    public static long localDateDiff(LocalDate date1, LocalDate date2) {
        Assert.notNull(date1, "First date must not be null");
        Assert.notNull(date2, "Second date must not be null");
        log.debug("Calculating date difference between {} and {}", date1, date2);

        return ChronoUnit.DAYS.between(date1, date2);
    }

    /**
     * LocalDate에 일수를 더합니다.
     *
     * @param date 기준 날짜
     * @param days 더할 일수
     * @return 계산된 날짜
     */
    public static LocalDate addDaysToLocalDate(LocalDate date, long days) {
        Assert.notNull(date, "Date must not be null");
        log.debug("Adding {} days to {}", days, date);

        return date.plusDays(days);
    }

    /**
     * LocalDate에 월수를 더합니다.
     *
     * @param date 기준 날짜
     * @param months 더할 월수
     * @return 계산된 날짜
     */
    public static LocalDate addMonthsToLocalDate(LocalDate date, long months) {
        Assert.notNull(date, "Date must not be null");
        log.debug("Adding {} months to {}", months, date);

        return date.plusMonths(months);
    }

    /**
     * 주의 첫 날짜(월요일)를 반환합니다.
     *
     * @param date 기준 날짜
     * @return 주의 첫 날짜
     */
    public static LocalDate getFirstDayOfLocalWeek(LocalDate date) {
        Assert.notNull(date, "Date must not be null");
        return date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
    }

    /**
     * 주의 마지막 날짜(일요일)를 반환합니다.
     *
     * @param date 기준 날짜
     * @return 주의 마지막 날짜
     */
    public static LocalDate getLastDayOfLocalWeek(LocalDate date) {
        Assert.notNull(date, "Date must not be null");
        return date.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
    }

    /**
     * 월의 마지막 날짜를 반환합니다.
     *
     * @param year 연도
     * @param month 월
     * @return 월의 마지막 날짜
     */
    public static int getLocalLastDayOfMonth(int year, int month) {
        Assert.isTrue(month >= 1 && month <= 12, "Month must be between 1 and 12");

        LocalDate date = LocalDate.of(year, month, 1);
        return date.with(TemporalAdjusters.lastDayOfMonth()).getDayOfMonth();
    }

    /**
     * 날짜를 지정된 형식의 문자열로 변환합니다.
     *
     * @param date 변환할 날짜
     * @param format 날짜 형식
     * @return 형식화된 날짜 문자열
     */
    public static String formatLocalDate(LocalDate date, String format) {
        Assert.notNull(date, "Date must not be null");
        Assert.hasText(format, "Format must not be empty");

        return date.format(DateTimeFormatter.ofPattern(format));
    }

    /**
     * 날짜와 시간을 지정된 형식의 문자열로 변환합니다.
     *
     * @param dateTime 변환할 날짜와 시간
     * @param format 날짜 형식
     * @return 형식화된 날짜와 시간 문자열
     */
    public static String formatLocalDateTime(LocalDateTime dateTime, String format) {
        Assert.notNull(dateTime, "DateTime must not be null");
        Assert.hasText(format, "Format must not be empty");

        return dateTime.format(DateTimeFormatter.ofPattern(format));
    }
}
