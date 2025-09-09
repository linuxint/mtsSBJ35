package com.devkbil.common.util;

import java.io.File;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * 다양한 데이터 형식을 변환하고 포맷팅하는 유틸리티 클래스입니다.
 * 이 클래스는 날짜, 시간, 숫자, 전화번호, 이메일 등 다양한 형식의 데이터를 처리하는 정적 메서드를 제공합니다.
 * 주요 기능:
 * - 날짜/시간 형식 변환
 * - 숫자 포맷팅
 * - 개인정보 마스킹
 * - 파일 크기 변환
 * - 문자열 패딩
 * - 한글 숫자 변환
 */
public class FormatUtil {
    private static final TimeZone timeZone = TimeZone.getTimeZone("GMT+09:00");

    private static final String MARK = "*";

    /**
     * 숫자를 포맷팅된 문자열로 변환합니다.
     * 천 단위 구분자(,)를 포함하여 반환합니다.
     *
     * @param obj 변환할 숫자 객체
     * @return 포맷팅된 숫자 문자열
     */
    public static String getNumber(Object obj) {
        return getNumber(obj, 0);
    }

    /**
     * 숫자를 지정된 소수점 자릿수를 가진 포맷팅된 문자열로 변환합니다.
     * 천 단위 구분자(,)를 포함하여 반환하며, 소수점 이하 불필요한 0은 제거됩니다.
     *
     * @param input         변환할 숫자 객체
     * @param decimalPlaces 표시할 소수점 자릿수
     * @return 포맷팅된 숫자 문자열
     */
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

    /**
     * 날짜 객체를 'yyyy-MM-dd' 형식의 문자열로 변환합니다.
     * Date 객체나 문자열을 입력받아 처리할 수 있습니다.
     *
     * @param source 변환할 날짜 (Date 객체 또는 문자열)
     * @return 포맷팅된 날짜 문자열, null인 경우 빈 문자열 반환
     */
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

    /**
     * 날짜/시간 객체를 'yyyy-MM-dd HH:mm:ss' 형식의 문자열로 변환합니다.
     * Date 객체, Long 타입의 timestamp, 또는 문자열을 입력받아 처리할 수 있습니다.
     *
     * @param source 변환할 날짜/시간 (Date, Long, 또는 문자열)
     * @return 포맷팅된 날짜/시간 문자열, null인 경우 빈 문자열 반환
     */
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

    /**
     * 날짜/시간 객체를 사용자 지정 구분자를 사용하여 포맷팅된 문자열로 변환합니다.
     * 구분자는 날짜 부분의 구분 문자로 사용됩니다. (예: '/' -> yyyy/MM/dd HH:mm:ss)
     *
     * @param source 변환할 날짜/시간 (Date 객체 또는 문자열)
     * @param glue 날짜 구분자
     * @return 포맷팅된 날짜/시간 문자열, null인 경우 빈 문자열 반환
     */
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

    /**
     * Date 객체를 "yyyyMMdd" 형식의 문자열로 변환합니다.
     *
     * @param source 변환할 Date 객체
     * @return "yyyyMMdd" 형식의 날짜 문자열
     */
    private static String getDate(Date source) {
        return getDate(dateToString(source, "yyyyMMdd"));
    }

    /**
     * 입력된 날짜 문자열을 기본 구분자를 사용하여 포맷팅된 날짜 문자열로 변환합니다.
     *
     * @param source 포맷팅할 날짜 문자열
     * @return 기본 구분자를 포함한 포맷팅된 날짜 문자열
     */
    private static String getDate(String source) {
        return getDate(source, "-");
    }

    /**
     * Date 객체를 지정된 구분자를 사용하여 포맷팅된 날짜 문자열로 변환합니다.
     *
     * @param source 변환할 Date 객체
     * @param dateSeparator 년, 월, 일 사이에 사용할 구분자
     * @return 지정된 구분자를 포함한 포맷팅된 날짜 문자열
     */
    public static String getDate(Date source, String dateSeparator) {
        return getDate(dateToString(source, "yyyyMMdd"), dateSeparator);
    }

