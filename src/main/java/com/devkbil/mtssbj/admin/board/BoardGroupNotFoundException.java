package com.devkbil.mtssbj.admin.board;

/**
 * 지정된 게시판 그룹을 찾을 수 없을 때 발생하는 예외.
 *
 * 이 예외는 존재하지 않거나 유효하지 않은 그룹 ID가 제공될 때
 * 게시판 그룹 관련 작업에서 일반적으로 사용됩니다.
 */
public class BoardGroupNotFoundException extends RuntimeException {

    /**
     * 지정된 상세 메시지로 새로운 BoardGroupNotFoundException을 생성합니다.
     *
     * @param message 예외 원인에 대한 자세한 정보를 제공하는 상세 메시지
     */
    public BoardGroupNotFoundException(String message) {
        super(message);
    }
}
