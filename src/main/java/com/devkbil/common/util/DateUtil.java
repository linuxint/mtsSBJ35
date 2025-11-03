package com.devkbil.common.util;

import com.devkbil.mtssbj.schedule.DateVO;
import com.devkbil.mtssbj.schedule.MonthVO;
import org.springframework.util.StringUtils;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.Date;
import java.util.Locale;

public class DateUtil {

    private static final ZoneId DEFAULT_ZONE_ID = ZoneId.systemDefault();
    private static final ThreadLocal<Instant> startTime = new ThreadLocal<>();
    private static final ThreadLocal<Instant> endTime = new ThreadLocal<>();

    private static final String YYYY_MM_DD = "yyyy-MM-dd";
    private static final String YYYYMMDD = "yyyyMMdd";
    private static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    private static final String YYYYMMDDHHMMSS = "yyyyMMddHHmmss";
    private static final String HHMMSS = "HHmmss";

    // =================================================================================
    // Legacy DateUtil compatibility aliases (for migration)
    // =================================================================================

    /**
     * 표준 날짜 포맷 문자열(yyyy-MM-dd)을 반환합니다.
     * DateUtil.getYyyyMMdd_bar() 대체
     */
    public static String getYyyyMMdd_bar() {
        return YYYY_MM_DD;
    }

    /**
     * 오늘 날짜를 Date로 반환합니다.
     * DateUtil.getToday() 대체
     */
    public static Date getToday() {
        return getTodayAsDate();
    }

    /**
     * Date를 yyyy-MM-dd 문자열로 변환합니다.
     * DateUtil.date2Str(Date) 대체
     */
    public static String date2Str(Date date) {
        return dateToString(date);
    }

    /**
     * 문자열을 Date로 변환합니다. 다양한 구분자와 포맷을 허용합니다.
     * DateUtil.str2Date(String) 대체
     */
    public static Date str2Date(String dateStr) {
        return stringToDate(dateStr);
    }

    /**
     * Date를 DateVO로 변환합니다.
     * DateUtil.date2VO(Date) 대체
     */
    public static DateVO date2VO(Date date) {
        return toVO(date);
    }

    /**
     * 두 날짜의 일수 차이를 반환합니다. (date1 - date2)
     * DateUtil.dateDiff(Date, Date) 대체
     */
    public static Integer dateDiff(Date date1, Date date2) {
        if (date1 == null || date2 == null) return null;
        return (int) daysBetween(date1, date2);
    }

    /**
     * 기준 날짜에 일수를 더합니다. (음수 가능)
     * DateUtil.dateAdd(Date, Integer) 대체
     */
    public static Date dateAdd(Date date, Integer days) {
        if (date == null || days == null) return date;
        return addDays(date, days);
    }

    /**
     * 기준 날짜에 월을 더합니다. (음수 가능)
     * DateUtil.monthAdd(Date, Integer) 대체
     */
    public static Date monthAdd(Date date, Integer months) {
        if (date == null || months == null) return date;
        return addMonths(date, months);
    }

    /**
     * Date에서 연도를 추출합니다.
     * DateUtil.getYear(Date) 대체
     */
    public static Integer getYear(Date date) {
        if (date == null) return null;
        return toLocalDate(date).getYear();
    }

    /**
     * Date에서 월(1-12)을 추출합니다.
     * DateUtil.getMonth(Date) 대체
     */
    public static Integer getMonth(Date date) {
        if (date == null) return null;
        return toLocalDate(date).getMonthValue();
    }

    /**
     * Date에서 요일을 추출합니다. (1:일, 2:월, ..., 7:토)
     * DateUtil.getDayOfWeek(Date) 대체
     */
    public static Integer getDayOfWeek(Date date) {
        if (date == null) return null;
        int val = toLocalDate(date).getDayOfWeek().getValue(); // 1=Mon..7=Sun
        return val % 7 + 1; // 1=Sun..7=Sat
    }

