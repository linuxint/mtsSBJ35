package com.devkbil.mtssbj.common.util;

import com.devkbil.mtssbj.schedule.DateVO;
import com.devkbil.mtssbj.schedule.MonthVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.util.StringUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

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
    private static final String[] DAY_ARR = new String[]{"일", "월", "화", "수", "목", "금", "토"};

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
     * 시스템의 오늘 일자 반.
     */
    public static Date getToday() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        return cal.getTime();
    }

    /**
     * 문자열을 날짜형으로 변환.
     */
    public static Date stringToDate(String date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(str2Date(date));
        return cal.getTime();
    }

    /**
     * 날짜를 문자열로 변환.
     */
    public static String date2Str(Date date) {
        SimpleDateFormat ft = new SimpleDateFormat(yyyyMMdd_bar);
        return ft.format(date.getTime());
    }

    /**
     * 문자열을 날짜(yyyy-MM-dd)로 변환.
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
     */
    public static DateVO date2VO(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        DateVO dvo = new DateVO();
        dvo.setYear(cal.get(Calendar.YEAR));
        dvo.setMonth(cal.get(Calendar.MONTH) + 1);
        dvo.setDay(cal.get(Calendar.DAY_OF_MONTH));
        dvo.setWeek(DAY_ARR[cal.get(Calendar.DAY_OF_WEEK) - 1]);
        return dvo;
    }

    /**
     * 년도 추출.
     */
    public static Integer getYear(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        return cal.get(Calendar.YEAR);
    }

    /**
     * 월 추출.
     */
    public static Integer getMonth(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        return cal.get(Calendar.MONTH) + 1;
    }

    /**
     * 한 주의 순서 (요일).
     * 예: 일요일 = 0
     */
    public static Integer getDayOfWeek(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.DAY_OF_WEEK) - 1;
    }

    /**
     * 월의 몇 번째 주 인지 추출.
     * 예: 반환값이 4이면 (7월) 4번째 주
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
     */
    public static Integer dateDiff(Date date1, Date date2) {
        String dt1 = date2Str(date1);
        String dt2 = date2Str(date2);

        Integer day = (int) ((str2Date(dt1).getTime() - str2Date(dt2).getTime()) / (24 * 60 * 60 * 1000));
        return day;
    }

    /**
     * 날자 계산: -1은 감소.
     */
    public static Date dateAdd(Date date, Integer calDay) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, calDay);

        return cal.getTime();
    }

    /**
     * 달 계산: -1은 감소.
     */
    public static Date monthAdd(Date date, Integer month) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MONTH, month);

        return cal.getTime();
    }

    public static Date addDate(Date date, Date date1) {
        if (evaluationOver()) {
            return new Date(0L);
        }
        long l = date.getTime();
        long l1 = date1.getTime();
        long l2 = l + l1;
        return new Date(l2);
    }

    public static Date addDay(Date date, long l) {
        if (evaluationOver()) {
            return new Date(0L);
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(5, (int) l);
        Date date1 = calendar.getTime();
        return date1;
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

    public static String formatDate(Date date, String s) {
        if (evaluationOver()) {
            return MSG_ALERT;
        }
        return DateFormatUtils.format(date, s);
    }

    public static Date getGMTDate() {
        if (evaluationOver()) {
            return new Date(0L);
        }
        Date date = new Date();
        long l = date.getTime();
        long l1 = (long) getServerGMTOffset() * 60 * 1000;
        Date date1 = new Date(l - l1);
        return date1;
    }

    public static int getGMTHour() {
        if (evaluationOver()) {
            return 0;
        }
        Date date = getGMTDate();
        int i = Integer.parseInt(formatDate(date, "H"));
        return i;
    }

    public static long getGMTOffset(Date date) {
        if (evaluationOver()) {
            return 0L;
        }
        Date date1 = getGMTDate();
        long l = date1.getTime();
        long l1 = date.getTime();
        double d = l1 - l;
        double d1 = d / 1000.0D;
        double d2 = Math.round(d1) / 60L;
        return Math.round(d2);
    }

    public static String getMonthName(int argI) throws Exception {
        int i = argI;
        if (evaluationOver()) {
            return MSG_ALERT;
        }
        i--;
        if ((i >= 0) && (i <= 11)) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(5, 1);
            calendar.set(2, i);
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
        int i = calendar.get(0);
        int j = calendar.get(1);
        int k = calendar.get(2);
        int l = calendar.get(5);
        int i1 = calendar.get(7);
        int j1 = calendar.get(14);
        int k1 = Calendar.getInstance().getTimeZone().getOffset(i, j, k, l, i1, j1);
        return k1 / 1000 / 60;
    }

    /**
     * [오퍼레이션명] getHour<br />
     * [요약] 현재 시간을 가져온다.<br />
     *
     * @return<br />
     */

    public static int getHour() {

        Calendar cal = Calendar.getInstance(Locale.getDefault());
        return cal.get(Calendar.HOUR_OF_DAY);

    }

    /**
     * [오퍼레이션명] getMinute<br />
     * [요약] 현재 분을 가져온다.<br />
     *
     * @return<br />
     */

    public static int getMinute() {

        Calendar cal = Calendar.getInstance(Locale.getDefault());
        return cal.get(Calendar.MINUTE);

    }

    /**
     * [오퍼레이션명] getYear<br />
     * [요약] 현재 년도를 가져온다.<br />
     *
     * @return<br />
     */

    public static int getYear() {

        Calendar cal = Calendar.getInstance(Locale.getDefault());
        return cal.get(Calendar.YEAR);

    }

    /**
     * [오퍼레이션명] getMonth<br />
     * [요약] 현재 월을 가져온다.<br />
     *
     * @return<br />
     */

    public static int getMonth() {

        Calendar cal = Calendar.getInstance(Locale.getDefault());
        return cal.get(Calendar.MONTH);

    }

    /**
     * [오퍼레이션명] getDate<br />
     * [요약] 현재 일을 가져온다.<br />
     *
     * @return<br />
     */

    public static int getDate() {

        Calendar cal = Calendar.getInstance(Locale.getDefault());
        return cal.get(Calendar.DATE);

    }

    /**
     * 전달받은 날짜(Date)를 지정된 Format의 날짜 표현형식으로 돌려준다. <BR>
     * <BR>
     * 사용예) getToday("yyyy/MM/dd hh:mm a")<BR>
     * 결 과 ) 2001/12/07 10:10 오후<BR>
     * <BR>
     * <p>
     * Format은 J2SE의 SimpleDateFormat의 Documentation을 참고한다.
     *
     * @param pOutformat String
     * @return java.lang.String
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
     * <BR>
     * 사용예) getToday("yyyy/MM/dd hh:mm a")<BR>
     * 결 과 ) 2001/12/07 10:10 오후<BR>
     * <BR>
     * Format은 J2SE의 SimpleDateFormat의 Documentation을 참고한다.
     *
     * @param pOutformat String
     * @return java.lang.String
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
     * <BR>
     * 사용예) getDateFormat("20101121","-")<BR>
     * 결 과 ) 2010-11-21<BR>
     * <BR>
     *
     * @param type String
     * @return java.lang.String
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
     * <BR>
     * 사용예) getDateFormat("20101121","-")<BR>
     * 결 과 ) 2010-11-21<BR>
     * <BR>
     * 전달받은 날짜가 00000000 으로 내려오면 공백리턴함 (윤진욱)
     *
     * @param type String
     * @return java.lang.String
     */

    public static String getDateFormat2(String date, String type) {

        String rDateString;

        if ("00000000".equals(date) || "0".equals(date) || "".equals(date)) {// 00000000
            // 날짜이면
            // 공백리턴함
            rDateString = "";
        } else {
            rDateString = date.substring(0, 4) + type + date.substring(4, 6) + type + date.substring(6);
        }
        return rDateString;

    }

    /**
     * 전달받은 날짜(Date)를 지정된 Format의 날짜 표현형식으로 돌려준다. <BR>
     * <BR>
     *
     * @param type String
     * @return java.lang.String
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
     * <BR>
     * Parameter는 입력일, 입력일 Format, 출력일 Format, 일단위 더하기, 시단위 더하기,
     * 분단위 더하기이다.
     * 간단한 사용예는 다음과 같다.
     * 사용예) LLog.debug.println(
     * getFormattedDateAdd("200201010605","yyyyMMddhhmm"
     * ,"yyyy/MM/dd HH:mm",-100,10,-11) );
     * 결과) 2001/09/23 15:54
     * Format은 J2SE의 SimpleDateFormat의 Documentation을 참고한다.
     *
     * @param pIndate    String
     * @param pInformat  String
     * @param pOutformat String
     * @param pDay       int
     * @param pHour      int
     * @param pMin       int
     * @return java.lang.String
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
     * 입력받은 날짜에 월 단위의 값을 더하여 출력Format에 따라 값을 넘겨준다. <BR>
     * <BR>
     * Parameter는 입력일, 입력일 Format, 출력일 Format, 일단위 더하기, 시단위 더하기,
     * 분단위 더하기이다.
     * 간단한 사용예는 다음과 같다.
     * 사용예) LLog.debug.println(
     * getFormattedDateAdd("200201010605","yyyyMMddhhmm","yyyy/MM/dd HH:mm",-6)
     * );
     * 결과) 2001/09/23 15:54
     * Format은 J2SE의 SimpleDateFormat의 Documentation을 참고한다.
     *
     * @param pIndate    String
     * @param pInformat  String
     * @param pOutformat String
     * @param pYear      int
     * @return java.lang.String
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
     * <BR>
     * Parameter는 입력일, 입력일 Format, 출력일 Format, 일단위 더하기, 시단위 더하기,
     * 분단위 더하기이다.
     * 간단한 사용예는 다음과 같다.
     * 사용예) LLog.debug.println(
     * getFormattedDateAdd("200201010605","yyyyMMddhhmm","yyyy/MM/dd HH:mm",-6)
     * );
     * 결과) 2001/09/23 15:54
     * Format은 J2SE의 SimpleDateFormat의 Documentation을 참고한다.
     *
     * @param pIndate    String
     * @param pInformat
     * @param pOutformat String
     * @param pMonth     int
     * @return java.lang.String
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
     * 입력받은 날짜에 월 단위의 값을 더하여 출력Format에 따라 값을 넘겨준다. <BR>
     * <BR>
     * Parameter는 입력일, 입력일 Format, 출력일 Format, 일단위 더하기, 시단위 더하기,
     * 분단위 더하기이다.
     * 간단한 사용예는 다음과 같다.
     * 사용예) LLog.debug.println(
     * getFormattedDateAdd("200201010605","yyyyMMddhhmm","yyyy/MM/dd HH:mm",-6)
     * );
     * 결과) 2001/09/23 15:54
     * Format은 J2SE의 SimpleDateFormat의 Documentation을 참고한다.
     *
     * @param pIndate    String
     * @param pInformat  String
     * @param pOutformat String
     * @param pHour      int
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
     * @param amount
     * @return
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
     * amount 차이만큼 날짜를 반환한다.
     *
     * @param basicDate
     * @param amount
     * @return
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
     * amount 차이만큼 날짜를 반환한다.
     *
     * @param amount
     * @return
     */

    public static String getAddHour(int amount) {

        Calendar cal = Calendar.getInstance(Locale.getDefault());
        cal.add(Calendar.HOUR, amount);

        SimpleDateFormat dateprint = new SimpleDateFormat(yyyyMMddHHmmss_bar);

        return dateprint.format(cal.getTime());

    }

    /**
     * amount 차이만큼 날짜를 반환한다.
     *
     * @param amount
     * @return
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
     * yyyymm
     *
     * @param yearMonth
     * @return
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
     * 입력받은 날짜 시간 yyyy-MM-dd HH:mm:ss 형식으로 반한
     * date는 format과 날짜형식이 같아야 한다.
     * </pre>
     *
     * @param date   EX)2011-11-11 11:11:11
     * @param format EX) "yyyy-MM-dd HH:mm:ss"
     * @return "yyyy-MM-dd HH:mm:ss"
     * @throws ParseException
     */

    public static String getReqDate(String date, String format) {

        return getCurrentDate(date, format, yyyyMMddHHmmss_bar);

    }

    /**
     * "yyyy-MM-dd HH:mm:SS"형의 날짜를 반환한다.
     *
     * @return yyyy-MM-dd HH:mm:SS 형의 날짜
     */

    public static String getCurrentDate() {

        return getCurrentDate(yyyyMMddHHmmss_bar);

    }

    /**
     * [오퍼레이션명] getCurrentDate<br />
     * [요약] 날자 반환<br />
     *
     * @param s
     * @return<br />
     */

    public static String getCurrentDate(String s) {

        SimpleDateFormat simpledateformat = new SimpleDateFormat(s, Locale.KOREA);

        return simpledateformat.format(new Date());

    }

    /**
     * [오퍼레이션명] getCurrentDate<br />
     * [요약] 날자 반환<br />
     *
     * @param date
     * @param format
     * @param s
     * @return<br />
     */

    public static String getCurrentDate(String date, String format, String s) {

        SimpleDateFormat simpledateformat = new SimpleDateFormat(s, Locale.KOREA);
        SimpleDateFormat simpledateformat2 = new SimpleDateFormat(format, Locale.KOREA);

        try {
            return simpledateformat.format(simpledateformat2.parse(date));
        } catch (ParseException e) {
            return "";
        }
    }

    /**
     * [오퍼레이션명] getCurrentDate<br />
     * [요약] 날자 반환<br />
     *
     * @param date
     * @param s
     * @return<br />
     */

    public static String getCurrentDate(Date date, String s) {

        SimpleDateFormat simpledateformat = new SimpleDateFormat(s, Locale.KOREA);

        return simpledateformat.format(date);

    }

    /**
     * [오퍼레이션명] dayOfWeek<br />
     * [요약] 요일 수 변환<br />
     *
     * @param date
     * @return<br />
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
     * [오퍼레이션명] getDateDiff<br />
     * [요약] 두 날자의 차이<br />
     *
     * @param dataPart
     * @param pIndate1
     * @return<br />
     */

    public static long getDateDiff(String dataPart, String pIndate1) {

        String pInformat1 = yyyyMMddHHmmss_bar;
        String pInformat2 = yyyyMMddHHmmss_bar;

        return getDateDiff(dataPart, pIndate1, pInformat1, getCurrentDate(), pInformat2);

    }

    /**
     * [오퍼레이션명] getDateDiff<br />
     * [요약] 두 날자의 차이<br />
     *
     * @param dataPart
     * @param pIndate1
     * @param pIndate2
     * @return<br />
     */

    public static long getDateDiff(String dataPart, String pIndate1, String pIndate2) {

        String pInformat1 = yyyyMMddHHmmss_bar;
        String pInformat2 = yyyyMMddHHmmss_bar;

        return getDateDiff(dataPart, pIndate1, pInformat1, pIndate2, pInformat2);

    }

    /**
     * <pre>
     * *
     * getDataDiff : 년,월,일,시,분,초 로 지난 시간을 (long)로 리턴
     * dataPart(y : 년, m : 월, d : 일, h : 시, n : 분, s : 초)
     *
     * pIndate1 EX)2011-01-01 01:01:01
     * pInformat1 EX)yyyyMMdd hh:mm:ss
     * pIndate2 EX)2011-12-31 01:01:01
     * pInformat2 EX)yyyyMMdd hh:mm:ss
     * pIndate1, pIndate2 값이 "now" 경우 현재 날짜가 들어간다.
     * </pre>
     *
     * @param argDataPart
     * @param argPindate1
     * @param argPinformat1
     * @param argPindate2
     * @param argPinformat2
     * @return
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
     * <pre>
     * getDataDiff : 년,월,일,시,분,초 로 지난 시간을 (long)로 리턴
     * dataPart(y : 년, m : 월, d : 일, h : 시, n : 분, s : 초)
     * </pre>
     *
     * @param dataPart
     * @param pIndate1
     * @param pIndate2
     * @return
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
     * [오퍼레이션명] getYearWeek<br />
     * [요약] 해당 년의 몇번째 주인지<br />
     *
     * @param thisday
     * @return<br />
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
     * [오퍼레이션명] diffOfDate<br />
     * [요약] 시작일, 마지막 일에서 뺀 차이<br />
     *
     * @param begin
     * @param end
     * @return
     * @throws Exception <br />
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
     * [오퍼레이션명] diffOfHour<br />
     * [요약] 시작일, 마지막 일에서 뺀 차이<br />
     *
     * @param begin
     * @param end
     * @return
     * @throws Exception <br />
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
     * [오퍼레이션명] diffOfSecond<br />
     * [요약] 시작일, 마지막 일에서 뺀 차이<br />
     *
     * @param begin
     * @param end
     * @return
     * @throws Exception <br />
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
     * <PRE>
     * 1. MethodName    :    firstDateOfWeek
     * 2. Comment        :    해당일의 첫번째 날 리턴
     * 3. 작성자            :    retriver
     * 4. 작성일            :    2012. 4. 20.    오후 5:33:31
     *
     * </PRE>
     *
     * @param dtPrevDate
     * @return
     * @throws Exception
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
     * 현재 시스템일시를 일자 포맷 형태로 생성한다.<br />
     *
     * @param parsePatterns 일자 포맷 패턴
     * @return 현재 시스템일시를 일자 포맷 형태의 String 타입으로 리턴한다.
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
     * [오퍼레이션명] getCurrDate<br />
     * <p>
     * [요약] 현재 시간을 가져오기 년도,월,일<br />
     *
     * @return<br />
     */

    public static int getCurrDate() {

        DateFormat df = new SimpleDateFormat(yyyyMMdd_dot);
        String date = df.format(new Date());
        return Integer.parseInt(date.substring(0, 4) + date.substring(5, 7) + date.substring(8, 10));

    }

    /**
     * [오퍼레이션명] getCurrTime<br />
     * <p>
     * [요약] 현재 시간 가져오기<br />
     *
     * @return<br />
     */

    public static int getCurrTime() {

        DateFormat df = DateFormat.getTimeInstance(DateFormat.MEDIUM, Locale.UK);
        String time = df.format(new Date());
        return Integer.parseInt(time.substring(0, 2) + time.substring(3, 5) + time.substring(6, 8));

    }

    /**
     * [오퍼레이션명] getMilSecTime<br />
     * <p>
     * [요약] 현재 시간을 yyyyMMddHHmmss 리턴한다.<br />
     *
     * @return<br />
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

    /**
     * 날짜에 월 추가
     *
     * @param date   원본 날짜
     * @param months 추가할 월 (음수 값으로 전달하면 월을 뺌)
     * @return 결과 날짜
     */
    public static Date dateAddMonth(Date date, int months) {
        if (date == null) {
            return null;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, months);
        return calendar.getTime();
    }

}
