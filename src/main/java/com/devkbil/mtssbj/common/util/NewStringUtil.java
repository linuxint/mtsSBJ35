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

    public static String replace(String source, String pattern, String replace) {
        if (isEmpty(source)) {
            return "";
        }
        if (replace == null) {
            replace = "";
        }

        int i = 0;
        int j = 0;
        int k = pattern.length();

        StringBuilder buf = new StringBuilder();
        String result = source;
        while ((j = source.indexOf(pattern, i)) >= 0) {
            if (buf == null) {
                buf = new StringBuilder(source.length() * 2);
            }
            buf.append(source, i, j);
            buf.append(replace);

            i = j + k;
        }

        if (i != 0) {
            buf.append(source.substring(i));
            result = buf.toString();
        }
        return result;
    }
}
