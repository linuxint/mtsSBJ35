package com.devkbil.mtssbj.member;

/**
 * 회원 관련 상수를 정의하는 클래스.
 * 회원 기능에서 사용되는 공통 상수 값들을 중앙 집중적으로 관리합니다.
 */
public class MemberConstant {
    /**
     * 쿠키 만료 시간을 초 단위로 정의합니다.
     * 계산: 60(초) * 60(분) * 24(시간) * 30(일) = 1개월
     */
    static final int COOKIE_EXPIRE = 60 * 60 * 24 * 30; // 1 month
}