    /**
     * 해당 월의 주차(1부터 시작)를 반환합니다.
     * DateUtil.getWeekOfMonth(Date) 대체
     */
    public static Integer getWeekOfMonth(Date date) {
        if (date == null) return null;
        LocalDate localDate = toLocalDate(date);
        LocalDate firstDayOfMonth = localDate.withDayOfMonth(1);
        int firstDay = firstDayOfMonth.getDayOfWeek().getValue(); // 1=Mon..7=Sun
        firstDay = firstDay % 7 + 1; // 1=Sun..7=Sat
        int dayOfMonth = localDate.getDayOfMonth();
        return (dayOfMonth + firstDay - 2) / 7 + 1;
    }

    /**
     * 주의 첫 날(일요일)을 반환합니다.
     * DateUtil.getFirstOfWeek(Date) 대체
     */
    public static Date getFirstOfWeek(Date date) {
        return getFirstDayOfWeek(date);
    }

    /**
     * 주의 마지막 날(토요일)을 반환합니다.
     * DateUtil.getLastOfWeek(Date) 대체
     */
    public static Date getLastOfWeek(Date date) {
        return getLastDayOfWeek(date);
    }

    /**
     * MonthVO의 년/월 값을 검증합니다.
     * DateUtil.monthValid(MonthVO) 대체
     */
    public static MonthVO monthValid(MonthVO monthVO) {
        return validate(monthVO);
    }

    // =================================================================================
    // 값 참조 (현재 시간/날짜 가져오기)
    // =================================================================================

    /**
     * 현재 날짜를 Date 객체로 반환합니다.
     */
    public static Date getTodayAsDate() {
        return Date.from(LocalDate.now(DEFAULT_ZONE_ID).atStartOfDay(DEFAULT_ZONE_ID).toInstant());
    }

    /**
     * 현재 날짜와 시간을 LocalDateTime 객체로 반환합니다.
     */
    public static LocalDateTime getCurrentLocalDateTime() {
        return LocalDateTime.now(DEFAULT_ZONE_ID);
    }

    /**
     * 현재 날짜를 LocalDate 객체로 반환합니다.
     */
    public static LocalDate getCurrentLocalDate() {
        return LocalDate.now(DEFAULT_ZONE_ID);
    }

    /**
     * 현재 시간(Hour)을 int로 반환합니다.
     */
    public static int getCurrentHour() {
        return LocalDateTime.now(DEFAULT_ZONE_ID).getHour();
    }

    /**
     * 현재 분(Minute)을 int로 반환합니다.
     */
    public static int getCurrentMinute() {
        return LocalDateTime.now(DEFAULT_ZONE_ID).getMinute();
    }

    /**
     * 현재 년도를 int로 반환합니다.
     */
    public static int getCurrentYear() {
        return LocalDate.now(DEFAULT_ZONE_ID).getYear();
    }

    /**
     * 현재 월을 int로 반환합니다.
     */
    public static int getCurrentMonth() {
        return LocalDate.now(DEFAULT_ZONE_ID).getMonthValue();
    }

    /**
     * 현재 일을 int로 반환합니다.
     */
    public static int getCurrentDayOfMonth() {
        return LocalDate.now(DEFAULT_ZONE_ID).getDayOfMonth();
    }

