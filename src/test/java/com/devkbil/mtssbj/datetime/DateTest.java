package com.devkbil.mtssbj.datetime;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateTest {
    public static void main(String[] args) {

        String[] a = new String[2];
        System.out.println(Arrays.toString(a));
        // 현재 날짜/시간
        LocalDateTime now = LocalDateTime.now();
        // 현재 날짜/시간 출력
        System.out.println(now); // 2022-05-03T15:52:21.419878100

        // 포맷팅
        String formatedNow = now.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분 ss초"));
        // 포맷팅 현재 날짜/시간 출력
        System.out.println(formatedNow); // 2022년 05월 03일 15시 52분 21초

        formatedNow = now.format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmssSSS_"));
        System.out.println(formatedNow); // 2022년 05월 03일 15시 52분 21초

        // 년, 월(문자열, 숫자), 일(월 기준, 년 기준), 요일(문자열, 숫자), 시, 분, 초 구하기
        int year = now.getYear(); // 연도
        String month = now.getMonth().toString(); // 월(문자열)
        int monthValue = now.getMonthValue(); // 월(숫자)
        int dayOfMonth = now.getDayOfMonth(); // 일(월 기준)
        int dayOfYear = now.getDayOfYear(); // 일(년 기준)
        String dayOfWeek = now.getDayOfWeek().toString(); // 요일(문자열)
        int dayOfWeekValue = now.getDayOfWeek().getValue(); // 요일(숫자)
        int hour = now.getHour();
        int minute = now.getMinute();
        int second = now.getSecond();

        // 년, 월(문자열, 숫자), 일(월 기준, 년 기준), 요일(문자열, 숫자), 시, 분, 초 출력
        System.out.println("년 : " + year); // 년 : 2022
        System.out.println("월 : " + month + "(" + monthValue + ")"); // 월 : MAY(5)
        System.out.println("일(월기준) : " + dayOfMonth); // 일(월기준) : 03
        System.out.println("일(년기준) : " + dayOfYear); // 일(년기준) : 122
        System.out.println("요일 : " + dayOfWeek + "(" + dayOfWeekValue + ")"); // 요일 : TUESDAY(2)
        System.out.println("시간 : " + hour); // 시간 : 15
        System.out.println("분 : " + minute); // 분 : 53
        System.out.println("초 : " + second); // 초 : 35

        // 1. LocalDate 생성
        LocalDate date = LocalDate.of(2021, 12, 25);
        System.out.println(date); // 2021-12-25

        // 2. DayOfWeek 객체 구하기
        DayOfWeek dayOfWeek2 = date.getDayOfWeek();

        // 3. 텍스트 요일 구하기 (영문)
        System.out.println(dayOfWeek2.getDisplayName(TextStyle.FULL, Locale.US));  // Saturday
        System.out.println(dayOfWeek2.getDisplayName(TextStyle.NARROW, Locale.US));  // S
        System.out.println(dayOfWeek2.getDisplayName(TextStyle.SHORT, Locale.US));  // Sat

        // 4. 텍스트 요일 구하기 (한글)
        System.out.println(dayOfWeek2.getDisplayName(TextStyle.FULL, Locale.KOREAN));  // 토요일
        System.out.println(dayOfWeek2.getDisplayName(TextStyle.NARROW, Locale.KOREAN));  // 토
        System.out.println(dayOfWeek2.getDisplayName(TextStyle.SHORT, Locale.KOREAN));  // 토

        // 5. 텍스트 요일 구하기 (default)
        System.out.println(dayOfWeek2.getDisplayName(TextStyle.FULL, Locale.getDefault()));  // 토요일

        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        System.out.println(cal.getTime());

    }
}
