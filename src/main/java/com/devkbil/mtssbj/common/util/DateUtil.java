package com.devkbil.mtssbj.common.util;

import com.devkbil.mtssbj.schedule.DateVO;
import com.devkbil.mtssbj.schedule.MonthVO;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.Date;
import java.util.Locale;

/**
 * Java 8+ 시간 API를 사용하는 현대적인 날짜 유틸리티 클래스
 * 
 * 이 클래스는 다음과 같은 기능을 제공합니다:
 * - 기본 날짜/시간 조작
 * - 날짜 문자열 파싱 및 포맷팅
 * - 날짜 계산 (날짜 간의 차이, 날짜 더하기/빼기)
 * - 주간 작업 (주의 시작일/종료일)
 * - 월간 작업
 * - VO 변환
 * - 레거시 Date 클래스 지원
 */
@Slf4j
public class DateUtil {
    private static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
    private static final String DEFAULT_DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final Locale DEFAULT_LOCALE = Locale.KOREAN;

    // Thread-safe cached formatters and time zone
    private static final DateTimeFormatter DEFAULT_DATE_FORMATTER = DateTimeFormatter.ofPattern(DEFAULT_DATE_FORMAT);
    private static final DateTimeFormatter DEFAULT_DATETIME_FORMATTER = DateTimeFormatter.ofPattern(DEFAULT_DATETIME_FORMAT);
    private static final ZoneId SYSTEM_ZONE_ID = ZoneId.systemDefault();

    private DateUtil() {
        throw new IllegalStateException("Utility class");
    }

    // 기본 날짜/시간 작업
    /**
     * 현재 날짜를 반환합니다.
     *
     * @return 현재 날짜 (LocalDate)
     */
    public static LocalDate getCurrentDate() {
        return LocalDate.now();
    }

    /**
     * 현재 날짜와 시간을 반환합니다.
     *
     * @return 현재 날짜와 시간 (LocalDateTime)
     */
    public static LocalDateTime getCurrentDateTime() {
        return LocalDateTime.now();
    }

    /**
     * 현재 시간을 반환합니다.
     *
     * @return 현재 시간 (LocalTime)
     */
    public static LocalTime getCurrentTime() {
        return LocalTime.now();
    }

    // 날짜 문자열 파싱 메소드
    /**
     * 문자열을 날짜로 변환합니다. 기본 형식(yyyy-MM-dd)을 사용합니다.
     *
     * @param dateStr 변환할 날짜 문자열
     * @return 변환된 LocalDate 객체, 파싱 실패시 null
     */
    public static LocalDate parseDate(String dateStr) {
        if (!StringUtils.hasText(dateStr)) {
            return null;
        }
        try {
            return LocalDate.parse(dateStr, DEFAULT_DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            log.error("날짜 파싱 실패: {}", dateStr, e);
            return null;
        }
    }

    /**
     * 지정된 형식의 문자열을 날짜로 변환합니다.
     *
     * @param dateStr 변환할 날짜 문자열
     * @param format 날짜 형식 (예: "yyyy-MM-dd")
     * @return 변환된 LocalDate 객체, 파싱 실패시 null
     */
    public static LocalDate parseDate(String dateStr, String format) {
        if (!StringUtils.hasText(dateStr) || !StringUtils.hasText(format)) {
            return null;
        }
        try {
            return LocalDate.parse(dateStr, DateTimeFormatter.ofPattern(format));
        } catch (DateTimeParseException e) {
            log.error("날짜 파싱 실패: {} (형식: {})", dateStr, format, e);
            return null;
        }
    }

    /**
     * 문자열을 날짜시간으로 변환합니다. 기본 형식(yyyy-MM-dd HH:mm:ss)을 사용합니다.
     *
     * @param dateTimeStr 변환할 날짜시간 문자열
     * @return 변환된 LocalDateTime 객체, 파싱 실패시 null
     */
    public static LocalDateTime parseDateTime(String dateTimeStr) {
        if (!StringUtils.hasText(dateTimeStr)) {
            return null;
        }
        try {
            return LocalDateTime.parse(dateTimeStr, DEFAULT_DATETIME_FORMATTER);
        } catch (DateTimeParseException e) {
            log.error("날짜시간 파싱 실패: {}", dateTimeStr, e);
            return null;
        }
    }

    /**
     * 지정된 형식의 문자열을 날짜시간으로 변환합니다.
     *
     * @param dateTimeStr 변환할 날짜시간 문자열
     * @param format 날짜시간 형식 (예: "yyyy-MM-dd HH:mm:ss")
     * @return 변환된 LocalDateTime 객체, 파싱 실패시 null
     */
    public static LocalDateTime parseDateTime(String dateTimeStr, String format) {
        if (!StringUtils.hasText(dateTimeStr) || !StringUtils.hasText(format)) {
            return null;
        }
        try {
            return LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ofPattern(format));
        } catch (DateTimeParseException e) {
            log.error("날짜시간 파싱 실패: {} (형식: {})", dateTimeStr, format, e);
            return null;
        }
    }

