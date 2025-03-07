package com.devkbil.mtssbj.common.util;

import java.util.Collection;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.Getter;

public class ValidateUtil {

    private static final int MAX_LOOP_CNT = 3;

    /**
     * 문자열의 길이가 지정된 범위 내에 있는지 확인합니다.
     *
     * @param value 검사할 문자열; null일 수 있습니다
     * @param min   최소 길이 (포함)
     * @param max   최대 길이 (포함)
     * @return 문자열의 길이가 범위 내에 있으면 {@code true}, 그렇지 않으면 {@code false}
     */
    public static boolean rangeLength(String value, int min, int max) {
        return value != null && value.trim().length() >= min && value.trim().length() <= max;
    }

    /**
     * 문자열에 하나 이상의 영문 알파벳 문자가 포함되어 있는지 확인합니다.
     *
     * @param value 검사할 문자열; null일 수 있습니다
     * @return 문자열에 영문 문자가 포함되어 있으면 {@code true}, 그렇지 않으면 {@code false}
     */
    public static boolean containsEng(String value) {
        return ValidateUtil.regex(value, "[a-zA-Z]+");
    }

    /**
     * 문자열에 하나 이상의 숫자가 포함되어 있는지 확인합니다.
     *
     * @param value 검사할 문자열; null일 수 있습니다
     * @return 문자열에 숫자가 포함되어 있으면 {@code true}, 그렇지 않으면 {@code false}
     */
    public static boolean containsDigit(String value) {
        return ValidateUtil.regex(value, "\\d+");
    }

    /**
     * 문자열이 숫자로만 구성되어 있는지 확인합니다.
     *
     * @param value 검사할 문자열; null일 수 있습니다
     * @return 문자열이 숫자로만 구성되어 있으면 {@code true}, 그렇지 않으면 {@code false}
     */
    public static boolean onlyDigit(String value) {
        return ValidateUtil.regex(value, "^\\d+$");
    }

    /**
     * 문자열이 숫자로만 구성되어 있는지 확인합니다. 실패 시 예외를 발생시킵니다.
     *
     * @param value 확인할 문자열
     * @param msg   검증 실패 시 사용할 에러 메시지
     * @throws ValidateException 문자열에 숫자가 아닌 문자가 포함된 경우
     */
    public static void onlyDigit(String value, String msg) {
        if (!ValidateUtil.regex(value, "^\\d+$")) {
            throw new ValidateException(msg);
        }
    }

    /**
     * 문자열이 float 값으로 변환 가능한지 확인합니다. 실패 시 예외를 발생시킵니다.
     *
     * @param value 검증할 문자열
     * @param msg   검증 실패 시 사용할 에러 메시지
     * @throws ValidateException 문자열이 float 값으로 변환할 수 없는 경우
     */
    public static void canParseFloat(String value, String msg) {
        try {
            Float.parseFloat(value);
        } catch (NumberFormatException e) {
            throw new ValidateException(msg);
        } catch (Exception e) {
            throw new ValidateException(msg);
        }
    }

    /**
     * 문자열에 특수 문자가 포함되어 있는지 확인합니다.
     *
     * @param value 검사할 문자열; null일 수 있습니다
     * @return 문자열에 특수 문자가 포함되어 있으면 {@code true}, 그렇지 않으면 {@code false}
     */
    public static boolean containsSpecialChar(String value) {
        String specialChars = "~․!@#$%^&*()_-+={}[]|\\;:'\"<>,.?/";
        for (int i = 0; i < specialChars.length(); i++) {
            if (value.indexOf(specialChars.charAt(i)) > -1) {
                return true;
            }
        }
        return false;
    }

    /**
     * 문자열이 주어진 정규식 패턴과 일치하는지 확인합니다.
     *
     * @param value 검사할 문자열
     * @param regex 일치시킬 정규식 패턴
     * @return 문자열이 패턴과 일치하면 {@code true}, 그렇지 않거나 매개변수가 null이면 {@code false}
     */
    public static boolean regex(String value, String regex) {
        if (value == null || regex == null) {
            return false;
        }
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(value);
        return matcher.find();
    }

