package com.devkbil.mtssbj.common.util;

import java.io.File;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class FormatUtil {
    private static final TimeZone timeZone = TimeZone.getTimeZone("GMT+09:00");

    private static final String MARK = "*";

    public static String getNumber(Object obj) {
        return getNumber(obj, 0);
    }

    public static String getNumber(Object input, int decimalPlaces) {
        if (input == null) {
            return "0";
        }
        try {
            String strValue = String.valueOf(input);
            String[] parts = strValue.split("\\.");
            if (parts.length < 2 || decimalPlaces == 0) {
                return String.format("%,d", Long.parseLong(parts[0]));
            } else {
                return String.format("%,d", Long.parseLong(parts[0])) + "." + parts[1].substring(0,
                    Math.min(decimalPlaces, parts[1].length())).replaceAll("0+$", "");
            }
        } catch (NumberFormatException e) {
            return String.valueOf(input);
        } catch (Exception e) {
            return String.valueOf(input);
        }
    }

    public static String getDate(Object source) {
        if (source == null) {
            return "";
        }
        if (source instanceof Date) {
            return getDate((Date) source);
        } else {
            return getDate(String.valueOf(source));
        }
    }

    public static String getDatetime(Object source) {
        if (source == null) {
            return "";
        }
        if (source instanceof Date) {
            return getDateTime((Date) source);
        } else if (source instanceof Long) {
            return getDateTime(new Date((Long) source));
        } else {
            return getDateTime(String.valueOf(source));
        }
    }

    public static String getDatetime(Object source, String glue) {
        if (source == null) {
            return "";
        }
        if (source instanceof Date) {
            return getDateTime((Date) source, glue);
        } else {
            return getDateTime(String.valueOf(source), glue);
        }
    }

    private static String getDate(Date source) {
        return getDate(dateToString(source, "yyyyMMdd"));
    }

    private static String getDate(String source) {
        return getDate(source, "-");
    }

    public static String getDate(Date source, String dateSeparator) {
        return getDate(dateToString(source, "yyyyMMdd"), dateSeparator);
    }

    public static String getDate(String source, String dateSeparator) {
        if (source == null) {
            return null;
        }
        source = source.trim();
        if (source.matches("^[0-9]{8}.*")) {
            return source.replaceAll("^(....)(..)(..).*", "$1" + dateSeparator + "$2" + dateSeparator + "$3");
        } else if (source.matches("^(....).?(..).?(..).*")) {
            return source.replaceAll("^(....).?(..).?(..).*", "$1" + dateSeparator + "$2" + dateSeparator + "$3");
        } else {
            return source;
        }
    }

    public static String getDateTime(Date date) {
        return dateToString(date, "yyyy-MM-dd HH:mm:ss");
    }

    public static String getDateTime(Date date, String dateSeparator) {
        return dateToString(date, "yyyy" + dateSeparator + "MM" + dateSeparator + "dd HH:mm:ss");
    }

    public static String getDateTime(String source) {
        return getDateTime(source, "-");
    }

    public static String getDateTime(String source, String dateSeparator) {
        if (source == null) {
            return null;
        }
        source = source.trim();
        if (source.matches("(....)-(..)-(..) ..:..:...*")) {
            return source.replaceAll("(....)-(..)-(..) (..):(..):(..).*",
                    "$1" + dateSeparator + "$2" + dateSeparator + "$3 $4:$5:$6");
        } else if (source.matches("^(....)(..)(..)$")) {
            return source.replaceAll("^(....)(..)(..)$", "$1" + dateSeparator + "$2" + dateSeparator + "$3");
        } else if (source.matches("^(....)(..)(..)(..)(..)$")) {
            return source.replaceAll("^(....)(..)(..)(..)(..)$",
                    "$1" + dateSeparator + "$2" + dateSeparator + "$3 $4:$5");
        } else if (source.matches("^(....)(..)(..)(..)(..)(..)$")) {
            return source.replaceAll("^(....)(..)(..)(..)(..)(..)$",
                    "$1" + dateSeparator + "$2" + dateSeparator + "$3 $4:$5:$6");
        } else {
            return source;
        }
    }

    public static String getDateMinute(String source) {
        return getDateMinute(source, "-");
    }

    public static String getDateMinute(String source, String dateSeparator) {
        if (source == null) {
            return null;
        }
        source = source.trim();
        if (source.matches("(....).(..).(..) ..:...*")) {
            return source.replaceAll("(....).(..).(..) (..):(..).*",
                    "$1" + dateSeparator + "$2" + dateSeparator + "$3 $4:$5");
        } else if (source.matches("^(....)(..)(..).*")) {
            return source.replaceAll("^(....)(..)(..).*", "$1" + dateSeparator + "$2" + dateSeparator + "$3");
        } else {
            return source;
        }
    }

    public static String getTime(Object source) {
        if (source == null) {
            return "";
        }
        if (source instanceof Date) {
            return getTime((Date) source);
        } else {
            return getTime(String.valueOf(source));
        }
    }

    public static String getTime(Date source) {
        if (source == null) {
            return null;
        }
        return dateToString(source, "HH:mm:ss");
    }

    public static String getTime(String source) {
        if (source == null) {
            return null;
        }
        source = source.trim();
        if (source.matches("^(....).(..).(..) (..):(..):(..)$")) {
            return source.replaceAll("^(....).(..).(..) (..):(..):(..)$", "$4:$5:$6");
        } else if (source.matches("^(..)(..)(..)(...)$")) {
            return source.replaceAll("^(..)(..)(..)(...)$", "$1:$2:$3.$4");
        } else if (source.matches("^(..)(..)(..)$")) {
            return source.replaceAll("^(..)(..)(..)$", "$1:$2:$3");
        } else if (source.matches("^(..)(..)$")) {
            return source.replaceAll("^(..)(..)$", "$1:$2");
        } else {
            return source;
        }
    }

    public static String getMobile(String source) {
        if (source == null) {
            return null;
        } else {
            return source.replaceAll("^(\\d\\d\\d?)-?(\\d\\d\\d\\d?)-?(\\d\\d\\d\\d)$", "$1-$2-$3");
        }
    }

    public static String getBizNo(String source) {
        if (source == null) {
            return null;
        } else {
            return source.replaceAll("^(\\d\\d\\d)(\\d\\d)(\\d\\d\\d\\d\\d)$", "$1-$2-$3");
        }
    }

    public static String getEmailId(String source) {
        if (source == null) {
            return null;
        } else {
            return source.replaceAll("([^@]+)@.*", "$1");
        }
    }

    public static String getEmailServerName(String source) {
        if (source == null) {
            return null;
        } else {
            return source.replaceAll("[^@]+@(.*)", "$1");
        }
    }

    public static String getSecurityBirthday(String source) {
        if (source == null) {
            return null;
        }
        int length = source.length();
        if (length == 8) {
            return source.substring(0, 2) + multiMark(2) + source.substring(length - 4, length);
        } else {
            return getSecurity(source);
        }
    }

    public static String getSecurityMobile(String source) {
        if (source == null) {
            return null;
        }
        source = getMobile(source);
        if (source.length() != 12 && source.length() != 13) {
            return getSecurity(source);
        } else if (source.length() == 12) {
            // 010-123-4567
            return source.substring(0, 4) + multiMark(3) + "-" + source.substring(source.length() - 4);
        } else {
            // 010-1234-5678
            return source.substring(0, 4) + multiMark(4) + "-" + source.substring(source.length() - 4);
        }
    }

    public static String getSecurityEmail(String mail) {
        if (mail == null) {
            return null;
        }
        String[] datas = mail.split("@");
        if (datas.length != 2) {
            return getSecurity(mail);
        }
        String[] bodys = datas[1].split("\\.", 2);
        if (bodys.length != 2) {
            return getSecurity(mail);
        }
        String header = getSecurity(datas[0]);
        return header + "@" + getSecurity(bodys[0]) + "." + bodys[1];
    }

    public static String getSecurity(String source) {
        if (source == null) {
            return null;
        }
        int length = source.length();
        if (length == 2) {
            return source.charAt(0) + MARK;
        }
        int markCnt = 0;
        if (length < 2) {
            markCnt = 0;
        } else if (length == 3) {
            markCnt = 1;
        } else if (length < 6) {
            markCnt = 2;
        } else if (length > 5) {
            markCnt = (int) (length / 2.3);
        }
        int prefixCnt = (length - markCnt);
        if (prefixCnt > 1) {
            prefixCnt = prefixCnt / 2;
        }
        int suffixCnt = length - markCnt - prefixCnt;
        String result = source.substring(0, prefixCnt) + multiMark(markCnt);
        result += source.substring(length - suffixCnt, length);
        return result;
    }

    private static String multiMark(int loop) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < loop; i++) {
            sb.append(MARK);
        }
        return sb.toString();
    }

    public static String getFileSize(long fileSizeByte) {
        DecimalFormat oneDecimal = new DecimalFormat("0.0");
        long absNumber = Math.abs(fileSizeByte);
        double result = fileSizeByte;
        String suffix = "";
        if (absNumber < 1024) {
            oneDecimal = new DecimalFormat("0");
            suffix = "Byte";
        } else if (absNumber < 1024 * 1024) {
            result = fileSizeByte / 1024.0;
            suffix = "KB";
        } else if (absNumber < 1024 * 1024 * 1024) {
            result = fileSizeByte / (1024.0 * 1024);
            suffix = "MB";
        } else {
            result = fileSizeByte / (1024.0 * 1024 * 1024);
            suffix = "GB";
        }
        return oneDecimal.format(result) + suffix;
    }

    public static String lpad(Object src, String padChar, int targetLength) {
        if (src == null) {
            return null;
        }
        String sourceStr = String.valueOf(src).trim();
        StringBuilder result = new StringBuilder(sourceStr);
        while (result.length() < targetLength) {
            result.insert(0, padChar);
        }
        return result.toString();
    }

    /**
     * 개행문자를 Br태그로 변경
     *
     * @param source
     * @return
     */
    public static String returnToBr(String source) {
        if (source == null) {
            return null;
        }
        return source.replaceAll("\r\n", "<br/>").replaceAll("\r", "<br/>").replaceAll("\n", "<br/>");
    }

    public static String spaceToNbsp(String source) {
        if (source == null) {
            return null;
        }
        return source.replaceAll("^ ", "&nbsp;").replaceAll("\n ", "\n&nbsp;");
    }

    public static boolean isTel(String source) {
        if (source == null) {
            return false;
        }
        return source.matches("^(\\d\\d\\d?)-?(\\d\\d\\d\\d?)-?(\\d\\d\\d\\d)$");
    }

    public static String concat(String str1, String str2) {
        return str1 + str2;
    }

    public static String getFilename(String filepath) {
        if (filepath == null) {
            return "";
        }
        int lastSeparatorIndex = filepath.lastIndexOf(File.pathSeparator);
        if (lastSeparatorIndex >= 0) {
            filepath = filepath.substring(lastSeparatorIndex + 1);
        }
        int lastDotIndex = filepath.lastIndexOf(".");
        if (lastDotIndex >= 0) {
            filepath = filepath.substring(0, lastDotIndex);
        }
        return filepath;
    }

    public static String getDayName(Object date) {
        String strDate = getDate(date);
        if (strDate == null || strDate.length() != 10) {
            return "";
        }
        Calendar calendar = getCalendar(stringToDate(strDate, "yyyy-MM-dd"));
        int curDay = calendar.get(Calendar.DAY_OF_WEEK);
        String[] days = {"", "일", "월", "화", "수", "목", "금", "토"};
        return days[curDay];
    }

    public static Date stringToDate(String dateString, String format) {
        SimpleDateFormat simpleFormat = new SimpleDateFormat(format);
        simpleFormat.setTimeZone(timeZone);
        try {
            return simpleFormat.parse(dateString);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public static Calendar getCalendar(Date date) {
        Calendar cal = Calendar.getInstance(timeZone, Locale.KOREAN);
        cal.setTime(date);
        return cal;
    }

    public static String dateToString(Date date, String format) {
        SimpleDateFormat simpleFormat = new SimpleDateFormat(format);
        simpleFormat.setTimeZone(timeZone);
        return simpleFormat.format(date.getTime());
    }

    public static String dateToString(Calendar date, String format) {
        SimpleDateFormat simpleFormat = new SimpleDateFormat(format);
        simpleFormat.setTimeZone(timeZone);
        return simpleFormat.format(date.getTime());
    }

    public static String convertNumberToHangul(String number) {
        String[] han1 = {"", "일", "이", "삼", "사", "오", "육", "칠", "팔", "구"};
        String[] han2 = {"", "십", "백", "천"};
        String[] han3 = {"", "만", "억", "조", "경"};
        String result = "";
        int len = number.length();
        int nowInt = 0;
        boolean hasHan3 = false;
        for (int i = len; i > 0; i--) {
            nowInt = Integer.parseInt(number.substring(len - i, len - i + 1));
            int han2Pick = (i - 1) % 4;
            if (nowInt > 0) {
                result += (han1[nowInt]) + (han2[han2Pick]);
                if (han2Pick > 0) {
                    hasHan3 = false;
                }
            }
            if (!hasHan3 && han2Pick == 0) {
                result += (han3[(i - 1) / 4]);
                hasHan3 = true;
            }
        }
        return result;
    }

    public static String doubleToString(Double src, int scale) {
        String pattern = "#";
        if (scale > 0) {
            pattern += ".";
            for (int i = 0; i < scale; i++) {
                pattern += "#";
            }
        }
        return new DecimalFormat(pattern).format(src);
    }

    public static String getElaspedTimeForMinute(Integer seconds) {
        int hour = seconds / 3600;
        int minute = (seconds % 3600) / 60;
        String result = "";
        if (hour > 0) {
            result += hour + "시간 ";
        }
        result += minute + "분";
        return result;
    }

}
