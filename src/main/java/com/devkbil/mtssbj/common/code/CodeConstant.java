package com.devkbil.mtssbj.common.code;

import java.util.Arrays;

import lombok.Getter;

/**
 * CodeConstant는 코드 관리와 관련된 상수 및 유틸리티 메소드를 정의하는 유틸리티 클래스입니다.
 * `CodeOption` 및 `CodeGroup` 열거형을 포함하여 사용자 역할, 문서 상태, 근무 조건 및
 * 시스템의 기타 속성을 분류하고 그룹화합니다.
 */
public class CodeConstant {

    /**
     * CodeOption은 다양한 코드 또는 상태 유형을 나타내는 열거형입니다.
     * 각 열거형 상수는 `viewName` 필드에 정의된 표시 이름 또는 라벨과 연결됩니다.
     * <p>
     * 사용자 역할, 처리 상태, 작업 유형, 반복 단위 및 공개 상태와 같은
     * 특정 카테고리 또는 상태를 표준화하는 데 이 열거형을 사용하십시오.
     * <p>
     * 열거형 상수:
     * - ADMIN: 관리자 사용자(e.g., "관리자").
     * - USER: 일반 사용자(e.g., "사용자").
     * - SAVE: 임시 저장 상태(e.g., "임시저장").
     * - DELAY: 대기 상태(e.g., "대기중").
     * - CONTINUE: 진행 중인 심사 상태(e.g., "심사중").
     * - REJECT: 반려 상태(e.g., "반려").
     * - COMPLETE: 결재 완료 상태(e.g., "결재 완료").
     * - WORK: 업무와 관련된 상태(e.g., "업무").
     * - MEET: 회의와 관련된 상태(e.g., "회의").
     * - NOREPEAT: 반복 없음(e.g., "반복없음").
     * - WREPEAT: 주 단위 반복(e.g., "주단위").
     * - MREPEAT: 월 단위 반복(e.g., "월단위").
     * - OPEN: 공개 상태(e.g., "공개").
     * - CLOSE: 비공개 상태(e.g., "비공개").
     */
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

        /**
         * 지정된 viewName으로 CodeOption 열거형의 새 인스턴스를 생성합니다.
         *
         * @param viewName 이 CodeOption과 관련된 로컬화된 이름 또는 라벨
         */
        CodeOption(String viewName) {
            this.viewName = viewName;
        }

    }

    /**
     * CodeGroup 열거형은 관련 `CodeOption` 값 그룹을 분류하며,
     * 코드를 해당 속성에 따라 분류하고 검색하는 구조화된 방식을 제공합니다.
     * <p>
     * 각 CodeGroup 카테고리는 설명 이름(viewName)과 해당 멤버를 정의하는 `CodeOption` 세트를 가집니다.
     * 이를 사용하여 사용자 역할, 문서 상태, 근무 조건 등과 같은 다양한 시스템 코드를 관리하고 조직화할 수 있습니다.
     * <p>
     * 열거형 상수:
     * - CodeGroup 열거형의 각 상수는 로컬화된 이름과 연관된 `CodeOption` 값 세트를 나타내는 카테고리를 나타냅니다.
     * - 예로는 사용자 역할(USER), 문서 결제 상태(SIGN), 근무 상태(WORK) 등이 포함됩니다.
     * <p>
     * 기능적 동작:
     * - `findGroup` 메서드는 특정 `CodeOption`을 포함하는 CodeGroup 인스턴스를 검색합니다.
     * - `hasCodeOption` 비공개 메서드는 특정 `CodeOption`이 CodeGroup에 존재하는지 여부를 테스트합니다.
     * <p>
     * 사용 예:
     * - 시스템 코드를 일관되고 중앙 집중식으로 관리하는 데 유용합니다.
     * - 시스템 내에서 다양한 작업을 수행하기 위해 코드 검색 및 분류에 유틸리티 메서드를 제공합니다.
     * <p>
     * 스레드:
     * - 이 열거형은 변경 불가능하므로 스레드에 안전합니다.
     */
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

        /**
         * 지정된 설명 이름 및 연관된 `CodeOption` 값 배열로 `CodeGroup` 열거형의 새 인스턴스를 생성합니다.
         *
         * @param viewName 이 `CodeGroup`과 연관된 설명적 또는 로컬화된 이름
         * @param containCode 이 `CodeGroup`에 속하는 `CodeOption` 값 배열
         */
        CodeGroup(String viewName, CodeOption[] containCode) {
            this.viewName = viewName;
            this.containCode = containCode;
        }

        /**
         * 지정된 CodeOption과 일치하는 CodeGroup을 검색하여 반환합니다.
         * <p>
         * 이 메서드는 제공된 CodeOption이 어느 그룹에도 속하는지
         * 확인하기 위해 사용 가능한 모든 CodeGroup 값을 검색합니다.
         * 일치 항목이 없을 경우 기본값으로 CodeGroup.EMPTY를 반환합니다.
         *
         * @param searchTarget 검색할 CodeOption
         * @return 일치하는 CodeGroup(찾을 경우); 그렇지 않으면 CodeGroup.EMPTY 반환
         */
        public static CodeGroup findGroup(CodeOption searchTarget) {
            return Arrays.stream(CodeGroup.values())
                .filter(group -> hasCodeOption(group, searchTarget))
                .findAny()
                .orElse(CodeGroup.EMPTY);
        }

        /**
         * 주어진 {@code CodeOption}이 {@code CodeGroup}의 코드 옵션에 존재하는지 확인합니다.
         *
         * @param from {@code CodeOption} 값을 포함하는 {@code CodeGroup} 인스턴스
         * @param searchTarget {@code CodeGroup} 내에서 검색할 {@code CodeOption} 값
         * @return {@code searchTarget}이 {@code from} {@code CodeGroup} 내에 존재하면 {@code true};
         *         그렇지 않으면 {@code false}.
         */
        private static boolean hasCodeOption(CodeGroup from, CodeOption searchTarget) {
            return Arrays.stream(from.containCode)
                .anyMatch(containCode -> containCode == searchTarget);
        }

    }
}