    /**
     * 주어진 문자열에 한글이 포함되어 있는지 확인합니다.
     *
     * @param value 검사할 문자열; null일 수 있습니다
     * @return 문자열에 한글이 포함되어 있으면 {@code true}, 그렇지 않으면 {@code false}
     */
    public static boolean isKo(String value) {

        String regex = ".*[ㄱ-ㅎㅏ-ㅣ가-힣]+.*";
        if (value == null) {
            return false;
        }
        return regex(value, regex);
    }

    /**
     * 문자열에 반복된 문자가 최대 허용 반복 횟수(MAX_LOOP_CNT)를 초과하여 포함되어 있는지 확인합니다.
     * 예를 들어, MAX_LOOP_CNT가 3인 경우 "aaa", "111" 등의 패턴을 탐지합니다.
     *
     * @param userPwd 확인할 비밀번호 문자열
     * @return 반복된 문자가 제한을 초과하면 {@code true}, 그렇지 않으면 {@code false}
     */
    public static boolean loopChar(String userPwd) {
        int tmp = 0;
        int loopCnt = 0;
        for (int i = 0; i < userPwd.length(); i++) {
            if (userPwd.charAt(i) == tmp) {
                loopCnt++;
            } else {
                loopCnt = 0;
            }
            if (loopCnt == (MAX_LOOP_CNT - 1)) {
                return true;
            }
            tmp = userPwd.charAt(i);
        }
        return false;
    }

    /**
     * 문자열에 연속된 문자나 숫자가 지정된 기준 이상으로 포함되어 있는지 확인합니다.
     * 예: "123", "321", "abc", "cba".
     *
     * @param userPwd 확인할 문자열
     * @return 문자열에 연속된 문자/숫자가 포함되어 있으면 {@code true}, 그렇지 않으면 {@code false}
     */
    public static boolean continuosChar(String userPwd) {
        int tmp = 0;
        int reverseLoopCnt = 0;
        int loopCnt = 0;
        for (int i = 0; i < userPwd.length(); i++) {
            int gap = userPwd.charAt(i) - tmp;
            if (gap == 1) {
                reverseLoopCnt = 0;
                loopCnt++;
            } else if (gap == -1) {
                reverseLoopCnt++;
                loopCnt = 0;
            } else {
                reverseLoopCnt = 0;
                loopCnt = 0;
            }
            if (loopCnt == (MAX_LOOP_CNT - 1) || reverseLoopCnt == (MAX_LOOP_CNT - 1)) {
                return true;
            }
            tmp = userPwd.charAt(i);
        }
        return false;
    }

    /**
     * 기본 유효성 검사 규칙으로 비밀번호를 검증합니다.
     *
     * @param newPwd 검증할 비밀번호
     * @throws ValidateException 비밀번호가 유효성 검사 기준을 충족하지 않는 경우
     */
    public static void password(String newPwd) {
        password(null, newPwd, null);
    }

    /**
     * 사용자 ID 검사와 함께 비밀번호를 검증합니다.
     *
     * @param userId 비밀번호와 대조할 사용자 ID
     * @param newPwd 검증할 비밀번호
     * @throws ValidateException 비밀번호가 유효성 검사 기준을 충족하지 않는 경우
     */
    public static void password(String userId, String newPwd) {
        password(userId, newPwd, null);
    }

    /**
     * 기본 보안 요구사항에 따라 비밀번호를 검증합니다.
     * 비밀번호는 다음 조건을 만족해야 합니다:
     * - null이 아님
     * - 6~20자 길이
     * - 최소 1개의 영문자 포함
     * - 최소 1개의 숫자 포함
     *
     * @param newPwd 검증할 비밀번호
     * @throws ValidateException 비밀번호가 요구사항을 충족하지 않는 경우
     */
    public static void weakPassword(String newPwd) {
        if (newPwd == null) {
            throw new ValidateException("비밀번호는 반드시 입력해 주세요.");
        }
        if (!ValidateUtil.rangeLength(newPwd, 6, 20)) {
            throw new ValidateException("비밀번호는 6 ~ 20자리까지 입력해 주세요.");
        }
        if (!ValidateUtil.containsEng(newPwd)) {
            throw new ValidateException("비밀번호는 영문/숫자 모두 1문자 이상 포함되게 입력해 주세요.");
        }
        if (!ValidateUtil.containsDigit(newPwd)) {
            throw new ValidateException("비밀번호는 영문/숫자 모두 1문자 이상 포함되게 입력해 주세요.");
        }
    }