    /**
     * 날짜 문자열을 지정된 구분자를 사용하여 포맷팅된 날짜로 변환합니다.
     * 입력 문자열이 특정 날짜 패턴과 일치하는 경우 처리하며,
     * 년, 월, 일 구성 요소 사이에 제공된 구분자를 삽입합니다.
     *
     * @param source 포맷팅할 날짜를 나타내는 입력 문자열.
     *               문자열은 다양한 형식의 날짜를 포함할 수 있습니다.
     *               입력이 null인 경우 null을 반환합니다.
     * @param dateSeparator 포맷팅된 날짜 문자열에서 년, 월, 일 구성 요소 사이에
     *                      삽입될 구분자
     * @return 지정된 구분자를 포함한 포맷팅된 날짜 문자열.
     *         예상 날짜 패턴과 일치하지 않는 경우 원본 문자열 반환
     */
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

    /**
     * Date 객체를 포맷팅된 날짜-시간 문자열로 변환합니다.
     *
     * @param date 문자열로 변환할 Date 객체
     * @return "yyyy-MM-dd HH:mm:ss" 형식의 날짜-시간 문자열
     */
    public static String getDateTime(Date date) {
        return dateToString(date, "yyyy-MM-dd HH:mm:ss");
    }

    /**
     * Date 객체를 지정된 날짜 구분자를 사용하여 포맷팅된 날짜-시간 문자열로 변환합니다.
     *
     * @param date 포맷팅할 Date 객체
     * @param dateSeparator 날짜 형식에서 사용할 구분자
     * @return "yyyy{구분자}MM{구분자}dd HH:mm:ss" 형식의 날짜-시간 문자열
     */
    public static String getDateTime(Date date, String dateSeparator) {
        return dateToString(date, "yyyy" + dateSeparator + "MM" + dateSeparator + "dd HH:mm:ss");
    }

    /**
     * Retrieves the formatted date and time based on the source input.
     *
     * @param source the source string that provides the base information for date and time.
     * @return a formatted date and time string using the default delimiter.
     */
    public static String getDateTime(String source) {
        return getDateTime(source, "-");
    }

    /**
     * 입력 문자열과 지정된 날짜 구분자를 기반으로 날짜-시간 문자열을 포맷팅합니다.
     * 입력 문자열을 분석하고 변환을 적용하여 지정된 날짜 구분자를 사용하는
     * 포맷팅된 날짜 또는 날짜-시간 문자열로 변환합니다.
     *
     * @param source 날짜 또는 날짜-시간을 나타내는 입력 문자열로, 다양한 형식일 수 있습니다.
     *               입력이 null인 경우 null을 반환합니다.
     * @param dateSeparator 날짜 구성 요소(년, 월, 일)를 구분하는 데 사용되는 문자열
     * @return 지정된 구분자를 사용하여 포맷팅된 날짜 또는 날짜-시간 문자열.
     *         예상 패턴과 일치하지 않는 경우 원본 문자열을 반환합니다.
     *         소스 문자열이 null인 경우 null을 반환합니다.
     */
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

    /**
     * Extracts and returns the minute portion of the date from the given source string,
     * using a default delimiter.
     *
     * @param source the input string containing the date information
     * @return the minute portion of the date as a string
     */
    public static String getDateMinute(String source) {
        return getDateMinute(source, "-");
    }

    /**
     * Formats a date string by adding a specified date separator at appropriate positions,
     * and optionally includes the time up to the minute if present in the input.
     *
     * @param source the input date string to be formatted, which can include date and time
     * @param dateSeparator the string to use as the separator between year, month, and day
     * @return the formatted date string with the specified date separator, and with optional
     *         time up to the minute if present; returns null if the input source is null
     */
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

