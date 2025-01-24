package com.devkbil.mtssbj;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import com.devkbil.mtssbj.common.util.SecurityUtil;

public class SecurityUtilTest {

    /*
    안녕? base64 encode : 7JWI64WVPw==
    안녕? base64 decode : 안녕?
    안녕? sha256 : B8E6062790B1BB7B274E21ED93CF77CFEA49F6DBCEF21DC90BF16667A21C85BA
    안녕? sha512 : 29F7079E6A30921EE941BE349D6479277B26F1A4FC2ACE7EE2DF48C9848EC1B70027B1E8111CB19E5D27ABE5B2F130D7AC6BE3E22DBCD6DA29A59FE71623D2A5
     */

    public static void main(String[] args) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        System.out.println("1234 base64 encode : " + SecurityUtil.encodeBase64("1234"));
        System.out.println("안녕? base64 encode : " + SecurityUtil.encodeBase64("안녕?"));
        System.out.println("안녕? base64 decode : " + SecurityUtil.decodeBase64("7JWI64WVPw=="));
        System.out.println("안녕? sha256 : " + SecurityUtil.sha256("안녕?"));
        System.out.println("안녕? sha512 : " + SecurityUtil.sha512("안녕?"));
        System.out.println("1234 sha512 : " + SecurityUtil.sha512("1234"));
        System.out.println("1234 sha256 : " + SecurityUtil.sha256("1234"));
    }

}