    /**
     * Validates a password against comprehensive security requirements.
     * The password must meet the following criteria:
     * - Not be null
     * - Not contain the user ID (if provided)
     * - Be between 8 and 20 characters in length
     * - Contain at least one English letter
     * - Contain at least one digit
     * - Contain at least one special character
     * - Not contain repeated characters (e.g., "111")
     * - Not contain sequential characters (e.g., "123", "abc")
     * - Not be the same as the current password (if provided)
     *
     * @param userId        the user ID to check against the password (optional)
     * @param newPwd        the new password to validate
     * @param encCurrentPwd the current encrypted password to compare against (optional)
     * @throws ValidateException with specific message if any validation rule is violated:
     *                           - "비밀번호는 반드시 입력해 주세요." if password is null
     *                           - "비밀번호는 사용자 ID가 포함되지 않게 입력해 주세요." if password contains user ID
     *                           - "비밀번호는 8 ~ 20자리까지 입력해 주세요." if length is invalid
     *                           - "비밀번호는 영문/숫자/특수 문자 모두 1문자 이상 조합되게 입력해 주세요." if missing required character types
     *                           - "비밀번호는 연속적인 문자/숫자(예:111,123,abc)가 포함되지 않게 입력해 주세요." if contains sequential patterns
     *                           - "변경 전 비밀번호는 사용할 수 없습니다. 새로운 비밀번호를 입력해 주세요." if same as current password
     */
    public static void password(String userId, String newPwd, String encCurrentPwd) {
        if (newPwd == null) {
            throw new ValidateException("비밀번호는 반드시 입력해 주세요.");
        }
        if (userId != null && newPwd.contains(userId)) {
            throw new ValidateException("비밀번호는 사용자 ID가 포함되지 않게 입력해 주세요.");
        }
        if (!ValidateUtil.rangeLength(newPwd, 8, 20)) {
            throw new ValidateException("비밀번호는 8 ~ 20자리까지 입력해 주세요.");
        }
        if (!ValidateUtil.containsEng(newPwd)) {
            throw new ValidateException("비밀번호는 영문/숫자/특수 문자 모두 1문자 이상 조합되게 입력해 주세요.");
        }
        if (!ValidateUtil.containsDigit(newPwd)) {
            throw new ValidateException("비밀번호는 영문/숫자/특수 문자 모두 1문자 이상 조합되게 입력해 주세요.");
        }
        if (!ValidateUtil.containsSpecialChar(newPwd)) {
            throw new ValidateException("비밀번호는 영문/숫자/특수 문자 모두 1문자 이상 조합되게 입력해 주세요.");
        }
        if (ValidateUtil.loopChar(newPwd)) {
            throw new ValidateException("비밀번호는 연속적인 문자/숫자(예:111,123,abc)가 포함되지 않게 입력해 주세요.");
        }
        if (ValidateUtil.continuosChar(newPwd)) {
            throw new ValidateException("비밀번호는 연속적인 문자/숫자(예:111,123,abc)가 포함되지 않게 입력해 주세요.");
        }
        if (encCurrentPwd != null && encCurrentPwd.equals(newPwd)) {
            throw new ValidateException("변경 전 비밀번호는 사용할 수 없습니다. 새로운 비밀번호를 입력해 주세요.");
        }
    }

    /**
     * Validates a user ID according to the following rules:
     * - Must be 6-20 characters long
     * - Must contain only alphanumeric characters
     * - Must start with an English letter
     *
     * @param loginId the user ID to validate
     * @throws ValidateException with messages:
     *                          - "아이디는 영문, 숫자 조합으로 6~20자로 입력해주세요." if format is invalid
     *                          - "아이디는 영문으로 시작해 주세요." if doesn't start with a letter
     */
    public static void userId(String loginId) {
        if (!ValidateUtil.regex(loginId, "^[a-zA-Z0-9]{6,20}$")) {
            throw new ValidateException("아이디는 영문, 숫자 조합으로 6~20자로 입력해주세요.");
        }
        if (!ValidateUtil.regex(loginId, "^[a-zA-Z]")) {
            throw new ValidateException("아이디는 영문으로 시작해 주세요.");
        }
    }

