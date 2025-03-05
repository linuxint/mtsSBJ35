package com.devkbil.mtssbj.datetime;

import com.devkbil.mtssbj.common.util.DateUtilLunar;

import java.util.HashMap;

public class DateUtilLunarTest {

    public static void main(String[] args) {

        // 양력->음력 변환
        String sSlrDate = "20140908";
        HashMap hm = DateUtilLunar.toLunar(sSlrDate);

        String retLrrDate = hm.get("day").toString();
        String retLeapMonth = hm.get("leap").toString();

        System.out.println("[양력->음력 변환]");
        System.out.println(sSlrDate + " -> " + retLrrDate);
        System.out.println(DateUtilLunar.formatDate(retLrrDate, "-"));
        System.out.println("윤달여부:" + retLeapMonth);

        System.out.println();
        // 음력->양력 변환
        String sLrrDate = "20140815";
        int iLeapMonth = 0; // 평달 : 0, 윤달 : 1
        String retSlrDate = DateUtilLunar.toSolar(sLrrDate, iLeapMonth);

        System.out.println("[음력->양력 변환]");
        System.out.println(sLrrDate + " -> " + retSlrDate);
        System.out.println(DateUtilLunar.formatDate(retSlrDate, "-"));
    }
}
