package com.devkbil.mtssbj;

import jodd.util.StringUtil;

import com.devkbil.mtssbj.common.util.TimeUtil;

import org.springframework.util.StringUtils;

public class StringIsEmptyTest {

    public static void main(String[] args) {
        String text = "  ";

        int loopCount = 100_000_000; // 반복 횟수
        TimeUtil timeUtil = new TimeUtil(); // TimeUtil 객체 생성

        // 1. text.equals("")
        timeUtil.setStartTime();
        for (int i = 0; i < loopCount; i++) {
            if(text.equals("")) {
                ;
            }
        }
        timeUtil.setEndTime();
        long durationEquals = timeUtil.getDiffTime();
        System.out.println(".equals(\"\") duration: \t" + durationEquals + " ms");

        // 2. text.isEmpty()
        timeUtil.setStartTime();
        for (int i = 0; i < loopCount; i++) {
            if(text.isEmpty()) {
                ;
            }
        }
        timeUtil.setEndTime();
        long durationIsEmpty = timeUtil.getDiffTime();
        System.out.println(".isEmpty() duration: \t\t" + durationIsEmpty + " ms");


        // 2. text.isEmpty()
        timeUtil.setStartTime();
        for (int i = 0; i < loopCount; i++) {
            if(text==null || text.equals("")) {
                ;
            }
        }
        timeUtil.setEndTime();
        long durationEmptyIsEmpty = timeUtil.getDiffTime();
        System.out.println("==null || .equals('') duration: \t\t" + durationEmptyIsEmpty + " ms");

        // 3. Spring's StringUtils.hasLength(text)
        timeUtil.setStartTime();
        for (int i = 0; i < loopCount; i++) {
            if(StringUtils.hasLength(text)) {
                ;
            }
        }
        timeUtil.setEndTime();
        long durationHasLength = timeUtil.getDiffTime();
        System.out.println("spring.hasLength() duration: " + durationHasLength + " ms");

        // 4. Spring's StringUtils.hasText(text)
        timeUtil.setStartTime();
        for (int i = 0; i < loopCount; i++) {
            if(StringUtils.hasText(text)) {
                ;
            }
        }
        timeUtil.setEndTime();
        long durationHasText = timeUtil.getDiffTime();
        System.out.println("spring.hasText() duration: " + durationHasText + " ms");

        // 5. Jodd Util's StringUtil.isEmpty(text)
        timeUtil.setStartTime();
        for (int i = 0; i < loopCount; i++) {
            if(StringUtil.isEmpty(text)) {
                ;
            }
        }
        timeUtil.setEndTime();
        long durationJoddIsEmpty = timeUtil.getDiffTime();
        System.out.println("jodd.isEmpty() duration: \t" + durationJoddIsEmpty + " ms");

        // 6. String trim isEmpty
        timeUtil.setStartTime();
        for (int i = 0; i < loopCount; i++) {
            if(text.trim().isEmpty()) {
                ;
            }
        }
        timeUtil.setEndTime();
        long durationTrimIsempty = timeUtil.getDiffTime();
        System.out.println("trim isEmpty duration: \t" + durationTrimIsempty + " ms");

        // 비교 비율 출력
        System.out.println("isEmpty() to equals(\"\") ratio: " +
                ((float) durationIsEmpty / (float) durationEquals));
    }
}