    /**
     * 시간 객체를 'HH:mm:ss' 형식의 문자열로 변환합니다.
     * Date 객체나 문자열을 입력받아 처리할 수 있습니다.
     *
     * @param source 변환할 시간 (Date 객체 또는 문자열)
     * @return 포맷팅된 시간 문자열, null인 경우 빈 문자열 반환
     */
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

    /**
     * Date 객체를 'HH:mm:ss' 형식의 시간 문자열로 변환합니다.
     *
     * @param source 변환할 Date 객체
     * @return 포맷팅된 시간 문자열, null인 경우 null 반환
     */
    public static String getTime(Date source) {
        if (source == null) {
            return null;
        }
        return dateToString(source, "HH:mm:ss");
    }

    /**
     * 시간 문자열을 'HH:mm:ss' 형식으로 변환합니다.
     * 다양한 형식의 시간 문자열을 처리할 수 있습니다:
     * - 'HHmmss'
     * - 'HHmm'
     * - 'HH:mm:ss'
     * - 'HH:mm'
     *
     * @param source 변환할 시간 문자열
     * @return 포맷팅된 시간 문자열, null인 경우 null 반환
     */
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

    /**
     * 문자열을 표준 휴대폰 번호 형식으로 포맷팅합니다.
     * 숫자로 구성된 문자열을 하이픈으로 구분된 그룹으로 변환합니다
     * (예: "123-456-7890").
     * 입력 문자열이 예상 패턴과 일치하지 않거나 null인 경우
     * null을 반환합니다.
     *
     * @param source 잠재적 휴대폰 번호를 포함하는 입력 문자열
     * @return 하이픈이 포함된 포맷팅된 휴대폰 번호, 입력이 null인 경우 null 반환
     */
    public static String getMobile(String source) {
        if (source == null) {
            return null;
        } else {
            return source.replaceAll("^(\\d\\d\\d?)-?(\\d\\d\\d\\d?)-?(\\d\\d\\d\\d)$", "$1-$2-$3");
        }
    }

    /**
     * 사업자 등록번호 문자열을 XXX-XX-XXXXX 형식으로 포맷팅합니다.
     *
     * @param source 사업자 등록번호를 나타내는 입력 문자열로, 구분자 없이
     *               XXXXXXXXXX 형식의 숫자로 구성되어야 합니다.
     * @return XXX-XX-XXXXX 형식의 포맷팅된 사업자 등록번호, 입력이 null인 경우 null 반환
     */
    public static String getBizNo(String source) {
        if (source == null) {
            return null;
        } else {
            return source.replaceAll("^(\\d\\d\\d)(\\d\\d)(\\d\\d\\d\\d\\d)$", "$1-$2-$3");
        }
    }

    /**
     * 주어진 이메일 주소 문자열에서 이메일 식별자('@' 기호 이전 부분)를 추출합니다.
     *
     * @param source 식별자를 추출할 이메일 주소 문자열.
     *               null인 경우 메서드는 null을 반환합니다.
     * @return 이메일 식별자('@' 기호 이전 부분), 입력이 null인 경우 null 반환
     */
    public static String getEmailId(String source) {
        if (source == null) {
            return null;
        } else {
            return source.replaceAll("([^@]+)@.*", "$1");
        }
    }

    /**
     * 제공된 이메일 주소에서 이메일 서버 이름을 추출합니다.
     *
     * @param source 서버 이름을 추출할 이메일 주소.
     *               null인 경우 메서드는 null을 반환합니다.
     * @return 이메일 주소에서 추출한 서버 이름, 입력이 null인 경우 null 반환
     */
    public static String getEmailServerName(String source) {
        if (source == null) {
            return null;
        } else {
            return source.replaceAll("[^@]+@(.*)", "$1");
        }
    }

    /**
     * 생년월일 문자열을 마스킹 처리합니다.
     * 8자리 생년월일의 경우 가운데 2자리를 '*'로 마스킹합니다.
     * (예: '19901225' -> '19**1225')
     *
     * @param source 마스킹할 생년월일 문자열
     * @return 마스킹된 생년월일 문자열, null인 경우 null 반환
     */
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

