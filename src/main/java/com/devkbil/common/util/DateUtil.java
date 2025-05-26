package com.devkbil.common.util;

import com.devkbil.mtssbj.schedule.DateVO;
import com.devkbil.mtssbj.schedule.MonthVO;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.util.StringUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import lombok.extern.slf4j.Slf4j;

/**
 * 날짜 관련 유틸리티 클래스입니다.
 * 이 클래스는 하위 호환성을 위해 유지되며, 새로운 코드에서는 ModernDateUtil 클래스를 사용하는 것이 권장됩니다.
 * 
 * @see ModernDateUtil
 */
@Slf4j
public class DateUtil {
    /**
     * 일자들끼리 연산을 수행할때 Return값의 단위를 정해주는 상수로 사용된다.
     * 이 상수는 일단위의 값을 Return하여 준다.
     */

    public static final char DAY = 'D';
    /**
     * 일자들끼리 연산을 수행할때 Return값의 단위를 정해주는 상수로 사용된다.
     * 이 상수는 시단위의 값을 Return하여 준다.
     */

    public static final char HOUR = 'H';
    /*    private static String EX_GMT_TO_DATE      = "gmtToDate: invalid gmt date string!";
        private static String EX_SQL_COMP1        = "getSQLComp (1 date) : illegal operator value! Allowed values are =,>,<,<=,>=,<>";
        private static String EX_SQL_COMP2        = "getSQLComp (2 dates) : illegal operator value! Allowed values are ><,>=<,><=,>=<=";
        private static String EX_GET_MONTH_NAME   = "getMonthName : month value must be between 1 and 12!";
        private static String EX_GET_ELAPSED_TIME = "getElapsedTime : begin date must be inferior to end date !";
        private static String EX_GET_DAY_NAME     = "getDayName: day value must be between 1 and 7!";
        private static String EX_FORMAT_MONTH     = "Invalid month name";
        private static String EX_DATE_TO_GMT      = "dateToGmt: unknown RFC format";*/
    /**
     * 일자들끼리 연산을 수행할때 Return값의 단위를 정해주는 상수로 사용된다.
     * 이 상수는 분단위의 값을 Return하여 준다.
     */

    private static final String yyyy = "yyyy";
    private static final String MM = "MM";
    private static final String dd = "dd";
    private static final String yyyyMMdd_bar = "yyyy-MM-dd";
    private static final String yyyyMMdd_dot = "yyyy.MM.dd";
    private static final String yyyyMMdd_strip = "yyyyMMdd";
    private static final String yyyyMMddHHmmss_bar = "yyyy-MM-dd HH:mm:ss";
    private static final String yyyyMMddHHmmss_strip = "yyyyMMddHHmmss";

    public static final char MIN = 'M';
    private static final String[] DAY_ARR = new String[] {"일", "월", "화", "수", "목", "금", "토"};

    private static final String MSG_ALERT = "EXPIRED..";

    public static String getYyyyMMdd_bar() {
        return yyyyMMdd_bar;
    }

    public static Date convDate(String sDate) throws ParseException {
        return convDate(sDate, yyyyMMddHHmmss_strip);
    }