    /**
     * 지정된 객체가 null이 아닌지 검증합니다.
     *
     * @param value 검사할 객체
     * @param msg   유효성 검사 실패 시 표시할 오류 메시지
     * @param cause 오류에 대한 추가 컨텍스트 정보
     * @throws ValidateException 값이 null인 경우 지정된 메시지와 원인과 함께 예외 발생
     */
    public static void notNull(Object value, String msg, Object cause) {
        if (value == null) {
            throw new ValidateException(msg, cause);
        }
    }

    /**
     * Validates that the specified object is not null.
     *
     * @param value the object to check
     * @param msg   the error message if validation fails
     * @throws ValidateException with the specified message if the value is null
     */
    public static void notNull(Object value, String msg) {
        if (value == null) {
            throw new ValidateException(msg);
        }
    }

    /**
     * 지정된 컬렉션이 null이 아니고 비어있지 않은지 검증합니다.
     *
     * @param value 검사할 컬렉션
     * @param msg   유효성 검사 실패 시 표시할 오류 메시지
     * @throws ValidateException 컬렉션이 null이거나 비어있는 경우 지정된 메시지와 함께 예외 발생
     */
    public static void notEmpty(Collection<?> value, String msg) {
        if (value == null || value.size() == 0) {
            throw new ValidateException(msg);
        }
    }

    /**
     * Validates that the specified collection is not null and not empty.
     *
     * @param value the collection to check
     * @param msg   the error message if validation fails
     * @param cause additional context information for the error
     * @throws ValidateException with the specified message and cause if the collection is null or empty
     */
    public static void notEmpty(Collection<?> value, String msg, Object cause) {
        if (value == null || value.size() == 0) {
            throw new ValidateException(msg, cause);
        }
    }

    /**
     * Validates that the specified map is not null and not empty.
     *
     * @param value the map to check
     * @param msg   the error message if validation fails
     * @throws ValidateException with the specified message if the map is null or empty
     */
    public static void notEmpty(Map<?, ?> value, String msg) {
        if (value == null || value.size() == 0) {
            throw new ValidateException(msg);
        }
    }

    /**
     * Validates that the specified object is null.
     *
     * @param value the object to check
     * @param msg   the error message if validation fails
     * @throws ValidateException with the specified message if the value is not null
     */
    public static void isNull(Object value, String msg) {
        if (value != null) {
            throw new ValidateException(msg);
        }
    }

    /**
     * 지정된 불리언 조건이 참인지 검증합니다.
     *
     * @param value 검사할 불리언 조건
     * @param msg   유효성 검사 실패 시 표시할 오류 메시지
     * @throws ValidateException 조건이 거짓인 경우 지정된 메시지와 함께 예외 발생
     */
    public static void isTrue(boolean value, String msg) {
        if (!value) {
            throw new ValidateException(msg);
        }
    }

    /**
     * Validates that the specified boolean condition is true.
     *
     * @param value the boolean condition to check
     * @param msg   the error message if validation fails
     * @param cause additional context information for the error
     * @throws ValidateException with the specified message and cause if the condition is false
     */
    public static void isTrue(boolean value, String msg, Object cause) {
        if (!value) {
            throw new ValidateException(msg, cause);
        }
    }

    /**
     * Validates that the specified boolean condition is false.
     *
     * @param value the boolean condition to check
     * @param msg   the error message if validation fails
     * @throws ValidateException with the specified message if the condition is true
     */
    public static void isNotTrue(boolean value, String msg) {
        if (value) {
            throw new ValidateException(msg);
        }
    }

    /**
     * Throws a validation exception with the specified message and cause.
     *
     * @param msg   the error message
     * @param cause additional context information for the error
     * @throws ValidateException always throws with the specified message and cause
     */
    public static void error(String msg, Object cause) {
        throw new ValidateException(msg, cause);
    }