    /**
     * 휴대폰 번호를 마스킹 처리합니다.
     * 가운데 숫자들을 '*'로 마스킹하고 하이픈(-)으로 구분합니다.
     * - 10자리: 010-***-1234 형식
     * - 11자리: 010-****-1234 형식
     *
     * @param source 마스킹할 휴대폰 번호
     * @return 마스킹된 휴대폰 번호, null인 경우 null 반환
     */
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

    /**
     * 이메일 주소를 마스킹 처리합니다.
     * 로컬파트(@앞부분)와 도메인의 서브도메인을 부분적으로 '*'로 마스킹합니다.
     * (예: 'user@example.com' -> 'u**r@e*****e.com')
     *
     * @param mail 마스킹할 이메일 주소
     * @return 마스킹된 이메일 주소, null인 경우 null 반환
     */
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

    /**
     * 문자열을 일반적인 규칙에 따라 마스킹 처리합니다.
     * 문자열 길이에 따라 적절한 수의 문자를 '*'로 대체합니다.
     * - 2자: 마지막 1자 마스킹
     * - 3자: 가운데 1자 마스킹
     * - 4-5자: 가운데 2자 마스킹
     * - 6자 이상: 전체 길이의 약 43%를 마스킹
     *
     * @param source 마스킹할 문자열
     * @return 마스킹된 문자열, null인 경우 null 반환
     */
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

