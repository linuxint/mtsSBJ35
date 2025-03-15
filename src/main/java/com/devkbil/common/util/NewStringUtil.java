package com.devkbil.common.util;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * 문자열 처리를 위한 유틸리티 클래스입니다.
 * 문자열의 분할, 치환, XML 이스케이프 등 다양한 문자열 조작 기능을 제공합니다.
 */
public class NewStringUtil {

    /**
     * 문자열이 null이거나 비어있는지 확인합니다.
     *
     * @param str 검사할 문자열
     * @return 문자열이 null이거나 비어있으면 true, 그렇지 않으면 false
     */
    public static boolean isEmpty(String str) {
        return (str == null || str.length() == 0) || (str.trim().equals(""));
    }

    /**
     * 문자열을 지정된 구분자로 분할하여 문자열 배열로 반환합니다.
     *
     * @param srcTxt 분할할 원본 문자열
     * @param delim  구분자
     * @return 분할된 문자열 배열, 원본 문자열이 null이면 null 반환
     */
    public static String[] split(String srcTxt, String delim) {
        List<String> list = toArrayList(srcTxt, delim);
        if (list != null) {
            String[] strings = new String[list.size()];
            for (int i = 0; i < strings.length; i++) {
                strings[i] = list.get(i);
            }

            return strings;
        } else {
            // SecurityPrism
            // return null;
            String[] stringsNull = null;
            return stringsNull;
        }
    }

    /**
     * 문자열을 지정된 구분자로 분할하여 ArrayList로 반환합니다.
     *
     * @param srcTxt 분할할 원본 문자열
     * @param delim 구분자
     * @return 분할된 문자열을 담은 ArrayList
     */
    public static ArrayList<String> toArrayList(String srcTxt, String delim) {
        ArrayList<String> list = new ArrayList<String>();
        if (isEmpty(srcTxt)) {
            return list;
        }

        StringTokenizer st = new StringTokenizer(srcTxt, delim);
        while (st.hasMoreTokens()) {
            list.add(st.nextToken());
        }
        return list;
    }

    /**
     * 문자열의 특수 문자를 XML 엔티티로 이스케이프 처리합니다.
     *
     * @param str 이스케이프 처리할 문자열
     * @return 이스케이프 처리된 문자열
     */
    public static Object escapeXml(String str) {
        str = str.replaceAll("&", "&amp;");
        str = str.replaceAll("<", "&lt;");
        str = str.replaceAll(">", "&gt;");
        str = str.replaceAll("\"", "&quot;");
        str = str.replaceAll("'", "&apos;");
        return str;
    }

    /**
     * 문자열 내의 특정 패턴을 다른 문자열로 모두 치환합니다.
     *
     * @param str 원본 문자열
     * @param src 검색할 패턴
     * @param des 대체할 문자열
     * @return 치환이 완료된 문자열
     */
    public static String replaceAll(String str, String src, String des) {
        StringBuffer sb = new StringBuffer(str.length());
        int startIdx = 0;
        int oldIdx = 0;
        for (; ; ) {
            startIdx = str.indexOf(src, startIdx);
            if (startIdx == -1) {
                sb.append(str.substring(oldIdx));
                break;
            }
            sb.append(str, oldIdx, startIdx);
            sb.append(des);

            startIdx += src.length();
            oldIdx = startIdx;
        }
        return sb.toString();
    }

    /**
     * 소스 문자열 내에서 지정된 검색 패턴의 모든 발생을 지정된 대체 텍스트로 교체합니다.
     *
     * @param sourceText      교체가 수행될 원본 문자열;
     *                        null이거나 비어 있으면 빈 문자열이 반환됩니다
     * @param searchPattern   소스 문자열에서 검색할 하위 문자열;
     *                        null이거나 비어 있으면 안 됩니다
     * @param replacementText 검색 패턴의 각 발생을 대체할 대체 텍스트;
     *                        null 값은 빈 문자열로 처리됩니다
     * @return 검색 패턴의 모든 발생이 대체 텍스트로 교체된 수정된 문자열,
     * 또는 일치 항목이 없을 경우 원본 문자열
     */
    public static String replace(String sourceText, String searchPattern, String replacementText) {
        if (isEmpty(sourceText)) {
            return "";
        }
        if (replacementText == null) {
            replacementText = "";
        }

        int currentIndex = 0;
        int foundIndex = 0;
        int patternLength = searchPattern.length();

        StringBuilder resultBuffer = new StringBuilder();
        String resultString = sourceText;
        while ((foundIndex = sourceText.indexOf(searchPattern, currentIndex)) >= 0) {
            if (resultBuffer == null) {
                resultBuffer = new StringBuilder(sourceText.length() * 2);
            }
            resultBuffer.append(sourceText, currentIndex, foundIndex);
            resultBuffer.append(replacementText);

            currentIndex = foundIndex + patternLength;
        }

        if (currentIndex != 0) {
            resultBuffer.append(sourceText.substring(currentIndex));
            resultString = resultBuffer.toString();
        }
        return resultString;
    }
}