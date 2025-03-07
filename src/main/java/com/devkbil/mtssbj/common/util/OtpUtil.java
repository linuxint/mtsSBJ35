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

/**
 * OTP(One Time Password) 생성 및 검증을 위한 유틸리티 클래스입니다.
 * Google Authenticator 호환 OTP를 생성하고 검증하는 기능을 제공합니다.
 */
public class OtpUtil {

    /**
     * OTP 발급을 위한 키와 QR 코드 URL을 생성합니다.
     *
     * @param userName 사용자계정 - OTP 발급 대상 사용자의 식별자
     * @param hostName 시스템구분 - OTP를 사용할 시스템의 식별자
     * @return HashMap 객체로, 다음 키를 포함:
     *         - "encodedKey": Base32로 인코딩된 비밀키
     *         - "url": QR 코드 생성을 위한 URL
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
     * Google Authenticator 호환 QR 코드 URL을 생성합니다.
     *
     * @param user 사용자 식별자 - QR 코드에 포함될 사용자 이름
     * @param host 호스트 식별자 - QR 코드에 포함될 시스템/서비스 이름
     * @param secret Base32로 인코딩된 비밀키
     * @return Google Chart API를 사용하여 생성된 QR 코드 URL
     */
    public static String getQRBarcodeURL(String user, String host, String secret) {
        String format2 = "http://chart.apis.google.com/chart?cht=qr&chs=200x200&chl=otpauth://totp/%s@%s%%3Fsecret%%3D%s&chld=H|0";
        return String.format(format2, user, host, secret);
    }

    /**
     * 사용자가 입력한 OTP 코드의 유효성을 검증합니다.
     * 시간 기반으로 생성된 OTP 코드를 검증하며, 전후 3개의 시간 윈도우를 허용합니다.
     *
     * @param userCode 사용자가 입력한 6자리 OTP 코드
     * @param otpkey   Base32로 인코딩된 비밀키
     * @return 입력된 코드가 유효하면 true, 그렇지 않으면 false
     */
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

    /**
     * 주어진 키와 타임스탬프를 사용하여 TOTP(Time-based One-Time Password) 코드를 생성합니다.
     * HMAC-SHA1 알고리즘을 사용하여 6자리 코드를 생성합니다.
     *
     * @param key HMAC-SHA1 해시에 사용할 비밀키
     * @param timestamp 현재 시간 기반의 카운터 값
     * @return 생성된 6자리 OTP 코드
     * @throws NoSuchAlgorithmException HMAC-SHA1 알고리즘을 사용할 수 없는 경우
     * @throws InvalidKeyException 잘못된 키가 제공된 경우
     */
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

    /**
     * Google Authenticator 라이브러리를 사용하여 새로운 OTP 인증 정보를 생성합니다.
     * 이 메서드는 새로운 비밀키를 생성하고 해당하는 QR 코드 URL을 반환합니다.
     *
     * @return QR 코드 생성을 위한 URL 문자열
     */
    public static String generateOtp2() {
        GoogleAuthenticator googleAuthenticator = new GoogleAuthenticator();
        GoogleAuthenticatorKey googleAuthenticatorKey = googleAuthenticator.createCredentials();

        // 실제론 생성한 key를 DB에 저장해놔야 나중에 OTP를 검증할 수 있음
        String key = googleAuthenticatorKey.getKey();

        String qrUrl = GoogleAuthenticatorQRGenerator.getOtpAuthURL("adduci", "userId", googleAuthenticatorKey);

        return qrUrl;
    }
}