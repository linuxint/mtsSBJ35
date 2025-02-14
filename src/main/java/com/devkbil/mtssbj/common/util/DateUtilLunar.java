package com.devkbil.mtssbj.common.util;

import com.ibm.icu.util.ChineseCalendar;

import org.springframework.util.StringUtils;

import java.util.Calendar;
import java.util.HashMap;

public class DateUtilLunar {

    /**
     * 입력받은 양력일자를 변환하여 음력일자로 반환
     *
     * @param sDate 양력일자
     * @return 음력일자
     */
    public static HashMap toLunar(String sDate) {
        String dateStr = validChkDate(sDate);

        HashMap hm = new HashMap();
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
     * 입력받은 음력일자를 변환하여 양력일자로 반환
     *
     * @param sDate      음력일자
     * @param iLeapMonth 음력윤달여부(IS_LEAP_MONTH)  윤달구분[0,1] (예, 1)
     * @return 양력일자
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
     * yyyyMMdd 형식의 날짜문자열을 원하는 캐릭터(ch)로 쪼개 돌려준다<br/>
     * <pre>
     * ex) 20030405, ch(.) -> 2003.04.05
     * ex) 200304, ch(.) -> 2003.04
     * ex) 20040101,ch(/) --> 2004/01/01 로 리턴
     * </pre>
     *
     * @param sDate yyyyMMdd 형식의 날짜문자열
     * @param ch    구분자
     * @return 변환된 문자열
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
     * 입력된 일자 문자열을 확인하고 8자리로 리턴
     *
     * @param dateStr 날짜문자열
     * @return 변환문자열
     */
    public static String validChkDate(String dateStr) {
        String retValue = dateStr;

        if (dateStr == null || !(dateStr.trim().length() == 8 || dateStr.trim().length() == 10)) {
            throw new IllegalArgumentException("Invalid date format: " + dateStr);
        }
        if (dateStr.length() == 10) {
            retValue = dateStr.replace("-", "");
        }
        return retValue;
    }


    /**
     * <p>
     * String이 비었거나("") 혹은 null 인지 검증한다.
     * </p>
     *
     * <pre>
     *  StringUtil.isEmpty(null)      = true
     *  StringUtil.isEmpty("")        = true
     *  StringUtil.isEmpty(" ")       = false
     *  StringUtil.isEmpty("bob")     = false
     *  StringUtil.isEmpty("  bob  ") = false
     * </pre>
     *
     * @param str - 체크 대상 스트링오브젝트이며 null을 허용함
     * @return <code>true</code> - 입력받은 String 이 빈 문자열 또는 null인 경우
     */
    public static boolean isEmpty(String str) {
        return !StringUtils.hasText(str);
    }

}