    /**
     * 현재 날짜/시간을 "yyyy-MM-dd HH:mm:ss" 형식의 문자열로 반환합니다.
     */
    public static String getCurrentDateTimeAsString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(YYYY_MM_DD_HH_MM_SS);
        return LocalDateTime.now(DEFAULT_ZONE_ID).format(formatter);
    }

    /**
     * 현재 날짜/시간을 지정된 형식의 문자열로 반환합니다.
     */
    public static String getCurrentDateTimeAsString(String format) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return LocalDateTime.now(DEFAULT_ZONE_ID).format(formatter);
    }

    /**
     * 현재 날짜를 "yyyyMMdd" 형식의 int로 반환합니다.
     */
    public static int getCurrentDateAsInt() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(YYYYMMDD);
        return Integer.parseInt(LocalDate.now(DEFAULT_ZONE_ID).format(formatter));
    }

    /**
     * 현재 시간을 "HHmmss" 형식의 int로 반환합니다.
     */
    public static int getCurrentTimeAsInt() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(HHMMSS);
        return Integer.parseInt(LocalDateTime.now(DEFAULT_ZONE_ID).format(formatter));
    }

    /**
     * 현재 날짜/시간을 "yyyyMMddHHmmss" 형식의 문자열로 반환합니다.
     */
    public static String getCurrentDateTimeAsCompactString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(YYYYMMDDHHMMSS);
        return LocalDateTime.now(DEFAULT_ZONE_ID).format(formatter);
    }

    /**
     * 현재 GMT 날짜를 Date 객체로 반환합니다.
     */
    public static Date getGmtDate() {
        return Date.from(ZonedDateTime.now(ZoneId.of("GMT")).toInstant());
    }

    /**
     * 현재 GMT 시간을 int로 반환합니다.
     */
    public static int getGmtHour() {
        return ZonedDateTime.now(ZoneId.of("GMT")).getHour();
    }

    /**
     * 시간 측정 시작 시간을 기록합니다.
     */
    public static void startTimer() {
        startTime.set(Instant.now());
    }

    /**
     * 시간 측정 종료 시간을 기록합니다.
     */
    public static void stopTimer() {
        endTime.set(Instant.now());
    }

    // =================================================================================
    // 값 연산 (날짜/시간 계산)
    // =================================================================================

    /**
     * Date 객체에 일을 더합니다.
     */
    public static Date addDays(Date date, int days) {
        if (date == null) return null;
        return toDate(toLocalDateTime(date).plusDays(days));
    }

    /**
     * Date 객체에 월을 더합니다.
     */
    public static Date addMonths(Date date, int months) {
        if (date == null) return null;
        return toDate(toLocalDateTime(date).plusMonths(months));
    }

    /**
     * 날짜 문자열에 일을 더합니다.
     */
    public static String addDays(String dateStr, int days) {
        if (!StringUtils.hasText(dateStr)) return "";
        try {
            LocalDate date = LocalDate.parse(dateStr);
            return date.plusDays(days).toString();
        } catch (DateTimeParseException e) {
            return dateStr;
        }
    }

    /**
     * LocalDate 객체에 월을 더합니다.
     */
    public static LocalDate addMonths(LocalDate date, long months) {
        if (date == null) return null;
        return date.plusMonths(months);
    }

    /**
     * LocalDate 객체에 일을 더합니다.
     */
    public static LocalDate addDays(LocalDate date, long days) {
        if (date == null) return null;
        return date.plusDays(days);
    }

    /**
     * 날짜 문자열에 년, 월, 일, 시, 분, 초를 더합니다.
     */
    public static String addDateTime(String dateStr, String inFormat, String outFormat, int years, int months, int days, int hours, int minutes, int seconds) {
        if (!StringUtils.hasText(dateStr)) return "";
        try {
            DateTimeFormatter inFormatter = DateTimeFormatter.ofPattern(inFormat);
            LocalDateTime dateTime = LocalDateTime.parse(dateStr, inFormatter);
            dateTime = dateTime.plusYears(years).plusMonths(months).plusDays(days).plusHours(hours).plusMinutes(minutes).plusSeconds(seconds);
            DateTimeFormatter outFormatter = DateTimeFormatter.ofPattern(outFormat);
            return dateTime.format(outFormatter);
        } catch (DateTimeParseException e) {
            return dateStr;
        }
    }

    /**
     * 현재 날짜에 일을 더합니다.
     */
    public static String addDaysToCurrent(int days) {
        return LocalDate.now(DEFAULT_ZONE_ID).plusDays(days).toString();
    }

    /**
     * 날짜 문자열에 월을 더합니다.
     */
    public static String addMonths(String dateStr, int months) {
        if (!StringUtils.hasText(dateStr)) return "";
        try {
            LocalDate date = LocalDate.parse(dateStr);
            return date.plusMonths(months).toString();
        } catch (DateTimeParseException e) {
            return dateStr;
        }
    }

    /**
     * 현재 날짜/시간에 시간을 더합니다.
     */
    public static String addHoursToCurrent(int hours) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(YYYY_MM_DD_HH_MM_SS);
        return LocalDateTime.now(DEFAULT_ZONE_ID).plusHours(hours).format(formatter);
    }

    /**
     * 현재 날짜에 월을 더합니다.
     */
    public static String addMonthsToCurrent(int months) {
        return LocalDate.now(DEFAULT_ZONE_ID).plusMonths(months).toString();
    }


    // =================================================================================
    // 값 비교 (날짜/시간 차이 계산)
    // =================================================================================

    /**
     * 두 Date 객체 사이의 일수 차이를 계산합니다.
     */
    public static long daysBetween(Date date1, Date date2) {
        if (date1 == null || date2 == null) return 0;
        return ChronoUnit.DAYS.between(toLocalDate(date1), toLocalDate(date2));
    }

    /**
     * 두 LocalDate 객체 사이의 일수 차이를 계산합니다.
     */
    public static long daysBetween(LocalDate date1, LocalDate date2) {
        if (date1 == null || date2 == null) return 0;
        return ChronoUnit.DAYS.between(date1, date2);
    }

    /**
     * 두 날짜 문자열 사이의 시간 차이를 밀리초로 계산합니다.
     */
    public static long millisBetween(String dateStr1, String format1, String dateStr2, String format2) {
        if (!StringUtils.hasText(dateStr1) || !StringUtils.hasText(dateStr2)) return 0;
        try {
            DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern(format1);
            LocalDateTime dateTime1 = LocalDateTime.parse(dateStr1, formatter1);
            DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern(format2);
            LocalDateTime dateTime2 = LocalDateTime.parse(dateStr2, formatter2);
            return Duration.between(dateTime1, dateTime2).toMillis();
        } catch (DateTimeParseException e) {
            return 0;
        }
    }

    /**
     * 두 날짜 사이의 차이를 지정된 단위로 계산합니다.
     *
     * @param unit "y"(년), "m"(월), "d"(일), "h"(시), "n"(분), "s"(초)
     */
    public static long diff(String unit, LocalDateTime dateTime1, LocalDateTime dateTime2) {
        if (dateTime1 == null || dateTime2 == null) return 0;
        switch (unit.toLowerCase(Locale.ROOT)) {
            case "y": return ChronoUnit.YEARS.between(dateTime1, dateTime2);
            case "m": return ChronoUnit.MONTHS.between(dateTime1, dateTime2);
            case "d": return ChronoUnit.DAYS.between(dateTime1, dateTime2);
            case "h": return ChronoUnit.HOURS.between(dateTime1, dateTime2);
            case "n": return ChronoUnit.MINUTES.between(dateTime1, dateTime2);
            case "s": return ChronoUnit.SECONDS.between(dateTime1, dateTime2);
            default: return 0;
        }
    }

    /**
     * "yyyyMMdd" 형식의 두 날짜 문자열 사이의 일수 차이를 계산합니다.
     */
    public static long daysBetween(String yyyyMMdd1, String yyyyMMdd2) {
        if (!StringUtils.hasText(yyyyMMdd1) || !StringUtils.hasText(yyyyMMdd2)) return 0;
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(YYYYMMDD);
            LocalDate date1 = LocalDate.parse(yyyyMMdd1, formatter);
            LocalDate date2 = LocalDate.parse(yyyyMMdd2, formatter);
            return ChronoUnit.DAYS.between(date1, date2);
        } catch (DateTimeParseException e) {
            return 0;
        }
    }

    /**
     * "yyyyMMdd" 형식의 두 날짜 문자열 사이의 시간 차이를 계산합니다.
     */
    public static long hoursBetween(String yyyyMMdd1, String yyyyMMdd2) {
        if (!StringUtils.hasText(yyyyMMdd1) || !StringUtils.hasText(yyyyMMdd2)) return 0;
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(YYYYMMDD);
            LocalDate date1 = LocalDate.parse(yyyyMMdd1, formatter);
            LocalDate date2 = LocalDate.parse(yyyyMMdd2, formatter);
            return ChronoUnit.HOURS.between(date1.atStartOfDay(), date2.atStartOfDay());
        } catch (DateTimeParseException e) {
            return 0;
        }
    }

    /**
     * "yyyyMMddHHmmss" 형식의 두 날짜 문자열 사이의 초 차이를 계산합니다.
     */
    public static long secondsBetween(String yyyyMMddHHmmss1, String yyyyMMddHHmmss2) {
        if (!StringUtils.hasText(yyyyMMddHHmmss1) || !StringUtils.hasText(yyyyMMddHHmmss2)) return 0;
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(YYYYMMDDHHMMSS);
            LocalDateTime dateTime1 = LocalDateTime.parse(yyyyMMddHHmmss1, formatter);
            LocalDateTime dateTime2 = LocalDateTime.parse(yyyyMMddHHmmss2, formatter);
            return ChronoUnit.SECONDS.between(dateTime1, dateTime2);
        } catch (DateTimeParseException e) {
            return 0;
        }
    }

    /**
     * startTimer()와 stopTimer()로 설정된 시간 사이의 경과 시간을 밀리초로 반환합니다.
     */
    public static long getElapsedTimeInMillis() {
        if (startTime.get() == null || endTime.get() == null) return 0;
        return Duration.between(startTime.get(), endTime.get()).toMillis();
    }

    // =================================================================================
    // 값 변환 (형식 변환)
    // =================================================================================

    /**
     * Date를 LocalDate로 변환합니다.
     */
    public static LocalDate toLocalDate(Date date) {
        if (date == null) return null;
        return date.toInstant().atZone(DEFAULT_ZONE_ID).toLocalDate();
    }

    /**
     * Date를 LocalDateTime으로 변환합니다.
     */
    public static LocalDateTime toLocalDateTime(Date date) {
        if (date == null) return null;
        return date.toInstant().atZone(DEFAULT_ZONE_ID).toLocalDateTime();
    }

    /**
     * LocalDate를 Date로 변환합니다.
     */
    public static Date toDate(LocalDate localDate) {
        if (localDate == null) return null;
        return Date.from(localDate.atStartOfDay(DEFAULT_ZONE_ID).toInstant());
    }

    /**
     * LocalDateTime를 Date로 변환합니다.
     */
    public static Date toDate(LocalDateTime localDateTime) {
        if (localDateTime == null) return null;
        return Date.from(localDateTime.atZone(DEFAULT_ZONE_ID).toInstant());
    }

    /**
     * 다양한 형식의 날짜 문자열을 Date로 변환합니다.
     */
    public static Date stringToDate(String dateStr) {
        if (!StringUtils.hasText(dateStr)) return null;
        try {
            // 여러 형식을 시도
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(YYYY_MM_DD);
            return toDate(LocalDate.parse(dateStr, formatter));
        } catch (DateTimeParseException e) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(YYYYMMDD);
                return toDate(LocalDate.parse(dateStr, formatter));
            } catch (DateTimeParseException e2) {
                return null;
            }
        }
    }

    /**
     * 지정된 형식의 문자열을 Date로 변환합니다.
     */
    public static Date stringToDate(String dateStr, String format) {
        if (!StringUtils.hasText(dateStr) || !StringUtils.hasText(format)) return null;
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
            return toDate(LocalDate.parse(dateStr, formatter));
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    /**
     * Date를 "yyyy-MM-dd" 형식의 문자열로 변환합니다.
     */
    public static String dateToString(Date date) {
        if (date == null) return "";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(YYYY_MM_DD);
        return toLocalDate(date).format(formatter);
    }

    /**
     * Date를 DateVO로 변환합니다.
     */
    public static DateVO toVO(Date date) {
        if (date == null) return null;
        LocalDateTime ldt = toLocalDateTime(date);
        DateVO vo = new DateVO();
        vo.setYear(ldt.getYear());
        vo.setMonth(ldt.getMonthValue());
        vo.setDay(ldt.getDayOfMonth());
        vo.setWeek(ldt.getDayOfWeek().getValue()); // MONDAY=1, SUNDAY=7
        return vo;
    }

    /**
     * LocalDate를 DateVO로 변환합니다.
     */
    public static DateVO toVO(LocalDate date) {
        if (date == null) return null;
        DateVO vo = new DateVO();
        vo.setYear(date.getYear());
        vo.setMonth(date.getMonthValue());
        vo.setDay(date.getDayOfMonth());
        vo.setWeek(date.getDayOfWeek().getValue()); // MONDAY=1, SUNDAY=7
        return vo;
    }

    /**
     * "yyyyMMddHHmmss" 형식의 문자열을 LocalDateTime으로 변환합니다.
     */
    public static LocalDateTime parseLocalDateTime(String yyyyMMddHHmmss) {
        if (!StringUtils.hasText(yyyyMMddHHmmss)) return null;
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(YYYYMMDDHHMMSS);
            return LocalDateTime.parse(yyyyMMddHHmmss, formatter);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    /**
     * 지정된 형식의 문자열을 LocalDateTime으로 변환합니다.
     */
    public static LocalDateTime parseLocalDateTime(String dateStr, String format) {
        if (!StringUtils.hasText(dateStr) || !StringUtils.hasText(format)) return null;
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
            return LocalDateTime.parse(dateStr, formatter);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    /**
     * LocalDate를 지정된 형식의 문자열로 변환합니다.
     */
    public static String format(LocalDate date, String format) {
        if (date == null || !StringUtils.hasText(format)) return "";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return date.format(formatter);
    }

    /**
     * LocalDateTime을 지정된 형식의 문자열로 변환합니다.
     */
    public static String format(LocalDateTime dateTime, String format) {
        if (dateTime == null || !StringUtils.hasText(format)) return "";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return dateTime.format(formatter);
    }

    /**
     * "yyyyMMdd" 형식의 날짜 문자열에 구분자를 추가합니다.
     */
    public static String formatDate(String yyyyMMdd, String delimiter) {
        if (!StringUtils.hasText(yyyyMMdd) || yyyyMMdd.length() != 8) return yyyyMMdd;
        return yyyyMMdd.substring(0, 4) + delimiter + yyyyMMdd.substring(4, 6) + delimiter + yyyyMMdd.substring(6, 8);
    }

    /**
     * "HHmmss" 또는 "HHmm" 형식의 시간 문자열에 구분자를 추가합니다.
     */
    public static String formatTime(String timeStr, String delimiter) {
        if (!StringUtils.hasText(timeStr)) return timeStr;
        if (timeStr.length() == 6) {
            return timeStr.substring(0, 2) + delimiter + timeStr.substring(2, 4) + delimiter + timeStr.substring(4, 6);
        } else if (timeStr.length() == 4) {
            return timeStr.substring(0, 2) + delimiter + timeStr.substring(2, 4);
        }
        return timeStr;
    }

    /**
     * 날짜 문자열을 "yyyy-MM-dd HH:mm:ss" 형식으로 변환합니다.
     */
    public static String toStandardDateTimeString(String dateStr, String format) {
        if (!StringUtils.hasText(dateStr) || !StringUtils.hasText(format)) return "";
        try {
            DateTimeFormatter inFormatter = DateTimeFormatter.ofPattern(format);
            LocalDateTime dateTime = LocalDateTime.parse(dateStr, inFormatter);
            DateTimeFormatter outFormatter = DateTimeFormatter.ofPattern(YYYY_MM_DD_HH_MM_SS);
            return dateTime.format(outFormatter);
        } catch (DateTimeParseException e) {
            return dateStr;
        }
    }

    /**
     * 밀리초를 "HH:mm:ss.SSS" 형식의 문자열로 변환합니다.
     */
    public static String formatMillis(long millis) {
        Duration duration = Duration.ofMillis(millis);
        long hours = duration.toHours();
        long minutes = duration.toMinutesPart();
        long seconds = duration.toSecondsPart();
        long milliPart = duration.toMillisPart();
        return String.format("%02d:%02d:%02d.%03d", hours, minutes, seconds, milliPart);
    }


    // =================================================================================
    // 기타
    // =================================================================================

    /**
     * MonthVO의 년/월 값을 검증하고 조정합니다.
     */
    public static MonthVO validate(MonthVO monthVO) {
        if (monthVO == null) return null;
        LocalDate date = LocalDate.of(Integer.parseInt(monthVO.getYear()), Integer.parseInt(monthVO.getMonth()), 1);
        monthVO.setYear(String.valueOf(date.getYear()));
        monthVO.setMonth(String.valueOf(date.getMonthValue()));
        return monthVO;
    }

    /**
     * 요일 인덱스를 한글 요일 문자열로 변환합니다. (1=월, ..., 7=일)
     */
    public static String getKoreanDayOfWeek(int dayIndex) {
        if (dayIndex < 1 || dayIndex > 7) return "";
        return DayOfWeek.of(dayIndex).getDisplayName(java.time.format.TextStyle.SHORT, Locale.KOREAN);
    }

    /**
     * 주의 첫 날짜(일요일)를 Date로 반환합니다.
     */
    public static Date getFirstDayOfWeek(Date date) {
        if (date == null) return null;
        LocalDate localDate = toLocalDate(date);
        return toDate(localDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY)));
    }

    /**
     * 주의 마지막 날짜(토요일)를 Date로 반환합니다.
     */
    public static Date getLastDayOfWeek(Date date) {
        if (date == null) return null;
        LocalDate localDate = toLocalDate(date);
        return toDate(localDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY)));
    }

    /**
     * 해당 월의 마지막 날짜를 int로 반환합니다.
     */
    public static int getLastDayOfMonth(int year, int month) {
        if (month < 1 || month > 12) return 0;
        return LocalDate.of(year, month, 1).lengthOfMonth();
    }

    /**
     * "yyyyMMdd" 형식의 날짜 문자열로부터 년도 주차를 계산합니다.
     */
    public static int getWeekOfYear(String yyyyMMdd) {
        if (!StringUtils.hasText(yyyyMMdd) || yyyyMMdd.length() != 8) return 0;
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(YYYYMMDD);
            LocalDate date = LocalDate.parse(yyyyMMdd, formatter);
            return date.get(WeekFields.of(Locale.KOREA).weekOfYear());
        } catch (DateTimeParseException e) {
            return 0;
        }
    }

    /**
     * 날짜 객체로부터 요일 이름을 반환합니다.
     */
    public static String getKoreanDayOfWeek(Object date) {
        if (date == null) return "";
        LocalDate localDate;
        if (date instanceof Date) {
            localDate = toLocalDate((Date) date);
        } else if (date instanceof LocalDate) {
            localDate = (LocalDate) date;
        } else if (date instanceof String) {
            try {
                localDate = LocalDate.parse((String) date);
            } catch (DateTimeParseException e) {
                return "";
            }
        } else {
            return "";
        }
        return localDate.getDayOfWeek().getDisplayName(java.time.format.TextStyle.FULL, Locale.KOREAN);
    }

    /**
     * ThreadLocal 리소스를 정리합니다.
     */
    public static void cleanupTimer() {
        startTime.remove();
        endTime.remove();
    }
}