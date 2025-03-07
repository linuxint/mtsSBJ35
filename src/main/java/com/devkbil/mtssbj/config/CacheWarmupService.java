package com.devkbil.mtssbj.config;

import com.devkbil.mtssbj.board.BoardSearchVO;
import com.devkbil.mtssbj.board.BoardService;

import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 애플리케이션 시작 시 캐시를 미리 로드하는 서비스
 * 자주 접근하는 데이터를 미리 로드하여 초기 응답 시간을 개선합니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CacheWarmupService {

    private final BoardService boardService;

    /**
     * 애플리케이션 시작 시 특정 캐시를 미리 로드합니다.
     * 이 메서드는 자주 사용하는 데이터를 애플리케이션 시작 시 캐시에 로드하여
     * 애플리케이션 성능을 향상시키는 데 목적이 있습니다.
     * <p>
     * 기능:
     * - 공지사항 목록을 캐시에 로드합니다.
     * - 사전에 정의된 일반 게시판 그룹 ID를 순회하며 각 그룹 게시판
     * 첫 번째 페이지를 캐시에 로드합니다.
     * <p>
     * 예외 처리:
     * - 캐시 로드 중 문제 발생 시 오류 메시지를 로깅합니다.
     * <p>
     * 로깅:
     * - 캐시 로드 프로세스의 시작과 성공적인 완료를 포함해 각 단계를 로깅합니다.
     * - 처리된 각 게시판 그룹의 세부 정보를 로깅합니다.
     * <p>
     * 호출:
     * - ApplicationStartedEvent 동안 자동으로 호출됩니다.
     */
    @EventListener(ApplicationStartedEvent.class)
    public void warmupCaches() {
        log.info("캐시 로드를 시작합니다...");

        try {
            // 공지사항 목록 캐시 로드
            BoardSearchVO searchVO = new BoardSearchVO();
            boardService.selectNoticeList(searchVO);
            log.info("공지사항 목록 캐시 로드 완료");

            // 각 일반 게시판 그룹의 첫 번째 페이지에 대한 목록 캐시 로드
            String[] commonBoardGroups = {"1", "2", "3"}; // 일반 게시판 그룹 ID를 추가하세요
            for (String bgno : commonBoardGroups) {
                searchVO = new BoardSearchVO();
                searchVO.setBgno(bgno);
                searchVO.setRowStart(1);
                boardService.selectBoardList(searchVO);
                log.info("게시판 목록 캐시 로드 완료: 그룹 {}", bgno);
            }

            log.info("캐시 로드를 성공적으로 완료했습니다.");
        } catch (Exception e) {
            log.error("캐시 로드 중 오류 발생", e);
        }
    }
}