package com.devkbil.mtssbj;

import java.util.HashMap;
import java.util.Scanner;

import lombok.extern.slf4j.Slf4j;

import static com.devkbil.mtssbj.common.util.OtpUtil.checkCode;
import static com.devkbil.mtssbj.common.util.OtpUtil.generate;
import static com.devkbil.mtssbj.common.util.OtpUtil.generateOtp2;

@Slf4j
public class GenerateOtp2Test {

    public static void main(String[] args) {

        String qturl = generateOtp2();
        log.debug(qturl);

        // 키값 생성부분
        HashMap<String, String> map = generate("linuxint@gmail.com", "localhost");
        String otpkey = map.get("encodedKey");
        String url = map.get("url");
        System.out.println("Generated Key : " + otpkey); // 앱에 세팅할 코드
        System.out.println("QR CODE : " + url);

        // 생성된 OTP 패스워드 인증 부분
        Scanner scan = new Scanner(System.in);
        System.out.print("Input OTP Code : "); // 키보드에서 입력 - 앱의코드
        boolean check = checkCode(scan.next(), otpkey);
        System.out.println(check);
    }
}