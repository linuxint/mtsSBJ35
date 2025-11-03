package com.devkbil.common.util;

import java.util.Collection;
import java.util.Map;
import java.util.regex.Pattern;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

/**
 * Spring Boot 기반 유효성 검사 유틸리티
 * - 순수 static 유틸리티 (Bean 주입 불필요)
 * - Service/Controller에서 Validation 보조용으로 사용
 */
@Slf4j
@UtilityClass
public class ValidateUtil {

    private static final int MAX_LOOP_CNT = 3;

    /** 문자열 길이 범위 검증 */
    public static boolean rangeLength(String value, int min, int max) {
        return value != null && value.trim().length() >= min && value.trim().length() <= max;
    }

    /** 영문 포함 여부 */
    public static boolean containsEng(String value) {
        return regex(value, "[a-zA-Z]+");
    }

    /** 숫자 포함 여부 */
    public static boolean containsDigit(String value) {
        return regex(value, "\\d+");
    }

    /** 숫자만 구성 */
    public static boolean onlyDigit(String value) {
        return regex(value, "^\\d+$");
    }

    /** 숫자만 구성 - 예외 발생 */
    public static void onlyDigit(String value, String msg) {
        if (!onlyDigit(value)) {
            throw new ValidateException(msg);
        }
    }

    /** float 변환 가능 여부 */
    public static void canParseFloat(String value, String msg) {
        try {
            Float.parseFloat(value);
        } catch (Exception e) {
            throw new ValidateException(msg, e);
        }
    }

    /** 특수문자 포함 여부 */
    public static boolean containsSpecialChar(String value) {
        if (value == null) return false;
        String specialChars = "~․!@#$%^&*()_-+={}[]|\\;:'\"<>,.?/";
        for (int i = 0; i < specialChars.length(); i++) {
            if (value.indexOf(specialChars.charAt(i)) > -1) return true;
        }
        return false;
    }

    /** 정규식 매칭 */
    public static boolean regex(String value, String regex) {
        if (value == null || regex == null) return false;
        return Pattern.compile(regex).matcher(value).find();
    }

    /** 한글 포함 여부 */
    public static boolean isKo(String value) {
        return regex(value, ".*[ㄱ-ㅎㅏ-ㅣ가-힣]+.*");
    }

    /** 반복 문자 체크 */
    public static boolean loopChar(String userPwd) {
        if (userPwd == null) return false;
        int tmp = 0, loopCnt = 0;
        for (int i = 0; i < userPwd.length(); i++) {
            char c = userPwd.charAt(i);
            if (c == tmp) loopCnt++; else loopCnt = 0;
            if (loopCnt == MAX_LOOP_CNT - 1) return true;
            tmp = c;
        }
        return false;
    }

    /** 연속 문자/숫자 체크 */
    public static boolean continuosChar(String userPwd) {
        if (userPwd == null) return false;
        int tmp = 0, inc = 0, dec = 0;
        for (int i = 0; i < userPwd.length(); i++) {
            char c = userPwd.charAt(i);
            int diff = c - tmp;
            if (diff == 1) { inc++; dec = 0; }
            else if (diff == -1) { dec++; inc = 0; }
            else { inc = dec = 0; }
            if (inc == MAX_LOOP_CNT - 1 || dec == MAX_LOOP_CNT - 1) return true;
            tmp = c;
        }
        return false;
    }

    /** 약한 비밀번호 검증 */
    public static void weakPassword(String newPwd) {
        if (newPwd == null)
            throw new ValidateException("비밀번호는 반드시 입력해 주세요.");
        if (!rangeLength(newPwd, 6, 20))
            throw new ValidateException("비밀번호는 6 ~ 20자리까지 입력해 주세요.");
        if (!containsEng(newPwd) || !containsDigit(newPwd))
            throw new ValidateException("비밀번호는 영문/숫자 모두 1문자 이상 포함되게 입력해 주세요.");
    }

    /** 강력한 비밀번호 검증 */
    public static void password(String userId, String newPwd, String currentPwd) {
        notEmpty(newPwd, "비밀번호는 반드시 입력해 주세요.");

        if (userId != null && newPwd.contains(userId))
            throw new ValidateException("비밀번호는 사용자 ID가 포함되지 않게 입력해 주세요.");
        if (!rangeLength(newPwd, 8, 20))
            throw new ValidateException("비밀번호는 8 ~ 20자리까지 입력해 주세요.");
        if (!containsEng(newPwd) || !containsDigit(newPwd) || !containsSpecialChar(newPwd))
            throw new ValidateException("비밀번호는 영문/숫자/특수문자 모두 1문자 이상 포함되게 입력해 주세요.");
        if (loopChar(newPwd) || continuosChar(newPwd))
            throw new ValidateException("비밀번호는 연속 문자/숫자가 포함되지 않게 입력해 주세요.");
        if (currentPwd != null && currentPwd.equals(newPwd))
            throw new ValidateException("변경 전 비밀번호는 사용할 수 없습니다.");
    }

    /** 아이디 형식 검증 */
    public static void userId(String loginId) {
        if (!regex(loginId, "^[a-zA-Z0-9]{6,20}$"))
            throw new ValidateException("아이디는 영문, 숫자 조합으로 6~20자로 입력해주세요.");
        if (!regex(loginId, "^[a-zA-Z]"))
            throw new ValidateException("아이디는 영문으로 시작해 주세요.");
    }

    /** null 여부 검증 */
    public static void notNull(Object value, String msg) {
        if (value == null) throw new ValidateException(msg);
    }

    /** 문자열 not empty */
    public static void notEmpty(String src, String msg) {
        if (src == null || src.trim().isEmpty()) throw new ValidateException(msg);
    }

    /** 컬렉션 not empty */
    public static void notEmpty(Collection<?> value, String msg) {
        if (value == null || value.isEmpty()) throw new ValidateException(msg);
    }

    /** 맵 not empty */
    public static void notEmpty(Map<?, ?> value, String msg) {
        if (value == null || value.isEmpty()) throw new ValidateException(msg);
    }

    /** 이메일 검증 (간단 버전) */
    public static void email(String email, String msg) {
        String regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        if (!regex(email, regex)) {
            throw new ValidateException(msg);
        }
    }
}