package com.devkbil.common.util;

import com.ibm.icu.util.ChineseCalendar;

import org.springframework.util.StringUtils;

import java.util.Calendar;
import java.util.HashMap;

/**
 * 음력/양력 날짜 변환을 위한 유틸리티 클래스입니다.
 * <p>
 * 주요 기능:
 * - 양력에서 음력으로 날짜 변환
 * - 음력에서 양력으로 날짜 변환
 * - 날짜 문자열 형식 변환
 * - 날짜 문자열 유효성 검사
 */
public class DateLunar {

    /**
     * 양력 날짜를 음력 날짜로 변환합니다.
     *
     * @param sDate 양력 날짜 (형식: "yyyyMMdd" 또는 "yyyy-MM-dd")
     * @return HashMap 객체로 다음 정보를 포함합니다:
     * - "day": 음력 날짜 (yyyyMMdd 형식)
     * - "leap": 윤달 여부 (0: 평달, 1: 윤달)
     * @throws IllegalArgumentException 날짜 형식이 잘못된 경우
     */
    public static HashMap<String, Object> toLunar(String sDate) {
        String dateStr = validChkDate(sDate);

        HashMap<String, Object> hm = new HashMap<>();
        hm.put("day", "");
        hm.put("leap", 0);

        if (dateStr.length() != 8) {
            return hm;
        }

        Calendar cal;
        ChineseCalendar lcal;

        cal = Calendar.getInstance();
        lcal = new ChineseCalendar();

        cal.set(Calendar.YEAR, Integer.parseInt(dateStr.substring(0, 4)));
        cal.set(Calendar.MONTH, Integer.parseInt(dateStr.substring(4, 6)) - 1);
        cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dateStr.substring(6, 8)));

        lcal.setTimeInMillis(cal.getTimeInMillis());

        String year = String.valueOf(lcal.get(ChineseCalendar.EXTENDED_YEAR) - 2637);
        String month = String.valueOf(lcal.get(ChineseCalendar.MONTH) + 1);
        String day = String.valueOf(lcal.get(ChineseCalendar.DAY_OF_MONTH));
        String leap = String.valueOf(lcal.get(ChineseCalendar.IS_LEAP_MONTH));

        String pad4Str = "0000";
        String pad2Str = "00";

        String retYear = (pad4Str + year).substring(year.length());
        String retMonth = (pad2Str + month).substring(month.length());
        String retDay = (pad2Str + day).substring(day.length());

        String sDay = retYear + retMonth + retDay;

        hm.put("day", sDay);
        hm.put("leap", leap);

        return hm;
    }

    /**
     * 음력 날짜를 양력 날짜로 변환합니다.
     *
     * @param sDate      음력 날짜 (형식: "yyyyMMdd" 또는 "yyyy-MM-dd")
     * @param iLeapMonth 윤달 여부 (0: 평달, 1: 윤달)
     * @return 양력 날짜 (yyyyMMdd 형식)
     * @throws IllegalArgumentException 날짜 형식이 잘못된 경우
     */
    public static String toSolar(String sDate, int iLeapMonth) {
        String dateStr = validChkDate(sDate);

        Calendar cal = Calendar.getInstance();
        ChineseCalendar lcal = new ChineseCalendar();

        lcal.set(ChineseCalendar.EXTENDED_YEAR, Integer.parseInt(dateStr.substring(0, 4)) + 2637);
        lcal.set(ChineseCalendar.MONTH, Integer.parseInt(dateStr.substring(4, 6)) - 1);
        lcal.set(ChineseCalendar.DAY_OF_MONTH, Integer.parseInt(dateStr.substring(6, 8)));
        lcal.set(ChineseCalendar.IS_LEAP_MONTH, iLeapMonth);

        cal.setTimeInMillis(lcal.getTimeInMillis());

        String year = String.valueOf(cal.get(Calendar.YEAR));
        String month = String.valueOf(cal.get(Calendar.MONTH) + 1);
        String day = String.valueOf(cal.get(Calendar.DAY_OF_MONTH));

        String pad4Str = "0000";
        String pad2Str = "00";

        String retYear = (pad4Str + year).substring(year.length());
        String retMonth = (pad2Str + month).substring(month.length());
        String retDay = (pad2Str + day).substring(day.length());

        return retYear + retMonth + retDay;
    }

    /**
     * 날짜 문자열에 구분자를 추가하여 형식화된 문자열로 변환합니다.
     *
     * @param sDate 날짜 문자열 (yyyyMMdd, yyyyMM, yyyy 형식)
     * @param ch    구분자 (예: ".", "-", "/")
     * @return 형식화된 날짜 문자열
     * @throws IllegalArgumentException 날짜 형식이 잘못된 경우
     */
    public static String formatDate(String sDate, String ch) {
        String dateStr = validChkDate(sDate);

        String str = dateStr.trim();
        String yyyy = "";
        String mm = "";
        String dd = "";

        if (str.length() == 8) {
            yyyy = str.substring(0, 4);
            if (yyyy.equals("0000")) {
                return "";
            }

            mm = str.substring(4, 6);
            if (mm.equals("00")) {
                return yyyy;
            }
            dd = str.substring(6, 8);
            if (dd.equals("00")) {
                return yyyy + ch + mm;
            }
            return yyyy + ch + mm + ch + dd;
        } else if (str.length() == 6) {
            yyyy = str.substring(0, 4);
            if (yyyy.equals("0000")) {
                return "";
            }
            mm = str.substring(4, 6);
            if (mm.equals("00")) {
                return yyyy;
            }
            return yyyy + ch + mm;
        } else if (str.length() == 4) {
            yyyy = str.substring(0, 4);
            if (yyyy.equals("0000")) {
                return "";
            } else {
                return yyyy;
            }
        } else {
            return "";
        }
    }

    /**
     * 날짜 문자열의 유효성을 검사하고 표준 형식으로 변환합니다.
     *
     * @param dateStr 검사할 날짜 문자열
     * @return 하이픈이 제거된 8자리 날짜 문자열 (yyyyMMdd)
     * @throws IllegalArgumentException 다음의 경우 발생:
     *                                  - 입력이 null인 경우
     *                                  - 날짜 문자열이 8자리 또는 10자리가 아닌 경우
     *                                  - 날짜 형식이 올바르지 않은 경우
     */
    public static String validChkDate(String dateStr) {
        String retValue = dateStr;

        if (dateStr == null || !(dateStr.trim().length() == 8 || dateStr.trim().length() == 10)) {
            throw new IllegalArgumentException("날짜 형식이 올바르지 않습니다: " + dateStr);
        }
        if (dateStr.length() == 10) {
            retValue = dateStr.replace("-", "");
        }
        return retValue;
    }

    /**
     * 문자열이 비어있거나 null인지 검사합니다.
     *
     * @param str 검사할 문자열 (null 허용)
     * @return true: 문자열이 null이거나 빈 문자열인 경우
     * false: 문자열에 공백이나 문자가 포함된 경우
     */
    public static boolean isEmpty(String str) {
        return !StringUtils.hasText(str);
    }

}