    public static Date convDate(String sDate, String sFormat) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat(sFormat, Locale.KOREA);
        Date retDate = formatter.parse(sDate);
        return retDate;
    }

    public static MonthVO monthValid(MonthVO monthVO) {
        if (Integer.parseInt(monthVO.getMonth()) < 1) {
            monthVO.setMonth(String.valueOf(Integer.parseInt(monthVO.getMonth()) + 12));
            monthVO.setYear(String.valueOf(Integer.parseInt(monthVO.getYear()) - 1));
        }
        if (Integer.parseInt(monthVO.getMonth()) > 12) {
            monthVO.setMonth(String.valueOf(Integer.parseInt(monthVO.getMonth()) - 12));
            monthVO.setYear(String.valueOf(Integer.parseInt(monthVO.getYear()) + 1));
        }
        return monthVO;
    }

    /**
     * 시스템의 오늘 일자를 반환한다.
     *
     * @return 현재 시스템 날짜
     */
    public static Date getToday() {
        // 최신 구현으로 위임
        return ModernDateUtil.getToday();
    }

    /**
     * 문자열을 날짜형으로 변환.
     *
     * @param date 변환할 날짜 문자열 (yyyy-MM-dd 형식)
     * @return 변환된 Date 객체
     */
    public static Date stringToDate(String date) {
        // 최신 구현으로 위임
        return ModernDateUtil.stringToDate(date);
    }

    /**
     * 날짜를 문자열로 변환.
     *
     * @param date 변환할 Date 객체
     * @return yyyy-MM-dd 형식의 날짜 문자열
     */
    public static String date2Str(Date date) {
        SimpleDateFormat ft = new SimpleDateFormat(yyyyMMdd_bar);
        return ft.format(date.getTime());
    }

    /**
     * 문자열을 날짜(yyyy-MM-dd)로 변환.
     *
     * @param date yyyy-MM-dd 형식의 날짜 문자열
     * @return 변환된 Date 객체, 변환 실패시 null
     */
    public static Date str2Date(String date) {
        SimpleDateFormat ft = new SimpleDateFormat(yyyyMMdd_bar);
        Date ret = null;
        try {
            ret = ft.parse(date);
        } catch (ParseException ex) {
            log.error("date parse error ");
        }
        return ret;
    }

    /**
     * 날짜를 년월일로 구분하여 저장.
     *
     * @param date 변환할 Date 객체
     * @return 년, 월, 일, 요일이 분리되어 저장된 DateVO 객체
     */
    public static DateVO date2VO(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        DateVO dvo = new DateVO();
        dvo.setYear(cal.get(Calendar.YEAR));
        dvo.setMonth(cal.get(Calendar.MONTH) + 1);
        dvo.setDay(cal.get(Calendar.DAY_OF_MONTH));
        dvo.setWeek(cal.get(Calendar.DAY_OF_WEEK));
        return dvo;
    }

    /**
     * 년도 추출.
     *
     * @param date 년도를 추출할 Date 객체
     * @return 주어진 날짜의 년도 (YYYY)
     */
    public static Integer getYear(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        return cal.get(Calendar.YEAR);
    }

    /**
     * 월 추출.
     *
     * @param date 월을 추출할 Date 객체
     * @return 주어진 날짜의 월 (1-12)
     */
    public static Integer getMonth(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        return cal.get(Calendar.MONTH) + 1;
    }

    /**
     * 한 주의 순서 (요일).
     * 예: 일요일 = 0
     *
     * @param date 요일을 추출할 Date 객체
     * @return 요일 값 (0: 일요일, 1: 월요일, ..., 6: 토요일)
     */
    public static Integer getDayOfWeek(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.DAY_OF_WEEK) - 1;
    }

    /**
     * 월의 몇 번째 주 인지 추출.
     * 예: 반환값이 4이면 (7월) 4번째 주
     *
     * @param date 주차를 계산할 Date 객체
     * @return 해당 월의 주차 (1부터 시작)
     */
    public static Integer getWeekOfMonth(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.WEEK_OF_MONTH);
    }

    public static String getWeekString(Integer idx) {
        return DAY_ARR[idx];
    }

    /**
     * 한주의 시작일자.
     *
     * @param date 기준이 되는 Date 객체
     * @return 해당 주의 첫 번째 날짜 (일요일)
     */
    public static Date getFirstOfWeek(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        Integer dw = cal.get(Calendar.DAY_OF_WEEK) - 1;
        cal.add(Calendar.DATE, dw * -1);
        return cal.getTime();
    }

    /**
     * 한주의 종료일자.
     *
     * @param date 기준이 되는 Date 객체
     * @return 해당 주의 마지막 날짜 (토요일)
     */
    public static Date getLastOfWeek(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        Integer dw = cal.get(Calendar.DAY_OF_WEEK);
        cal.add(Calendar.DATE, 7 - dw);
        return cal.getTime();
    }

    /**
     * 두 날짜(date1,date2)의 일수: 시간 포함해서 계산하면 오류가 있어 날짜만 추출해서 계산.
     *
     * @param date1 시작 날짜
     * @param date2 종료 날짜
     * @return 두 날짜 사이의 일수 차이
     */
    public static Integer dateDiff(Date date1, Date date2) {
        String dt1 = date2Str(date1);
        String dt2 = date2Str(date2);

        Integer day = (int)((str2Date(dt1).getTime() - str2Date(dt2).getTime()) / (24 * 60 * 60 * 1000));
        return day;
    }

    /**
     * 날자 계산: -1은 감소.
     *
     * @param date 기준 날짜
     * @param calDay 더하거나 뺄 일수 (음수는 감소)
     * @return 계산된 날짜
     */
    public static Date dateAdd(Date date, Integer calDay) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, calDay);

        return cal.getTime();
    }

    /**
     * 달 계산: -1은 감소.
     *
     * @param date 기준 날짜
     * @param month 더하거나 뺄 월수 (음수는 감소)
     * @return 계산된 날짜
     */
    public static Date monthAdd(Date date, Integer month) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MONTH, month);

        return cal.getTime();
    }

    public static Date addDate(Date baseDate, Date additionalDate) {
        if (evaluationOver()) {
            return new Date(0L);
        }
        long baseTime = baseDate.getTime();
        long additionalTime = additionalDate.getTime();
        long combinedTime = baseTime + additionalTime;
        return new Date(combinedTime);
    }

    public static Date addDay(Date originalDate, long daysToAdd) {
        if (evaluationOver()) {
            return new Date(0L);
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(originalDate);
        calendar.add(Calendar.DATE, (int)daysToAdd);
        Date updatedDate = calendar.getTime();
        return updatedDate;
    }

    public static String addDay(String argBasicDate, int argAddDay) throws Exception {
        String basicDate = argBasicDate;
        int addDay = argAddDay;
        basicDate = checkDateType(basicDate);
        if ("".equals(basicDate)) {
            return "";
        }
        Calendar cld = Calendar.getInstance();
        cld.set(Integer.parseInt(basicDate.substring(0, 4)), Integer.parseInt(basicDate.substring(4, 6)) - 1, Integer.parseInt(basicDate.substring(6, 8)));

        cld.add(5, addDay);

        basicDate = Integer.toString(cld.get(1));
        basicDate = basicDate + (Integer.toString(cld.get(2) + 1).length() == 1 ? "0" + (cld.get(2) + 1) : Integer.toString(cld.get(2) + 1));
        basicDate = basicDate + (Integer.toString(cld.get(5)).length() == 1 ? "0" + cld.get(5) : Integer.toString(cld.get(5)));

        return basicDate;
    }

    public static String checkDateType(String argDateStr) throws Exception {
        String dateStr = argDateStr;
        dateStr = NewStringUtil.replace(dateStr, "/", "");
        dateStr = NewStringUtil.replace(dateStr, "-", "");
        if ((dateStr.length() == 0) || (dateStr.length() != 8)) {
            return "";
        }
        for (int jj = 0; jj < dateStr.length(); jj++) {
            if ((dateStr.charAt(jj) < '0') || (dateStr.charAt(jj) > '9')) {
                return "";
            }
        }
        int year = Integer.parseInt(dateStr.substring(0, 4));
        int month = Integer.parseInt(dateStr.substring(4, 6));
        int day = Integer.parseInt(dateStr.substring(6, 8));
        if (year < 1970) {
            return "";
        }
        if ((month > 12) || (month < 1)) {
            return "";
        }
        if ((day > getEndDayOfMonth(year, month)) || (day < 1)) {
            return "";
        }
        return dateStr;
    }

    public static int getEndDayOfMonth(int year, int month) {
        int[] monarr = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
        if (((year % 4 == 0) && (year % 100 != 0)) || (year % 400 == 0)) {
            monarr[1] = 29;
        }
        return monarr[(month - 1)];
    }

    private static boolean evaluationOver() {
        return false;
    }

    public static String formatDate(Date inputDate, String dateFormatPattern) {
        if (evaluationOver()) {
            return MSG_ALERT;
        }
        return DateFormatUtils.format(inputDate, dateFormatPattern);
    }

    public static Date getGMTDate() {
        if (evaluationOver()) {
            return new Date(0L);
        }
        Date currentDate = new Date();
        long currentTimeMillis = currentDate.getTime();
        long serverGMTOffsetMillis = (long)getServerGMTOffset() * 60 * 1000;
        Date gmtDate = new Date(currentTimeMillis - serverGMTOffsetMillis);
        return gmtDate;
    }

    public static int getGMTHour() {
        if (evaluationOver()) {
            return 0;
        }
        Date gmtDate = getGMTDate();
        int gmtHour = Integer.parseInt(formatDate(gmtDate, "H"));
        return gmtHour;
    }

    public static long getGMTOffset(Date inputDate) {
        if (evaluationOver()) {
            return 0L;
        }
        Date gmtDate = getGMTDate();
        long gmtDateTime = gmtDate.getTime();
        long inputDateTime = inputDate.getTime();
        double timeDifferenceMillis = inputDateTime - gmtDateTime;
        double timeDifferenceSeconds = timeDifferenceMillis / 1000.0D;
        double timeDifferenceMinutes = Math.round(timeDifferenceSeconds) / 60L;
        return Math.round(timeDifferenceMinutes);
    }

    public static String getMonthName(int monthIndex) throws Exception {
        int adjustedMonthIndex = monthIndex;
        if (evaluationOver()) {
            return MSG_ALERT;
        }
        adjustedMonthIndex--;
        if ((adjustedMonthIndex >= 0) && (adjustedMonthIndex <= 11)) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            calendar.set(Calendar.MONTH, adjustedMonthIndex);
            return formatDate(calendar.getTime(), "MMMM");
        }
        return "";
        //throw new Exception(EX_GET_MONTH_NAME);
    }

    public static String getMonthName(Date date) {
        if (evaluationOver()) {
            return MSG_ALERT;
        }
        return formatDate(date, "MMMM");
    }

    public static int getServerGMTOffset() {
        if (evaluationOver()) {
            return 0;
        }
        Calendar calendar = Calendar.getInstance();
        int era = calendar.get(Calendar.ERA);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        int millis = calendar.get(Calendar.MILLISECOND);
        int offsetMillis = Calendar.getInstance().getTimeZone().getOffset(era, year, month, day, dayOfWeek, millis);
        return offsetMillis / 1000 / 60;
    }

    /**
     * [오퍼레이션명] getHour
     * [요약] 현재 시간을 가져온다.
     *
     * @return 현재 시간 (24시간 형식, 0-23)
     */
    public static int getHour() {

        Calendar cal = Calendar.getInstance(Locale.getDefault());
        return cal.get(Calendar.HOUR_OF_DAY);

    }

    /**
     * [오퍼레이션명] getMinute
     * [요약] 현재 분을 가져온다.
     *
     * @return 현재 분 (0-59)
     */
    public static int getMinute() {

        Calendar cal = Calendar.getInstance(Locale.getDefault());
        return cal.get(Calendar.MINUTE);

    }

    /**
     * [오퍼레이션명] getYear
     * [요약] 현재 년도를 가져온다.
     *
     * @return 현재 년도 (YYYY)
     */
    public static int getYear() {

        Calendar cal = Calendar.getInstance(Locale.getDefault());
        return cal.get(Calendar.YEAR);

    }

    /**
     * [오퍼레이션명] getMonth
     * [요약] 현재 월을 가져온다.
     *
     * @return 현재 월 (0-11, 0: 1월, 11: 12월)
     */
    public static int getMonth() {

        Calendar cal = Calendar.getInstance(Locale.getDefault());
        return cal.get(Calendar.MONTH);

    }

    /**
     * [오퍼레이션명] getDate
     * [요약] 현재 일을 가져온다.
     *
     * @return 현재 일 (1-31)
     */
    public static int getDate() {

        Calendar cal = Calendar.getInstance(Locale.getDefault());
        return cal.get(Calendar.DATE);

    }

    /**
     * 전달받은 날짜(Date)를 지정된 Format의 날짜 표현형식으로 돌려준다. <BR>
     * <p>
     * 사용예) getToday("yyyy/MM/dd hh:mm a")<BR>
     * 결 과 ) 2001/12/07 10:10 오후<BR>
     * <p>
     * <p>
     * Format은 J2SE의 SimpleDateFormat의 Documentation을 참고한다.
     *
     * @param pOutformat 출력할 날짜 형식 (예: "yyyy/MM/dd hh:mm a")
     * @param vDate 변환할 Date 객체
     * @return 지정된 형식으로 변환된 날짜 문자열, 변환 실패시 null
     */
    public static String getDateFormat(String pOutformat, Date vDate) {

        SimpleDateFormat pOutformatter = new SimpleDateFormat(pOutformat, Locale.KOREA);
        String rDateString;
        try {
            rDateString = pOutformatter.format(vDate);
        } catch (Exception e) {
            rDateString = null;
        }
        return rDateString;
    }

    /**
     * 전달받은 날짜(Date)를 지정된 Format의 날짜 표현형식으로 돌려준다. <BR>
     * <p>
     * 사용예) getToday("yyyy/MM/dd hh:mm a")<BR>
     * 결 과 ) 2001/12/07 10:10 오후<BR>
     * <p>
     * Format은 J2SE의 SimpleDateFormat의 Documentation을 참고한다.
     *
     * @param pIndate 변환할 날짜 문자열
     * @param pInformat 입력 날짜의 형식
     * @param pOutformat 출력할 날짜 형식
     * @return 변환된 날짜 문자열, 변환 실패시 입력 문자열 그대로 반환
     */
    public static String getDateFormat(String pIndate, String pInformat, String pOutformat) {

        SimpleDateFormat pInformatter = new SimpleDateFormat(pInformat, Locale.KOREA);
        String rDateString;

        Date vIndate;
        try {
            vIndate = pInformatter.parse(pIndate);
            rDateString = getDateFormat(pOutformat, vIndate);
        } catch (Exception e) {
            rDateString = pIndate;
        }
        return rDateString;

    }

    /**
     * 전달받은 날짜(Date)를 지정된 Format의 날짜 표현형식으로 돌려준다. <BR>
     * <p>
     * 사용예) getDateFormat("20101121","-")<BR>
     * 결 과 ) 2010-11-21<BR>
     *
     * @param date 변환할 날짜 문자열 (yyyyMMdd 형식)
     * @param type 구분자 (예: "-", ".")
     * @return 구분자가 포함된 날짜 문자열, 입력값이 유효하지 않으면 빈 문자열 반환
     */
    public static String getDateFormat(String date, String type) {
        String rDateString;

        if (!StringUtils.hasText(date)) {
            rDateString = "";
        } else {
            if (date.length() < 8) {
                rDateString = date;
            } else {
                rDateString = date.substring(0, 4) + type + date.substring(4, 6) + type + date.substring(6);
            }
        }

        return rDateString;

    }

    /**
     * 전달받은 날짜(Date)를 지정된 Format의 날짜 표현형식으로 돌려준다. <BR>
     * <p>
     * 사용예) getDateFormat("20101121","-")<BR>
     * 결 과 ) 2010-11-21<BR>
     * <p>
     * 전달받은 날짜가 00000000 으로 내려오면 공백리턴함 (윤진욱)
     *
     * @param date 변환할 날짜 문자열 (yyyyMMdd 형식)
     * @param type 구분자 (예: "-", ".")
     * @return 구분자가 포함된 날짜 문자열, 날짜가 00000000이거나 0이거나 빈 문자열이면 빈 문자열 반환
     */
    public static String getDateFormat2(String date, String type) {

        String rDateString;

        if ("00000000".equals(date) || "0".equals(date) || "".equals(date)) {
            // 날짜이면
            // 공백리턴함
            rDateString = "";
        } else {
            rDateString = date.substring(0, 4) + type + date.substring(4, 6) + type + date.substring(6);
        }
        return rDateString;

    }

    /**
     * 전달받은 시간을 지정된 Format의 시간 표현형식으로 돌려준다. <BR>
     *
     * @param date 변환할 시간 문자열 (HHmmss 또는 HHmm 형식)
     * @param type 구분자 (예: ":", "-")
     * @return 구분자가 포함된 시간 문자열 (HH:mm:ss 또는 HH:mm 형식)
     */
    public static String getTimeFormat(String date, String type) {

        String rDateString = "";

        if (date.length() == 6) {

            rDateString = date.substring(0, 2) + type + date.substring(2, 4) + type + date.substring(4, 6);

        } else if (date.length() == 4) {
            rDateString = date.substring(0, 2) + type + date.substring(2, 4);
        }
        return rDateString;
    }

    /**
     * 입력받은 날짜에 일/시/분 단위의 값을 더하여 출력Format에 따라 값을 넘겨준다. <BR>
     * <p>
     * Parameter는 입력일, 입력일 Format, 출력일 Format, 일단위 더하기, 시단위 더하기,
     * 분단위 더하기이다.
     * 간단한 사용예는 다음과 같다.
     * 사용예) LLog.debug.println(
     * getFormattedDateAdd("200201010605","yyyyMMddhhmm"
     * ,"yyyy/MM/dd HH:mm",-100,10,-11) );
     * 결과) 2001/09/23 15:54
     * Format은 J2SE의 SimpleDateFormat의 Documentation을 참고한다.
     *
     * @param pIndate 변환할 날짜 문자열
     * @param pInformat 입력 날짜의 형식
     * @param pOutformat 출력할 날짜 형식
     * @param pDay 더할 일수 (음수는 감소)
     * @param pHour 더할 시간 (음수는 감소)
     * @param pMin 더할 분 (음수는 감소)
     * @return 계산된 날짜를 지정된 출력 형식으로 변환한 문자열, 변환 실패시 입력 문자열 반환
     */
    public static String getFormattedDateAdd(String pIndate, String pInformat, String pOutformat, int pDay, int pHour, int pMin) {

        SimpleDateFormat pInformatter = new SimpleDateFormat(pInformat, Locale.KOREA);
        SimpleDateFormat pOutformatter = new SimpleDateFormat(pOutformat, Locale.KOREA);

        String rDateString;
        Date vIndate;
        long vAddon = (pDay * 24L * 60L * 60L * 1000L) + (pHour * 60L * 60L * 1000L) + (pMin * 60L * 1000L);
        try {
            vIndate = pInformatter.parse(pIndate);
            Date vAddday = new Date(vIndate.getTime() + vAddon);
            rDateString = pOutformatter.format(vAddday);
        } catch (Exception e) {
            rDateString = pIndate;
        }
        return rDateString;
    }

    /**
     * 입력받은 날짜에 연도 단위의 값을 더하여 출력Format에 따라 값을 넘겨준다. <BR>
     * <p>
     * Parameter는 입력일, 입력일 Format, 출력일 Format, 연도 단위 더하기이다.
     * 간단한 사용예는 다음과 같다.
     * 사용예) LLog.debug.println(
     * getFormattedDateYearAdd("200201010605","yyyyMMddhhmm","yyyy/MM/dd HH:mm",-6)
     * );
     * 결과) 1996/01/01 06:05
     * Format은 J2SE의 SimpleDateFormat의 Documentation을 참고한다.
     *
     * @param pIndate 변환할 날짜 문자열
     * @param pInformat 입력 날짜의 형식
     * @param pOutformat 출력할 날짜 형식
     * @param pYear 더할 연도 수 (음수는 감소)
     * @return 계산된 날짜를 지정된 출력 형식으로 변환한 문자열, 변환 실패시 입력 문자열 반환
     */
    public static String getFormattedDateYearAdd(String pIndate, String pInformat, String pOutformat, int pYear) {

        SimpleDateFormat pInformatter = new SimpleDateFormat(pInformat, Locale.KOREA);
        SimpleDateFormat pOutformatter = new SimpleDateFormat(pOutformat, Locale.KOREA);

        Calendar cal = Calendar.getInstance(Locale.getDefault());

        String rDateString;
        Date vIndate;

        try {

            vIndate = pInformatter.parse(pIndate);

            cal.setTime(vIndate);
            cal.add(Calendar.YEAR, pYear);

            rDateString = pOutformatter.format(cal.getTime());

        } catch (Exception e) {
            rDateString = pIndate;
        }
        return rDateString;
    }

    /**
     * 입력받은 날짜에 월 단위의 값을 더하여 출력Format에 따라 값을 넘겨준다. <BR>
     * <p>
     * Parameter는 입력일, 입력일 Format, 출력일 Format, 월 단위 더하기이다.
     * 간단한 사용예는 다음과 같다.
     * 사용예) LLog.debug.println(
     * getFormattedDateMonthAdd("200201010605","yyyyMMddhhmm","yyyy/MM/dd HH:mm",-6)
     * );
     * 결과) 2001/07/01 06:05
     * Format은 J2SE의 SimpleDateFormat의 Documentation을 참고한다.
     *
     * @param pIndate 변환할 날짜 문자열
     * @param pInformat 입력 날짜의 형식
     * @param pOutformat 출력할 날짜 형식
     * @param pMonth 더할 월 수 (음수는 감소)
     * @return 계산된 날짜를 지정된 출력 형식으로 변환한 문자열, 변환 실패시 입력 문자열 반환
     */
    public static String getFormattedDateMonthAdd(String pIndate, String pInformat, String pOutformat, int pMonth) {

        SimpleDateFormat pInformatter = new SimpleDateFormat(pInformat, Locale.KOREA);
        SimpleDateFormat pOutformatter = new SimpleDateFormat(pOutformat, Locale.KOREA);

        Calendar cal = Calendar.getInstance(Locale.getDefault());

        String rDateString;
        Date vIndate;

        try {

            vIndate = pInformatter.parse(pIndate);

            cal.setTime(vIndate);
            cal.add(Calendar.MONTH, pMonth);

            rDateString = pOutformatter.format(cal.getTime());

        } catch (Exception e) {
            rDateString = pIndate;
        }
        return rDateString;
    }

    /**
     * 입력받은 날짜에 시간 단위의 값을 더하여 출력Format에 따라 값을 넘겨준다. <BR>
     * <p>
     * Parameter는 입력일, 입력일 Format, 출력일 Format, 시간 단위 더하기이다.
     * 간단한 사용예는 다음과 같다.
     * 사용예) LLog.debug.println(
     * getFormattedDateHourAdd("200201010605","yyyyMMddhhmm","yyyy/MM/dd HH:mm",-6)
     * );
     * 결과) 2002/01/01 00:05
     * Format은 J2SE의 SimpleDateFormat의 Documentation을 참고한다.
     *
     * @param pIndate 변환할 날짜 문자열
     * @param pInformat 입력 날짜의 형식
     * @param pOutformat 출력할 날짜 형식
     * @param pHour 더할 시간 수 (음수는 감소)
     * @return 계산된 날짜를 지정된 출력 형식으로 변환한 문자열, 변환 실패시 입력 문자열 반환
     */
    public static String getFormattedDateHourAdd(String pIndate, String pInformat, String pOutformat, int pHour) {

        SimpleDateFormat pInformatter = new SimpleDateFormat(pInformat, Locale.KOREA);
        SimpleDateFormat pOutformatter = new SimpleDateFormat(pOutformat, Locale.KOREA);

        Calendar cal = Calendar.getInstance(Locale.getDefault());

        String rDateString;

        Date vIndate;

        try {

            vIndate = pInformatter.parse(pIndate);

            cal.setTime(vIndate);
            cal.add(Calendar.HOUR, pHour);

            rDateString = pOutformatter.format(cal.getTime());

        } catch (Exception e) {
            rDateString = pIndate;
        }
        return rDateString;
    }

    /**
     * 일자들의 계산을 수행한다..
     * 제일 마지막의 Parameter pType에 따라서 Return값이 다르다.
     * 둘째 Parameter는 첫째 Parameter의 입력 형식을 지정하고 넷째 Parameter는
     * 셋째 Parameter의 입력형식을 지정한다.
     * Return값의 단위를 정해주는 pType에는 4가지가 올 수 있는데
     * ECOMJDateU.DAY, ECOMJDateU.HOUR, ECOMJDateU.MIN, ECOMJDateU.SEC 이다.
     * 각각의 단위는 일단위, 시단위, 분단위 이다.
     * 첫째 Parameter로 입력받은 일자에서 셋째 Parameter로 입력받은 일자를 빼서
     * 나온 결과를 돌려준다.
     * Format은 J2SE의 SimpleDateFormat의 Documentation을 참고한다.
     * 간단한 사용예는 다음과 같다.
     * <p>
     * LLog.debug.println(getComputedDate("2002/01/04 00:01","yyyy/MM/dd hh:mm",
     * "2002/01/02 23:59","yyyy/MM/dd hh:mm",ECOMJDateU.DAY));
     * <p>
     * 작업 결과로 '1'이 표시된다.
     *
     * @param pIndate1   java.lang.String
     * @param pInformat1 java.lang.String
     * @param pIndate2   java.lang.String
     * @param pInformat2 java.lang.String
     * @return long
     */
    public static long getComputedDate(String pIndate1, String pInformat1, String pIndate2, String pInformat2) {

        SimpleDateFormat pInformatter1 = new SimpleDateFormat(pInformat1);
        SimpleDateFormat pInformatter2 = new SimpleDateFormat(pInformat2);

        long vDategap;

        try {

            Date vIndate1 = pInformatter1.parse(pIndate1);
            Date vIndate2 = pInformatter2.parse(pIndate2);

            vDategap = vIndate1.getTime() - vIndate2.getTime();

        } catch (Exception e) {
            // logger.error(e.getMessage(), e);
            return 0;
        }
        return vDategap;

    }

    /**
     * amount 차이만큼 날짜를 반환한다.
     *
     * @param amount 더하거나 뺄 일수 (음수는 감소)
     * @return 현재 날짜에서 지정된 일수만큼 계산된 날짜 (yyyyMMdd 형식)
     */
    public static String getAddDate(int amount) {

        Calendar cal = Calendar.getInstance(Locale.getDefault());
        cal.add(Calendar.DATE, amount);

        StringBuilder buf = new StringBuilder();
        buf.append(cal.get(1));

        String month = Integer.toString(cal.get(2) + 1);

        if (month.length() == 1) {
            month = "0" + month;
        }

        String day = Integer.toString(cal.get(5));

        if (day.length() == 1) {
            day = "0" + day;
        }

        buf.append(month);
        buf.append(day);

        return buf.toString();

    }

    /**
     * 기준 날짜에서 지정된 월수만큼 더하거나 뺀 날짜를 반환한다.
     *
     * @param basicDate 기준 날짜 (yyyyMMdd 형식)
     * @param amount 더하거나 뺄 월수 (음수는 감소)
     * @return 계산된 날짜 (yyyyMMdd 형식)
     */
    public static String getAddMonth(String basicDate, int amount) {

        Calendar cal = Calendar.getInstance(Locale.getDefault());

        int basicYear = Integer.parseInt(basicDate.substring(0, 4));
        int basicMonth = Integer.parseInt(basicDate.substring(4, 6)) - 1;
        int basicDay = Integer.parseInt(basicDate.substring(6, 8));

        cal.set(basicYear, basicMonth, basicDay);
        cal.add(Calendar.MONTH, amount);

        StringBuilder buf = new StringBuilder();

        buf.append(cal.get(1));

        String month = Integer.toString(cal.get(2) + 1);

        if (month.length() == 1) {
            month = "0" + month;
        }

        String day = Integer.toString(cal.get(5));

        if (day.length() == 1) {
            day = "0" + day;
        }

        buf.append(month);
        buf.append(day);

        return buf.toString();

    }

    /**
     * 현재 시간에서 지정된 시간만큼 더하거나 뺀 날짜/시간을 반환한다.
     *
     * @param amount 더하거나 뺄 시간 (음수는 감소)
     * @return 계산된 날짜와 시간 (yyyy-MM-dd HH:mm:ss 형식)
     */
    public static String getAddHour(int amount) {

        Calendar cal = Calendar.getInstance(Locale.getDefault());
        cal.add(Calendar.HOUR, amount);

        SimpleDateFormat dateprint = new SimpleDateFormat(yyyyMMddHHmmss_bar);

        return dateprint.format(cal.getTime());

    }

    /**
     * 현재 날짜에서 지정된 월수만큼 더하거나 뺀 날짜를 반환한다.
     *
     * @param amount 더하거나 뺄 월수 (음수는 감소)
     * @return 계산된 날짜 (yyyyMMdd 형식)
     */
    public static String getAddMonth(int amount) {

        Calendar cal = Calendar.getInstance(Locale.getDefault());
        cal.add(Calendar.MONTH, amount);

        StringBuilder buf = new StringBuilder();
        buf.append(cal.get(1));
        String month = Integer.toString(cal.get(2) + 1);

        if (month.length() == 1) {
            month = "0" + month;
        }

        String day = Integer.toString(cal.get(5));

        if (day.length() == 1) {
            day = "0" + day;
        }

        buf.append(month);
        buf.append(day);

        return buf.toString();

    }

    /**
     * 해당월에 마지막 일자 구하기
     *
     * @param yearMonth 년월 문자열 (yyyyMM 형식)
     * @return 해당 월의 마지막 일자 (28-31)
     */
    public static int getLastDayOfMonth(String yearMonth) {

        Calendar cal = Calendar.getInstance();

        int year = Integer.parseInt(yearMonth.substring(0, 4));
        int month = Integer.parseInt(yearMonth.substring(4, 6));

        cal.set(year, month - 1, 1);

        return cal.getActualMaximum(Calendar.DAY_OF_MONTH);

    }

    /**
     * <pre>
     * 입력받은 날짜 시간을 yyyy-MM-dd HH:mm:ss 형식으로 변환한다.
     * date는 format과 날짜형식이 같아야 한다.
     * </pre>
     *
     * @param date 변환할 날짜 문자열 (예: "2011-11-11 11:11:11")
     * @param format 입력 날짜의 형식 (예: "yyyy-MM-dd HH:mm:ss")
     * @return yyyy-MM-dd HH:mm:ss 형식으로 변환된 날짜 문자열, 변환 실패시 빈 문자열 반환
     */
    public static String getReqDate(String date, String format) {

        return getCurrentDate(date, format, yyyyMMddHHmmss_bar);

    }

    /**
     * "yyyy-MM-dd HH:mm:SS"형의 날짜를 반환한다.
     *
     * @return 현재 날짜와 시간을 yyyy-MM-dd HH:mm:ss 형식으로 변환한 문자열
     */
    public static String getCurrentDate() {

        return getCurrentDate(yyyyMMddHHmmss_bar);

    }

    /**
     * [오퍼레이션명] getCurrentDate
     * [요약] 날자 반환
     *
     * @param dateFormat 날짜 형식 (예: "yyyy-MM-dd HH:mm:ss")
     * @return 현재 날짜와 시간을 지정된 형식으로 변환한 문자열
     */
    public static String getCurrentDate(String dateFormat) {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat, Locale.KOREA);

        return simpleDateFormat.format(new Date());

    }

    /**
     * [오퍼레이션명] getCurrentDate
     * [요약] 날자 반환
     *
     * @param inputDate 변환할 날짜 문자열
     * @param inputFormat 입력 날짜의 형식
     * @param outputFormat 출력할 날짜 형식
     * @return 입력 날짜를 지정된 출력 형식으로 변환한 문자열, 변환 실패시 빈 문자열 반환
     */
    public static String getCurrentDate(String inputDate, String inputFormat, String outputFormat) {

        SimpleDateFormat outputDateFormat = new SimpleDateFormat(outputFormat, Locale.KOREA);
        SimpleDateFormat inputDateFormat = new SimpleDateFormat(inputFormat, Locale.KOREA);

        try {
            return outputDateFormat.format(inputDateFormat.parse(inputDate));
        } catch (ParseException e) {
            return "";
        }
    }

    /**
     * [오퍼레이션명] getCurrentDate
     * [요약] 날자 반환
     *
     * @param date 변환할 Date 객체
     * @param dateFormat 출력할 날짜 형식
     * @return 지정된 Date를 지정된 형식으로 변환한 문자열
     */
    public static String getCurrentDate(Date date, String dateFormat) {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat, Locale.KOREA);

        return simpleDateFormat.format(date);

    }

    /**
     * [오퍼레이션명] dayOfWeek
     * [요약] 요일 수 변환
     *
     * @param date 날짜 문자열 (yyyyMMdd 형식)
     * @return 요일 값 (1: 일요일, 2: 월요일, ..., 7: 토요일)
     */
    public static int dayOfWeek(String date) {

        Calendar cal = Calendar.getInstance();

        int year = Integer.parseInt(date.substring(0, 4));
        int month = Integer.parseInt(date.substring(4, 6));
        int day = Integer.parseInt(date.substring(6));

        cal.set(year, month - 1, day);

        return cal.get(Calendar.DAY_OF_WEEK);

    }

    /**
     * [오퍼레이션명] getDateDiff
     * [요약] 두 날자의 차이
     *
     * @param dataPart 반환할 단위 (y: 년, m: 월, d: 일, h: 시, n: 분, s: 초)
     * @param pIndate1 시작 날짜 (yyyy-MM-dd HH:mm:ss 형식)
     * @return 현재 시간과의 차이를 지정된 단위로 계산한 값
     */
    public static long getDateDiff(String dataPart, String pIndate1) {

        String pInformat1 = yyyyMMddHHmmss_bar;
        String pInformat2 = yyyyMMddHHmmss_bar;

        return getDateDiff(dataPart, pIndate1, pInformat1, getCurrentDate(), pInformat2);

    }

    /**
     * Computes the difference between two dates in the specified time unit.
     *
     * @param dataPart   The unit of time difference to be calculated (e.g., days, hours, minutes).
     * @param pIndate1   The first date in string format.
     * @param pIndate2   The second date in string format.
     * @return The computed time difference between the two dates in the specified unit.
     */
    public static long getDateDiff(String dataPart, String pIndate1, String pIndate2) {

        String pInformat1 = yyyyMMddHHmmss_bar;
        String pInformat2 = yyyyMMddHHmmss_bar;

        return getDateDiff(dataPart, pIndate1, pInformat1, pIndate2, pInformat2);

    }

    /**
     * Calculates the difference between two dates in a specified unit.
     *
     * @param argDataPart the unit of difference to calculate. Acceptable values are:
     *                    "y" for years,
     *                    "m" for months,
     *                    "d" for days,
     *                    "h" for hours,
     *                    "n" for minutes,
     *                    "s" for seconds. Any other value will result in a difference of 0.
     * @param argPindate1 the first date in string format. Can also be "now" to use the current date.
     * @param argPinformat1 the format of the first date (argPindate1) as a pattern string, e.g., "yyyy-MM-dd".
     *                      If argPindate1 is "now", this parameter defaults to "yyyyMMddHHmmss".
     * @param argPindate2 the second date in string format. Can also be "now" to use the current date.
     * @param argPinformat2 the format of the second date (argPindate2) as a pattern string, e.g., "yyyy-MM-dd".
     *                      If argPindate2 is "now", this parameter defaults to "yyyyMMddHHmmss".
     * @return the difference between the two dates in the specified unit.
     *         If an error occurs during processing, returns 0.
     */
    public static long getDateDiff(String argDataPart, String argPindate1, String argPinformat1, String argPindate2, String argPinformat2) {
        String dataPart = argDataPart;
        String pIndate1 = argPindate1;
        String pInformat1 = argPinformat1;
        String pIndate2 = argPindate2;
        String pInformat2 = argPinformat2;

        Date now = Calendar.getInstance().getTime();
        SimpleDateFormat nowFormatter = new SimpleDateFormat(yyyyMMddHHmmss_bar, Locale.KOREA);
        Calendar calendar1 = Calendar.getInstance();
        Calendar calendar2 = Calendar.getInstance();
        SimpleDateFormat pInformatter1 = new SimpleDateFormat(pInformat1, Locale.KOREA);
        SimpleDateFormat pInformatter2 = new SimpleDateFormat(pInformat2, Locale.KOREA);
        pIndate1 = "now".equals(pIndate1) ? nowFormatter.format(now) : pIndate1;
        pIndate2 = "now".equals(pIndate2) ? nowFormatter.format(now) : pIndate2;
        pInformat1 = "now".equals(pIndate1) ? yyyyMMddHHmmss_bar : pInformat1;
        pInformat2 = "now".equals(pIndate2) ? yyyyMMddHHmmss_bar : pInformat2;

        long vDategap;

        try {
            Date vIndate1 = pInformatter1.parse(pIndate1);
            Date vIndate2 = pInformatter2.parse(pIndate2);

            calendar1.setTime(vIndate1);
            calendar2.setTime(vIndate2);

            long vDateGapYear = calendar2.get(Calendar.YEAR) - calendar1.get(Calendar.YEAR);
            long vDateGapMonth = calendar2.get(Calendar.MONTH) - calendar1.get(Calendar.MONTH);

            if ("y".equals(dataPart)) {
                vDategap = vDateGapYear;
            } else if ("m".equals(dataPart)) {
                vDategap = (vDateGapYear * 12) + vDateGapMonth;
            } else if ("d".equals(dataPart)) {
                vDategap = (calendar2.getTimeInMillis() - calendar1.getTimeInMillis()) / (1000 * 3600 * 24);
            } else if ("h".equals(dataPart)) {
                vDategap = (calendar2.getTimeInMillis() - calendar1.getTimeInMillis()) / (1000 * 3600);
            } else if ("n".equals(dataPart)) {
                vDategap = (calendar2.getTimeInMillis() - calendar1.getTimeInMillis()) / (1000 * 60);
            } else if ("s".equals(dataPart)) {
                vDategap = (calendar2.getTimeInMillis() - calendar1.getTimeInMillis()) / 1000;
            } else {
                vDategap = 0;
            }
        } catch (Exception e) {
            // logger.error(e.getMessage(), e);
            return 0;
        }
        return vDategap;
    }

    /**
     * Calculates the difference between two dates in the specified unit of time.
     *
     * @param dataPart Represents the unit of time for the difference calculation.
     *                 Acceptable values are:
     *                 - "y" for years
     *                 - "m" for months
     *                 - "d" for days
     *                 - "h" for hours
     *                 - "n" for minutes
     *                 - "s" for seconds
     * @param pIndate1 The start date for the difference calculation.
     * @param pIndate2 The end date for the difference calculation.
     * @return The difference between the two dates in the specified unit of time.
     *         Returns 0 if an invalid dataPart is provided or an exception occurs.
     */
    public static long getDateDiff(String dataPart, Date pIndate1, Date pIndate2) {

        Calendar calendar1 = Calendar.getInstance();
        Calendar calendar2 = Calendar.getInstance();

        long vDategap;

        try {
            calendar1.setTime(pIndate1);
            calendar2.setTime(pIndate2);

            long vDateGapYear = calendar2.get(Calendar.YEAR) - calendar1.get(Calendar.YEAR);
            long vDateGapMonth = calendar2.get(Calendar.MONTH) - calendar1.get(Calendar.MONTH);
            if ("y".equals(dataPart)) {
                vDategap = vDateGapYear;
            } else if ("m".equals(dataPart)) {
                vDategap = (vDateGapYear * 12) + vDateGapMonth;
            } else if ("d".equals(dataPart)) {
                vDategap = (calendar2.getTimeInMillis() - calendar1.getTimeInMillis()) / (1000 * 3600 * 24);
            } else if ("h".equals(dataPart)) {
                vDategap = (calendar2.getTimeInMillis() - calendar1.getTimeInMillis()) / (1000 * 3600);
            } else if ("n".equals(dataPart)) {
                vDategap = (calendar2.getTimeInMillis() - calendar1.getTimeInMillis()) / (1000 * 60);
            } else if ("s".equals(dataPart)) {
                vDategap = (calendar2.getTimeInMillis() - calendar1.getTimeInMillis()) / 1000;
            } else {
                vDategap = 0;
            }
        } catch (Exception e) {
            // logger.error(e.getMessage(), e);
            return 0;
        }
        return vDategap;
    }

    /**
     * Extracts the week of the year from the given date string.
     *
     * @param thisday A string representing a date in the format "yyyyMMdd".
     * @return The week of the year as an integer, based on the provided date.
     */
    public static int getYearWeek(String thisday) {

        int year;
        int month;
        int day;

        Calendar cDate = Calendar.getInstance(); // Calendar 클래스의 인스턴스 생성
        year = Integer.parseInt(getDateFormat(thisday, yyyyMMdd_strip, yyyy));
        month = Integer.parseInt(getDateFormat(thisday, yyyyMMdd_strip, MM));
        day = Integer.parseInt(getDateFormat(thisday, yyyyMMdd_strip, dd));
        cDate.set(year, month - 1, day);

        return cDate.get(Calendar.WEEK_OF_YEAR);

    }

    /**
     * Calculates the difference in days between two dates provided as strings in the format "yyyyMMdd".
     *
     * @param begin the start date as a string in "yyyyMMdd" format
     * @param end the end date as a string in "yyyyMMdd" format
     * @return the difference in days between the two dates
     * @throws Exception if the date parsing fails or invalid date format is provided
     */
    public static long diffOfDate(String begin, String end) throws Exception {

        SimpleDateFormat formatter = new SimpleDateFormat(yyyyMMdd_strip);

        Date beginDate = formatter.parse(begin);
        Date endDate = formatter.parse(end);

        long diff = endDate.getTime() - beginDate.getTime();
        long diffDays = diff / (24 * 60 * 60 * 1000);

        return diffDays;

    }

    /**
     * Calculates the difference in hours between two date strings in the format yyyyMMdd_strip.
     *
     * @param begin the start date string in the format yyyyMMdd_strip
     * @param end the end date string in the format yyyyMMdd_strip
     * @return the difference in hours between the two dates
     * @throws Exception if the date format is invalid or parsing fails
     */
    public static long diffOfHour(String begin, String end) throws Exception {

        SimpleDateFormat formatter = new SimpleDateFormat(yyyyMMdd_strip);

        Date beginDate = formatter.parse(begin);
        Date endDate = formatter.parse(end);

        long diff = endDate.getTime() - beginDate.getTime();
        long diffDays = diff / (60 * 60 * 1000);

        return diffDays;

    }

    /**
     * Calculates the difference in seconds between two given timestamps.
     * The timestamps must be provided in the format "yyyyMMddHHmmss_strip".
     *
     * @param begin the start timestamp as a String
     * @param end the end timestamp as a String
     * @return the difference in seconds between the two timestamps
     * @throws Exception if the input strings cannot be parsed into valid dates
     */
    public static long diffOfSecond(String begin, String end) throws Exception {

        SimpleDateFormat formatter = new SimpleDateFormat(yyyyMMddHHmmss_strip);

        Date beginDate = formatter.parse(begin);
        Date endDate = formatter.parse(end);

        long diff = endDate.getTime() - beginDate.getTime();
        long diffSecond = diff / (1000);

        return diffSecond;

    }

    /**
     * Calculates and returns the first date of the week for a given date.
     * The week starts on Monday and the input date should be provided in the "yyyyMMdd" format.
     *
     * @param dtPrevDate The input date in "yyyyMMdd" format for which the first day of the week needs to be found.
     * @return A string representing the first date of the week in "yyyyMMdd" format.
     * @throws Exception If there are issues parsing the input date or other calendar-related operations.
     */
    public static String firstDateOfWeek(String dtPrevDate) throws Exception {
        Calendar cal = Calendar.getInstance(Locale.getDefault());

        cal.setFirstDayOfWeek(Calendar.MONDAY);
        cal.set(Integer.parseInt(dtPrevDate.substring(0, 4)), Integer.parseInt(dtPrevDate.substring(4, 6)) - 1, Integer.parseInt(dtPrevDate.substring(6)));

        int today = cal.get(Calendar.DAY_OF_WEEK);
        int firstDay = cal.getActualMinimum(Calendar.DAY_OF_WEEK);

        cal.add(Calendar.DAY_OF_MONTH, firstDay - today);

        String month = Integer.toString(cal.get(Calendar.MONTH) + 1);

        if (month.length() == 1) {
            month = "0" + month;
        }
        String day = Integer.toString(cal.get(Calendar.DAY_OF_MONTH));
        if (day.length() == 1) {
            day = "0" + day;
        }

        String buf = cal.get(Calendar.YEAR) + month + day;
        return buf;
    }

    /**
     * Retrieves the current time formatted according to the provided pattern.
     *
     * @param parsePatterns a string representing the date and time pattern to use for formatting
     * @return the current time as a formatted string based on the specified pattern,
     *         or an empty string if an error occurs
     */
    public static String getCurrentTime(String parsePatterns) {
        String returnValue = "";
        try {

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(parsePatterns);
            simpleDateFormat.setTimeZone(TimeZone.getDefault());
            returnValue = simpleDateFormat.format(new Date(System.currentTimeMillis()));
        } catch (Exception e) {
            // logger.error(e.getMessage(), e);
        }
        return returnValue;
    }

    /**
     * Retrieves the current date in the format yyyyMMdd as an integer.
     * The method formats the current date using a specified format
     * and converts it into an integer representation.
     *
     * @return An integer representing the current date in yyyyMMdd format.
     */
    public static int getCurrDate() {

        DateFormat df = new SimpleDateFormat(yyyyMMdd_dot);
        String date = df.format(new Date());
        return Integer.parseInt(date.substring(0, 4) + date.substring(5, 7) + date.substring(8, 10));

    }

    /**
     * Returns the current time in HHMMSS format as an integer.
     * The method formats the current time using the Locale.UK time standard
     * and extracts the hour, minute, and second components, concatenating them into a single integer.
     *
     * @return the current time represented as an integer in HHMMSS format
     */
    public static int getCurrTime() {

        DateFormat df = DateFormat.getTimeInstance(DateFormat.MEDIUM, Locale.UK);
        String time = df.format(new Date());
        return Integer.parseInt(time.substring(0, 2) + time.substring(3, 5) + time.substring(6, 8));

    }

    /**
     * Returns the current date and time formatted as a string.
     * The output format is specified by the SimpleDateFormat pattern provided in the method.
     *
     * @return The current date and time as a string formatted according to the defined pattern.
     */
    public static String getCurrDateTime() {

        String reTime;
        DateFormat df = new SimpleDateFormat(yyyyMMddHHmmss_strip);
        reTime = df.format(new Date());
        return reTime;

    }

    /**
     * 안전하게 문자열을 Date로 변환
     *
     * @param dateStr 변환할 날짜 문자열
     * @return 변환된 Date 객체 또는 null
     */
    public static Date safeStr2Date(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null;
        }

        try {
            SimpleDateFormat sdf = new SimpleDateFormat(yyyyMMdd_bar);
            return sdf.parse(dateStr);
        } catch (ParseException e) {
            return null; // 에러 발생 시 null 반환
        }
    }
}
