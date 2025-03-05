package com.devkbil.mtssbj;

import org.junit.jupiter.api.Test;
import org.springframework.util.StringUtils;

public class StringUtilTest {
    public static void main(String[] args) {
        String[] page = "1".split(",");
        // System.out.println(page.length);
        // System.out.println(Integer.parseInt(page[0]));

        String tempArr = "011,008,019,020,040,025,037,038";
        String var = null;
        // System.out.println(tempArr.indexOf(var));

        String prdctCd = "030201200180";
        String ClsfiL = prdctCd.substring(0, 4);
        String clsfiM = prdctCd.substring(4, 7);
        String clsfiL = "";

        String[] prdtcd = "".split(",");
        int nLoop = 0;
        String[] arrTempArr = tempArr.split(",");
        for (String a : arrTempArr) {
            System.out.println(a);
            System.out.println(prdtcd.length);
            if (prdtcd.length > nLoop) {
                System.out.println(prdtcd[nLoop]);
            } else {

            }
            nLoop++;
        }
    }

    @Test
    public void hasTextTest() {
        String str1 = "";
        if (StringUtils.hasText(str1)) {
            System.out.println("has text");
        } else {
            System.out.println("not has text");
        }
        String str2 = " ";
        if (StringUtils.hasText(str2)) {
            System.out.println("has text");
        } else {
            System.out.println("not has text");
        }
        String str3 = null;
        if (StringUtils.hasText(str3)) {
            System.out.println("has text");
        } else {
            System.out.println("not has text");
        }
    }
}
