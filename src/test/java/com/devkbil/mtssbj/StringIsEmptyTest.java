package com.devkbil.mtssbj;

import jodd.util.StringUtil;

import com.devkbil.mtssbj.common.util.TimeUtil;

import org.springframework.util.StringUtils;

public class StringIsEmptyTest {

    public static void main(String[] args) {
        String text = "  ";
        int loopCount = 100_000_000; // 반복 횟수
        long durationEquals = 0;
        long durationIsEmpty = 0;
        long durationEmptyIsEmpty = 0;
        long durationHasLength = 0;
        long durationHasText = 0;
        long durationJoddIsEmpty = 0;
        long durationTrimIsempty = 0;

        try {
            // 1. text.equals("")
            TimeUtil.setStartTime();
            for (int i = 0; i < loopCount; i++) {
                if (text.equals("")) {
                    ;
                }
            }
            TimeUtil.setEndTime();
            durationEquals = TimeUtil.getDiffTime();
            System.out.println(".equals(\"\") duration: \t" + durationEquals + " ms");

            // 2. text.isEmpty()
            TimeUtil.setStartTime();
            for (int i = 0; i < loopCount; i++) {
                if (text.isEmpty()) {
                    ;
                }
            }
            TimeUtil.setEndTime();
            durationIsEmpty = TimeUtil.getDiffTime();
            System.out.println(".isEmpty() duration: \t\t" + durationIsEmpty + " ms");

            // 3. text==null || text.equals("")
            TimeUtil.setStartTime();
            for (int i = 0; i < loopCount; i++) {
                if (text == null || text.equals("")) {
                    ;
                }
            }
            TimeUtil.setEndTime();
            durationEmptyIsEmpty = TimeUtil.getDiffTime();
            System.out.println("==null || .equals('') duration: \t\t" + durationEmptyIsEmpty + " ms");

            // 4. Spring's StringUtils.hasLength(text)
            TimeUtil.setStartTime();
            for (int i = 0; i < loopCount; i++) {
                if (StringUtils.hasLength(text)) {
                    ;
                }
            }
            TimeUtil.setEndTime();
            durationHasLength = TimeUtil.getDiffTime();
            System.out.println("spring.hasLength() duration: " + durationHasLength + " ms");

            // 5. Spring's StringUtils.hasText(text)
            TimeUtil.setStartTime();
            for (int i = 0; i < loopCount; i++) {
                if (StringUtils.hasText(text)) {
                    ;
                }
            }
            TimeUtil.setEndTime();
            durationHasText = TimeUtil.getDiffTime();
            System.out.println("spring.hasText() duration: " + durationHasText + " ms");

            // 6. Jodd Util's StringUtil.isEmpty(text)
            TimeUtil.setStartTime();
            for (int i = 0; i < loopCount; i++) {
                if (StringUtil.isEmpty(text)) {
                    ;
                }
            }
            TimeUtil.setEndTime();
            durationJoddIsEmpty = TimeUtil.getDiffTime();
            System.out.println("jodd.isEmpty() duration: \t" + durationJoddIsEmpty + " ms");

            // 7. String trim isEmpty
            TimeUtil.setStartTime();
            for (int i = 0; i < loopCount; i++) {
                if (text.trim().isEmpty()) {
                    ;
                }
            }
            TimeUtil.setEndTime();
            durationTrimIsempty = TimeUtil.getDiffTime();
            System.out.println("trim isEmpty duration: \t" + durationTrimIsempty + " ms");

        } finally {
            TimeUtil.cleanup();
        }

        // 비교 비율 출력
        System.out.println(
            "isEmpty() to equals(\"\") ratio: " + ((float)durationIsEmpty / (float)durationEquals));
    }
}
