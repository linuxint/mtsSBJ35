package com.devkbil.mtssbj.common.util;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class NewStringUtil {

    public static boolean isEmpty(String str) {
        return (str == null || str.length() == 0) || (str.trim().equals(""));
    }

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

    public static Object escapeXml(String str) {
        str = str.replaceAll("&", "&amp;");
        str = str.replaceAll("<", "&lt;");
        str = str.replaceAll(">", "&gt;");
        str = str.replaceAll("\"", "&quot;");
        str = str.replaceAll("'", "&apos;");
        return str;
    }

    /**
     * @param str
     * @param src
     * @param des
     * @return
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
