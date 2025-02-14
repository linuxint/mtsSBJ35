package com.devkbil.mtssbj.common.util;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class SecurityUtil {
    public static String sha256(String input) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        digest.reset();
        digest.update(input.getBytes(StandardCharsets.UTF_8));
        return byteArrayToHex(digest.digest());
    }

    public static String sha512(String input) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest digest = MessageDigest.getInstance("SHA-512");
        digest.reset();
        digest.update(input.getBytes(StandardCharsets.UTF_8));
        return byteArrayToHex(digest.digest());
    }

    public static String encodeBase64(String input) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        return Base64.getEncoder().encodeToString(input.getBytes());
    }

    public static String decodeBase64(String input) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        return new String(Base64.getDecoder().decode(input));
    }

    public static String byteArrayToHex(byte[] buf) {
        StringBuffer strbuf = new StringBuffer();
        for (int i = 0; i < buf.length; i++) {
            strbuf.append(String.format("%02X", buf[i]));
        }

        return strbuf.toString();
    }

    public static byte[] hexToByteArray(String hexString) {
        byte[] byteArray = null;
        if (hexString != null && hexString.length() != 0) {
            byteArray = new byte[hexString.length() / 2];
            for (int i = 0; i < byteArray.length; i++) {
                byteArray[i] = (byte) Integer.parseInt(hexString.substring(2 * i, 2 * i + 2), 16);
            }
        }
        return byteArray;
    }

    public static String getRandomAlphaNumeric(int len) {
        char[] charSet = "0123456789abcdefghijklmnopqrstuvwxyz".toCharArray();

        int idx = 0;
        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < len; i++) {
            idx = (int) (charSet.length * Math.random());
            sb.append(charSet[idx]);
        }

        return sb.toString();
    }

    public static String xss(String src) {
        if (src == null) {
            return null;
        }
        src = src.replaceAll("&", "&#38;");
        src = src.replaceAll("\"", "&quot;");
        src = src.replaceAll("'", "&rsquo;");
        src = src.replaceAll("<", "&lt;");
        src = src.replaceAll(">", "&gt;");

        return src;
    }

    public static String unxss(String src) {
        if (src == null) {
            return null;
        }
        src = src.replaceAll("&#38;", "&");
        src = src.replaceAll("&quot;", "\"");
        src = src.replaceAll("&rsquo;", "'");
        src = src.replaceAll("&lt;", "<");
        src = src.replaceAll("&gt;", ">");

        return src;
    }

    public static String xssScript(String src) {
        if (src == null) {
            return null;
        }
        src = src.replaceAll("(?i)<\\s*script\\s*>", "&lt;script&gt;");
        src = src.replaceAll("(?i)<\\s*/\\s*script\\s*>", "&lt;&quot;script&gt;");
        return src;
    }
}
