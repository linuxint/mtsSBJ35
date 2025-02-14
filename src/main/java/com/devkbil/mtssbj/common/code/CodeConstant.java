package com.devkbil.mtssbj.common.code;

import lombok.Getter;

import java.util.Arrays;

public class CodeConstant {

    @Getter
    public enum CodeOption {
        ADMIN("관리자"),
        USER("사용자"),
        SAVE("임시저장"),
        DELAY("대기중"),
        CONTINUE("심사중"),
        REJECT("반려"),
        COMPLETE("결재 완료"),

        // 직책구분
        /// 선임, 주임, 대리, 과장, 차장, 부장, 이사, 상무, 전무, 부사장, 사장, 부회장, 회장
        //근무상태
        /// 업무 회의 외근 출장 교육 휴가 기타
        WORK("업무"),
        MEET("회의"),
        //반복단위
        /// 반복없음 주단위 월단위
        NOREPEAT("반복없음"),
        WREPEAT("주단위"),
        MREPEAT("월단위"),
        // 공개여부
        OPEN("공개"),
        CLOSE("비공개");

        private final String viewName;

        CodeOption(String viewName) {
            this.viewName = viewName;
        }

    }

    //CODEGROUP
    //User, Sign,Posion, Work, Repeat, Open
    @Getter
    public enum CodeGroup {
        USER("사용자롤", new CodeOption[]{
            CodeOption.ADMIN, CodeOption.USER
        }),
        SIGN("문서결제상태", new CodeOption[]{
            CodeOption.SAVE, CodeOption.DELAY, CodeOption.CONTINUE, CodeOption.REJECT, CodeOption.COMPLETE
        }),
        POSITION("직책구", new CodeOption[]{
            //CodeOption.SAVE, CodeOption.DELAY, CodeOption.CONTINUE, CodeOption.REJECT, CodeOption.COMPLETE
        }),
        WORK("근무상태", new CodeOption[]{
            CodeOption.WORK, CodeOption.MEET
        }),
        REPEAT("반복단위", new CodeOption[]{
            CodeOption.NOREPEAT, CodeOption.WREPEAT, CodeOption.MREPEAT
        }),

        OPEN("공개여부", new CodeOption[]{
            CodeOption.OPEN, CodeOption.CLOSE
        }),
        EMPTY("없음", new CodeOption[]{});

        private final String viewName;
        private final CodeOption[] containCode;

        CodeGroup(String viewName, CodeOption[] containCode) {
            this.viewName = viewName;
            this.containCode = containCode;
        }

        public static CodeGroup findGroup(CodeOption searchTarget) {
            return Arrays.stream(CodeGroup.values())
                .filter(group -> hasCodeOption(group, searchTarget))
                .findAny()
                .orElse(CodeGroup.EMPTY);
        }

        private static boolean hasCodeOption(CodeGroup from, CodeOption searchTarget) {
            return Arrays.stream(from.containCode)
                .anyMatch(containCode -> containCode == searchTarget);
        }

    }
}