    /**
     * 지정된 횟수만큼 반복된 MARK 값으로 구성된 문자열을 생성합니다.
     *
     * @param loop MARK 값을 반복할 횟수
     * @return 지정된 횟수만큼 MARK 값이 반복된 문자열
     */
    private static String multiMark(int loop) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < loop; i++) {
            sb.append(MARK);
        }
        return sb.toString();
    }

    /**
     * 파일 크기를 사람이 읽기 쉬운 형식으로 변환합니다.
     * 단위는 자동으로 Byte, KB, MB, GB 중에서 선택됩니다.
     * 1024 단위로 변환되며, 소수점 한 자리까지 표시됩니다.
     * (예: '1.5MB', '800KB', '2.0GB')
     *
     * @param fileSizeByte 변환할 파일 크기 (바이트 단위)
     * @return 포맷팅된 파일 크기 문자열
     */
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

    /**
     * 문자열의 왼쪽에 지정된 문자를 채워 원하는 길이로 만듭니다.
     * 원본 문자열이 목표 길이보다 길면 원본을 그대로 반환합니다.
     *
     * @param src 원본 문자열 객체
     * @param padChar 채울 문자
     * @param targetLength 목표 길이
     * @return 패딩된 문자열, src가 null인 경우 null 반환
     */
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
     * 텍스트의 개행 문자를 HTML 줄바꿈 태그로 변환합니다.
     * 다음 개행 문자들을 처리합니다:
     * - \r\n (Windows)
     * - \r (Mac OS 9 이하)
     * - \n (Unix/Linux/Mac OS X)
     *
     * @param source 변환할 텍스트
     * @return HTML 줄바꿈 태그가 포함된 텍스트, null인 경우 null 반환
     */
    public static String returnToBr(String source) {
        if (source == null) {
            return null;
        }
        return source.replaceAll("\r\n", "<br/>").replaceAll("\r", "<br/>").replaceAll("\n", "<br/>");
    }

    /**
     * 문자열 시작 부분의 공백을 HTML non-breaking space로 변환합니다.
     * 줄의 시작 부분 공백만 변환되며, 다른 위치의 공백은 유지됩니다.
     *
     * @param source 변환할 문자열
     * @return 변환된 문자열, null인 경우 null 반환
     */
    public static String spaceToNbsp(String source) {
        if (source == null) {
            return null;
        }
        return source.replaceAll("^ ", "&nbsp;").replaceAll("\n ", "\n&nbsp;");
    }

    /**
     * 문자열이 전화번호 형식에 맞는지 확인합니다.
     *
     * @param source 검사할 문자열
     * @return 전화번호 형식이 맞으면 true, 그렇지 않으면 false
     */
    public static boolean isTel(String source) {
        if (source == null) {
            return false;
        }
        return source.matches("^(\\d\\d\\d?)-?(\\d\\d\\d\\d?)-?(\\d\\d\\d\\d)$");
    }

    /**
     * 두 문자열을 연결합니다.
     *
     * @param str1 첫 번째 문자열
     * @param str2 두 번째 문자열
     * @return 연결된 문자열
     */
    public static String concat(String str1, String str2) {
        return str1 + str2;
    }

    /**
     * 파일 경로에서 확장자를 제외한 파일 이름을 추출합니다.
     *
     * @param filepath 파일 경로
     * @return 확장자를 제외한 파일 이름, 입력이 null인 경우 빈 문자열 반환
     */
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

    /**
     * 날짜에 해당하는 요일 이름을 한글로 반환합니다.
     *
     * @param date 요일을 확인할 날짜 객체
     * @return 요일의 한글 이름 (예: "월", "화" 등), 유효하지 않은 날짜인 경우 빈 문자열 반환
     */
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

    /**
     * 문자열을 지정된 형식의 Date 객체로 변환합니다.
     *
     * @param dateString 변환할 날짜 문자열
     * @param format 날짜 문자열의 형식
     * @return 변환된 Date 객체
     * @throws RuntimeException 날짜 형식이 잘못된 경우
     */
    public static Date stringToDate(String dateString, String format) {
        SimpleDateFormat simpleFormat = new SimpleDateFormat(format);
        simpleFormat.setTimeZone(timeZone);
        try {
            return simpleFormat.parse(dateString);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Date 객체로부터 한국 시간대의 Calendar 객체를 생성합니다.
     *
     * @param date Calendar 객체로 변환할 Date 객체
     * @return 한국 시간대로 설정된 Calendar 객체
     */
    public static Calendar getCalendar(Date date) {
        Calendar cal = Calendar.getInstance(timeZone, Locale.KOREAN);
        cal.setTime(date);
        return cal;
    }

    /**
     * Date 객체를 지정된 형식의 문자열로 변환합니다.
     *
     * @param date 변환할 Date 객체
     * @param format 변환할 날짜 형식
     * @return 지정된 형식으로 변환된 날짜 문자열
     */
    public static String dateToString(Date date, String format) {
        SimpleDateFormat simpleFormat = new SimpleDateFormat(format);
        simpleFormat.setTimeZone(timeZone);
        return simpleFormat.format(date.getTime());
    }

    /**
     * Calendar 객체를 지정된 형식의 문자열로 변환합니다.
     *
     * @param date 변환할 Calendar 객체
     * @param format 변환할 날짜 형식
     * @return 지정된 형식으로 변환된 날짜 문자열
     */
    public static String dateToString(Calendar date, String format) {
        SimpleDateFormat simpleFormat = new SimpleDateFormat(format);
        simpleFormat.setTimeZone(timeZone);
        return simpleFormat.format(date.getTime());
    }

    /**
     * 숫자를 한글 표기로 변환합니다.
     * 1의 자리부터 경(京)까지 지원합니다.
     * (예: "1234" -> "일천이백삼십사")
     *
     * @param number 변환할 숫자 문자열
     * @return 한글로 변환된 숫자 문자열
     */
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

    /**
     * Double 값을 지정된 소수점 자릿수를 가진 문자열로 변환합니다.
     * 소수점 이하 불필요한 0은 표시하지 않습니다.
     *
     * @param src 변환할 Double 값
     * @param scale 소수점 이하 자릿수
     * @return 포맷팅된 숫자 문자열
     */
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

    /**
     * 초 단위의 시간을 "시간 분" 형식의 문자열로 변환합니다.
     * 시간이 0인 경우 분만 표시됩니다.
     * (예: 3661초 -> "1시간 1분")
     *
     * @param seconds 변환할 시간(초)
     * @return 포맷팅된 시간 문자열
     */
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