    /**
     * 지정된 문자열이 null이 아니고 공백을 제거한 후에도 비어있지 않은지 검증합니다.
     *
     * @param src 검사할 문자열
     * @param msg 유효성 검사 실패 시 표시할 오류 메시지
     * @throws ValidateException 문자열이 null이거나 공백 제거 후 비어있는 경우 지정된 메시지와 함께 예외 발생
     */
    public static void notEmpty(String src, String msg) {
        if (src == null || src.trim().isEmpty()) {
            throw new ValidateException(msg);
        }
    }

    /**
     * RFC 5322 규격을 준수하는 정규식 패턴을 사용하여 이메일 주소를 검증합니다.
     * 다음과 같은 형식을 지원합니다:
     * - 표준 이메일 형식 (user@domain.com)
     * - IP 도메인 형식 (user@[192.168.1.1])
     * - 따옴표로 묶인 로컬 파트 ("user.name"@domain.com)
     * - 다중 서브도메인 (user@sub1.sub2.domain.com)
     * - 로컬 파트의 특수 문자
     * - 주석 및 공백 처리
     *
     * @param email 검증할 이메일 주소
     * @param msg   유효성 검사 실패 시 표시할 오류 메시지
     * @throws ValidateException 이메일 형식이 유효하지 않은 경우 지정된 메시지와 함께 예외 발생
     */
    public static void email(String email, String msg) {
        String regex = "(?:(?:\\r\\n)?[ \\t])*(?:(?:(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*))*@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*|(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)*\\<(?:(?:\\r\\n)?[ \\t])*(?:@(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*(?:,@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*)*:(?:(?:\\r\\n)?[ \\t])*)?(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*))*@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*\\>(?:(?:\\r\\n)?[ \\t])*)|(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)*:(?:(?:\\r\\n)?[ \\t])*(?:(?:(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*))*@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*|(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)*\\<(?:(?:\\r\\n)?[ \\t])*(?:@(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*(?:,@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*)*:(?:(?:\\r\\n)?[ \\t])*)?(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*))*@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*\\>(?:(?:\\r\\n)?[ \\t])*)(?:,\\s*(?:(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*))*@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*|(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)*\\<(?:(?:\\r\\n)?[ \\t])*(?:@(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*(?:,@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*)*:(?:(?:\\r\\n)?[ \\t])*)?(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*))*@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*\\>(?:(?:\\r\\n)?[ \\t])*))*)?;\\s*)";
        if (!ValidateUtil.regex(email, regex)) {
            throw new ValidateException(msg);
        }
    }

    /**
     * Validates that the specified collection is empty or null.
     * This is the opposite of notEmpty validation.
     *
     * @param collection the collection to check
     * @param msg       the error message if validation fails
     * @throws ValidateException with the specified message if the collection is not empty
     */
    public static void isEmpty(Collection<?> collection, String msg) {
        if (collection != null && collection.size() > 0) {
            throw new ValidateException(msg);
        }
    }

    /**
     * 유효성 검사 실패에 대한 사용자 정의 런타임 예외입니다.
     * ValidateUtil의 유효성 검사 규칙이 위반될 때 발생합니다.
     * RuntimeException을 상속하며 선택적으로 원인을 문자열로 포함할 수 있습니다.
     */
    @Getter
    public static class ValidateException extends RuntimeException {

        private static final long serialVersionUID = -594115403693602549L;

        /**
         * 유효성 검사 실패의 원인에 대한 추가 컨텍스트 정보
         */
        private String strCause = null;

        /**
         * 지정된 오류 메시지로 새로운 유효성 검사 예외를 생성합니다.
         *
         * @param msg 유효성 검사 실패를 설명하는 오류 메시지
         */
        public ValidateException(String msg) {
            super(msg);
        }

        /**
         * 지정된 오류 메시지와 원인으로 새로운 유효성 검사 예외를 생성합니다.
         *
         * @param msg   유효성 검사 실패를 설명하는 오류 메시지
         * @param cause 유효성 검사 실패의 원인에 대한 추가 컨텍스트 정보
         */
        public ValidateException(String msg, Object cause) {
            super(msg);
            this.strCause = String.valueOf(cause);
        }

    }
}