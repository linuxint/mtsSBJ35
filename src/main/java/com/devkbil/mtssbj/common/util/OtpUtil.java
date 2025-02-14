package com.devkbil.mtssbj.common.util;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;

import org.apache.commons.codec.binary.Base32;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

public class OtpUtil {

    /**
     * OTP 발급 업무키
     *
     * @param userName 사용자계정
     * @param hostName 시스템구분
     * @return
     */
    public static HashMap<String, String> generate(String userName, String hostName) {

        HashMap<String, String> map = new HashMap<>();
        byte[] buffer = new byte[5 + 5 * 5];
        new Random().nextBytes(buffer);
        Base32 codec = new Base32();
        byte[] secretKey = Arrays.copyOf(buffer, 10);
        byte[] bEncodedKey = codec.encode(secretKey);

        String encodedKey = new String(bEncodedKey);
        String url = getQRBarcodeURL(userName, hostName, encodedKey);
        map.put("encodedKey", encodedKey);
        map.put("url", url);

        return map;
    }

    /**
     * @param user
     * @param host
     * @param secret
     * @return
     */
    public static String getQRBarcodeURL(String user, String host, String secret) {
        String format2 = "http://chart.apis.google.com/chart?cht=qr&chs=200x200&chl=otpauth://totp/%s@%s%%3Fsecret%%3D%s&chld=H|0";
        return String.format(format2, user, host, secret);
    }


    public static boolean checkCode(String userCode, String otpkey) {
        long otpnum = Integer.parseInt(userCode);
        long wave = new Date().getTime() / 30000;
        boolean result = false;
        try {
            Base32 codec = new Base32();
            byte[] decodedKey = codec.decode(otpkey);
            int window = 3;
            for (int i = -window; i <= window; ++i) {
                long hash = verifyCode(decodedKey, wave + i);
                if (hash == otpnum) {
                    result = true;
                }
            }
        } catch (InvalidKeyException | NoSuchAlgorithmException e) {
            ; //log.error(e.getMessage());
        }
        return result;
    }

    private static int verifyCode(byte[] key, long timestamp) throws NoSuchAlgorithmException, InvalidKeyException {
        byte[] data = new byte[8];
        long tempValue = timestamp;
        for (int i = 8; i-- > 0; tempValue >>>= 8) {
            data[i] = (byte) tempValue;
        }

        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "HmacSHA1");
        Mac macInstance = Mac.getInstance("HmacSHA1");
        macInstance.init(secretKeySpec);
        byte[] hashedData = macInstance.doFinal(data);

        int offset = hashedData[20 - 1] & 0xF;

        long truncatedResult = 0;
        for (int i = 0; i < 4; ++i) {
            truncatedResult <<= 8;
            truncatedResult |= (hashedData[offset + i] & 0xFF);
        }

        truncatedResult &= 0x7FFFFFFF;
        truncatedResult %= 1000000;

        return (int) truncatedResult;
    }

    public static String generateOtp2() {
        GoogleAuthenticator googleAuthenticator = new GoogleAuthenticator();
        GoogleAuthenticatorKey googleAuthenticatorKey = googleAuthenticator.createCredentials();

        // 실제론 생성한 key를 DB에 저장해놔야 나중에 OTP를 검증할 수 있음
        String key = googleAuthenticatorKey.getKey();

        String qrUrl = GoogleAuthenticatorQRGenerator.getOtpAuthURL("adduci", "userId", googleAuthenticatorKey);

        return qrUrl;
    }
}
