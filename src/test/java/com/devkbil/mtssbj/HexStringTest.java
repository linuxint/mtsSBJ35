package com.devkbil.mtssbj;

public class HexStringTest {
    public static void main(String[] args) {

        String fileKey = "80467011348CC3130923Ok99LhMyCq";
        String fileKey1 = "804660100120221123131333000582";
        System.out.println(fileKey.substring(7, 14));
        System.out.println(fileKey1.substring(10, 18));
        System.out.println(Integer.valueOf(fileKey.substring(7, 14), 16));
        //System.out.println(Integer.valueOf(fileKey1.substring(7,14),16).toString());

        //System.out.println(Integer.toString(20221123,16).toUpperCase());

    }
}