    // 날짜 형식화 메소드
    /**
     * 날짜를 문자열로 변환합니다. 기본 형식(yyyy-MM-dd)을 사용합니다.
     *
     * @param date 변환할 날짜
     * @return 형식화된 날짜 문자열, 입력이 null인 경우 null 반환
     */
    public static String formatDate(LocalDate date) {
        if (date == null) return null;
        return date.format(DEFAULT_DATE_FORMATTER);
    }

    /**
     * 날짜를 지정된 형식의 문자열로 변환합니다.
     *
     * @param date 변환할 날짜
     * @param format 날짜 형식 (예: "yyyy-MM-dd")
     * @return 형식화된 날짜 문자열, 입력이 null인 경우 또는 형식이 유효하지 않은 경우 null 반환
     */
    public static String formatDate(LocalDate date, String format) {
        if (date == null || !StringUtils.hasText(format)) return null;
        try {
            return date.format(DateTimeFormatter.ofPattern(format));
        } catch (IllegalArgumentException e) {
            log.error("날짜 형식화 실패: {} (형식: {})", date, format, e);
            return null;
        }
    }

    /**
     * 날짜시간을 문자열로 변환합니다. 기본 형식(yyyy-MM-dd HH:mm:ss)을 사용합니다.
     *
     * @param dateTime 변환할 날짜시간
     * @return 형식화된 날짜시간 문자열, 입력이 null인 경우 null 반환
     */
    public static String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) return null;
        return dateTime.format(DEFAULT_DATETIME_FORMATTER);
    }

    /**
     * 날짜시간을 지정된 형식의 문자열로 변환합니다.
     *
     * @param dateTime 변환할 날짜시간
     * @param format 날짜시간 형식 (예: "yyyy-MM-dd HH:mm:ss")
     * @return 형식화된 날짜시간 문자열, 입력이 null인 경우 또는 형식이 유효하지 않은 경우 null 반환
     */
    public static String formatDateTime(LocalDateTime dateTime, String format) {
        if (dateTime == null || !StringUtils.hasText(format)) return null;
        try {
            return dateTime.format(DateTimeFormatter.ofPattern(format));
        } catch (IllegalArgumentException e) {
            log.error("날짜시간 형식화 실패: {} (형식: {})", dateTime, format, e);
            return null;
        }
    }

    // 날짜 계산 메소드
    /**
     * 두 날짜 사이의 일수를 계산합니다.
     *
     * @param date1 시작 날짜
     * @param date2 종료 날짜
     * @return 두 날짜 사이의 일수 (date2 - date1)
     */
    public static long daysBetween(LocalDate date1, LocalDate date2) {
        return ChronoUnit.DAYS.between(date1, date2);
    }

    /**
     * 주어진 날짜에 지정된 일수를 더합니다.
     *
     * @param date 기준 날짜
     * @param days 더할 일수 (음수 가능)
     * @return 계산된 새로운 날짜
     */
    public static LocalDate addDays(LocalDate date, int days) {
        return date.plusDays(days);
    }

    /**
     * 주어진 날짜에 지정된 월수를 더합니다.
     *
     * @param date 기준 날짜
     * @param months 더할 월수 (음수 가능)
     * @return 계산된 새로운 날짜
     */
    public static LocalDate addMonths(LocalDate date, int months) {
        return date.plusMonths(months);
    }

    // 주간 작업 메소드
    /**
     * 주어진 날짜가 속한 주의 첫 날(월요일)을 반환합니다.
     *
     * @param date 기준 날짜
     * @return 해당 주의 월요일 날짜
     */
    public static LocalDate getFirstDayOfWeek(LocalDate date) {
        return date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
    }

    /**
     * 주어진 날짜가 속한 주의 마지막 날(일요일)을 반환합니다.
     *
     * @param date 기준 날짜
     * @return 해당 주의 일요일 날짜
     */
    public static LocalDate getLastDayOfWeek(LocalDate date) {
        return date.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
    }

    /**
     * 주어진 날짜의 요일 이름을 한글로 반환합니다.
     *
     * @param date 기준 날짜
     * @return 요일 이름 (예: "월요일", "화요일" 등)
     */
    public static String getWeekDayName(LocalDate date) {
        return date.getDayOfWeek().getDisplayName(TextStyle.FULL, DEFAULT_LOCALE);
    }

    // 월간 작업 메소드
    /**
     * 지정된 연도와 월의 마지막 일자를 반환합니다.
     *
     * @param year 연도
     * @param month 월 (1-12)
     * @return 해당 월의 마지막 일자 (28-31)
     */
    public static int getEndDayOfMonth(int year, int month) {
        return YearMonth.of(year, month).lengthOfMonth();
    }

    // VO 변환 메소드
    /**
     * LocalDate를 DateVO 객체로 변환합니다.
     *
     * @param date 변환할 LocalDate 객체
     * @return 변환된 DateVO 객체, 입력이 null인 경우 null 반환
     */
    public static DateVO toDateVO(LocalDate date) {
        if (date == null) return null;
        DateVO vo = new DateVO();
        vo.setYear(date.getYear());
        vo.setMonth(date.getMonthValue());
        vo.setDay(date.getDayOfMonth());
        vo.setDate(formatDate(date));
        vo.setWeek(String.valueOf(date.get(WeekFields.of(DEFAULT_LOCALE).weekOfWeekBasedYear())));
        vo.setIstoday(date.equals(LocalDate.now()));
        return vo;
    }

    /**
     * LocalDate를 MonthVO 객체로 변환합니다.
     *
     * @param date 변환할 LocalDate 객체
     * @return 변환된 MonthVO 객체, 입력이 null인 경우 null 반환
     */
    public static MonthVO toMonthVO(LocalDate date) {
        if (date == null) return null;
        MonthVO vo = new MonthVO();
        vo.setYear(String.valueOf(date.getYear()));
        vo.setMonth(String.format("%02d", date.getMonthValue()));
        return vo;
    }

    /**
     * MonthVO 객체를 LocalDate로 변환합니다.
     * 유효하지 않은 월은 자동으로 조정됩니다 (예: 13월 → 다음해 1월).
     *
     * @param monthVO 변환할 MonthVO 객체
     * @return 변환된 LocalDate 객체 (해당 월의 1일), 변환 실패시 null 반환
     */
    public static LocalDate toLocalDate(MonthVO monthVO) {
        if (monthVO == null || !StringUtils.hasText(monthVO.getYear()) || !StringUtils.hasText(monthVO.getMonth())) {
            return null;
        }
        try {
            int year = Integer.parseInt(monthVO.getYear());
            int month = Integer.parseInt(monthVO.getMonth());

            // 유효하지 않은 월을 조정
            if (month < 1) {
                year--;
                month = 12;
            } else if (month > 12) {
                year++;
                month = 1;
            }

            return LocalDate.of(year, month, 1);
        } catch (NumberFormatException | DateTimeException e) {
            log.error("MonthVO를 LocalDate로 변환 실패: {}", e.getMessage());
            return null;
        }
    }

    /**
     * MonthVO 객체의 월 값을 검증하고 필요한 경우 조정합니다.
     * 유효하지 않은 월은 자동으로 조정됩니다 (예: 13월 → 다음해 1월).
     *
     * @param monthVO 검증할 MonthVO 객체
     * @return 조정된 MonthVO 객체, 변환 실패시 null 반환
     */
    public static MonthVO monthValid(MonthVO monthVO) {
        if (monthVO == null) return null;

        try {
            int month = Integer.parseInt(monthVO.getMonth());
            int year = Integer.parseInt(monthVO.getYear());

            // 유효하지 않은 월을 조정
            if (month < 1) {
                year--;
                month = 12;
            } else if (month > 12) {
                year++;
                month = 1;
            }

            MonthVO result = new MonthVO();
            result.setYear(String.valueOf(year));
            result.setMonth(String.format("%02d", month));
            return result;
        } catch (NumberFormatException e) {
            log.error("MonthVO 숫자 형식 오류: year={}, month={}", 
                monthVO.getYear(), monthVO.getMonth());
            return null;
        }
    }

    // 레거시 지원 메소드 (java.util.Date 호환)
    /**
     * 현재 날짜를 "yyyy-MM-dd" 형식의 문자열로 반환합니다.
     *
     * @return 형식화된 현재 날짜 문자열
     */
    public static String getYyyyMMdd_bar() {
        return formatDate(LocalDate.now());
    }

    /**
     * 현재 날짜를 java.util.Date 객체로 반환합니다.
     *
     * @return 현재 날짜의 Date 객체
     */
    public static Date getToday() {
        return Date.from(LocalDate.now().atStartOfDay(SYSTEM_ZONE_ID).toInstant());
    }

    /**
     * 문자열을 java.util.Date로 변환합니다. 기본 형식(yyyy-MM-dd)을 사용합니다.
     *
     * @param sDate 변환할 날짜 문자열
     * @return 변환된 Date 객체, 변환 실패시 null
     */
    public static Date convDate(String sDate) {
        return convDate(sDate, DEFAULT_DATE_FORMAT);
    }

    /**
     * 지정된 형식의 문자열을 java.util.Date로 변환합니다.
     *
     * @param sDate 변환할 날짜 문자열
     * @param sFormat 날짜 형식
     * @return 변환된 Date 객체, 변환 실패시 null
     */
    public static Date convDate(String sDate, String sFormat) {
        try {
            LocalDate date = parseDate(sDate, sFormat);
            return date != null ? Date.from(date.atStartOfDay(SYSTEM_ZONE_ID).toInstant()) : null;
        } catch (Exception e) {
            log.error("날짜 변환 실패: {} (형식: {})", sDate, sFormat, e);
            return null;
        }
    }

    /**
     * Date 객체에서 연도를 추출합니다.
     *
     * @param date Date 객체
     * @return 연도 값, null 입력시 0 반환
     */
    public static int getYear(Date date) {
        if (date == null) return 0;
        return Instant.ofEpochMilli(date.getTime())
            .atZone(SYSTEM_ZONE_ID)
            .getYear();
    }

    /**
     * Date 객체에서 월을 추출합니다.
     *
     * @param date Date 객체
     * @return 월 값 (1-12), null 입력시 0 반환
     */
    public static int getMonth(Date date) {
        if (date == null) return 0;
        return Instant.ofEpochMilli(date.getTime())
            .atZone(SYSTEM_ZONE_ID)
            .getMonthValue();
    }

    /**
     * Date 객체를 문자열로 변환합니다. 기본 형식(yyyy-MM-dd)을 사용합니다.
     *
     * @param date 변환할 Date 객체
     * @return 형식화된 날짜 문자열, 입력이 null인 경우 null 반환
     */
    public static String date2Str(Date date) {
        if (date == null) return null;
        return formatDate(date.toInstant()
            .atZone(SYSTEM_ZONE_ID)
            .toLocalDate());
    }

    /**
     * 문자열을 Date 객체로 변환합니다. 기본 형식(yyyy-MM-dd)을 사용합니다.
     * 
     * @deprecated Use {@link #stringToDate(String)} instead
     * @param dateStr 변환할 날짜 문자열
     * @return 변환된 Date 객체, 변환 실패시 null 반환
     */
    @Deprecated
    public static Date str2Date(String dateStr) {
        return stringToDate(dateStr);
    }

    /**
     * 문자열을 Date 객체로 변환합니다. 기본 형식(yyyy-MM-dd)을 사용합니다.
     *
     * @param dateStr 변환할 날짜 문자열
     * @return 변환된 Date 객체, 변환 실패시 null 반환
     */
    public static Date stringToDate(String dateStr) {
        if (!StringUtils.hasText(dateStr)) return null;
        try {
            LocalDate date = parseDate(dateStr);
            return date != null ? Date.from(date.atStartOfDay(SYSTEM_ZONE_ID).toInstant()) : null;
        } catch (Exception e) {
            log.error("날짜 문자열 파싱 실패: {}", dateStr, e);
            return null;
        }
    }

    /**
     * Date 객체에서 해당 월의 주차를 반환합니다.
     *
     * @param date Date 객체
     * @return 월의 주차 (1-5), null 입력시 0 반환
     */
    public static int getWeekOfMonth(Date date) {
        if (date == null) return 0;
        return date.toInstant()
            .atZone(SYSTEM_ZONE_ID)
            .toLocalDate()
            .get(WeekFields.of(DEFAULT_LOCALE).weekOfMonth());
    }

    /**
     * Date 객체가 속한 주의 첫 날(월요일)을 반환합니다.
     *
     * @param date Date 객체
     * @return 해당 주의 월요일 날짜, null 입력시 null 반환
     */
    public static Date getFirstOfWeek(Date date) {
        if (date == null) return null;
        LocalDate localDate = date.toInstant()
            .atZone(SYSTEM_ZONE_ID)
            .toLocalDate();
        return Date.from(getFirstDayOfWeek(localDate)
            .atStartOfDay(SYSTEM_ZONE_ID)
            .toInstant());
    }

    /**
     * Date 객체가 속한 주의 마지막 날(일요일)을 반환합니다.
     *
     * @param date Date 객체
     * @return 해당 주의 일요일 날짜, null 입력시 null 반환
     */
    public static Date getLastOfWeek(Date date) {
        if (date == null) return null;
        LocalDate localDate = date.toInstant()
            .atZone(SYSTEM_ZONE_ID)
            .toLocalDate();
        return Date.from(getLastDayOfWeek(localDate)
            .atStartOfDay(SYSTEM_ZONE_ID)
            .toInstant());
    }

    /**
     * Date 객체에 지정된 일수를 더합니다.
     *
     * @param date Date 객체
     * @param days 더할 일수 (음수 가능)
     * @return 계산된 새로운 Date 객체, null 입력시 null 반환
     */
    public static Date dateAdd(Date date, int days) {
        if (date == null) return null;
        LocalDate localDate = date.toInstant()
            .atZone(SYSTEM_ZONE_ID)
            .toLocalDate();
        return Date.from(addDays(localDate, days)
            .atStartOfDay(SYSTEM_ZONE_ID)
            .toInstant());
    }

    /**
     * Date 객체의 요일을 숫자로 반환합니다.
     *
     * @param date Date 객체
     * @return 요일 값 (1: 월요일 ~ 7: 일요일), null 입력시 0 반환
     */
    public static int getDayOfWeek(Date date) {
        if (date == null) return 0;
        return date.toInstant()
            .atZone(SYSTEM_ZONE_ID)
            .toLocalDate()
            .getDayOfWeek()
            .getValue();
    }

    /**
     * Date 객체를 DateVO로 변환합니다.
     *
     * @param date Date 객체
     * @return 변환된 DateVO 객체, null 입력시 null 반환
     */
    public static DateVO date2VO(Date date) {
        if (date == null) return null;
        return toDateVO(date.toInstant()
            .atZone(SYSTEM_ZONE_ID)
            .toLocalDate());
    }

    /**
     * 두 Date 객체 사이의 일수를 계산합니다.
     *
     * @param date1 시작 날짜
     * @param date2 종료 날짜
     * @return 두 날짜 사이의 일수 (date2 - date1), null 입력시 0 반환
     */
    public static long dateDiff(Date date1, Date date2) {
        if (date1 == null || date2 == null) return 0;
        LocalDate localDate1 = date1.toInstant()
            .atZone(SYSTEM_ZONE_ID)
            .toLocalDate();
        LocalDate localDate2 = date2.toInstant()
            .atZone(SYSTEM_ZONE_ID)
            .toLocalDate();
        return daysBetween(localDate1, localDate2);
    }

    /**
     * 문자열을 Date 객체로 안전하게 변환합니다. 기본 형식(yyyy-MM-dd)을 사용합니다.
     * 변환 실패시 예외를 발생시키지 않고 null을 반환합니다.
     *
     * @param dateStr 변환할 날짜 문자열
     * @return 변환된 Date 객체, 변환 실패시 null 반환
     */
    public static Date safeStr2Date(String dateStr) {
        if (!StringUtils.hasText(dateStr)) return null;
        try {
            return stringToDate(dateStr);
        } catch (Exception e) {
            log.error("날짜 문자열 안전 파싱 실패: {}", dateStr, e);
            return null;
        }
    }

    /**
     * Date 객체에 지정된 월수를 더합니다.
     *
     * @param date Date 객체
     * @param months 더할 월수 (음수 가능)
     * @return 계산된 새로운 Date 객체, null 입력시 null 반환
     */
    public static Date dateAddMonth(Date date, int months) {
        if (date == null) return null;
        LocalDate localDate = date.toInstant()
            .atZone(SYSTEM_ZONE_ID)
            .toLocalDate();
        return Date.from(addMonths(localDate, months)
            .atStartOfDay(SYSTEM_ZONE_ID)
            .toInstant());
    }
}
