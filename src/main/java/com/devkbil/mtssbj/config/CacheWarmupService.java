package com.devkbil.mtssbj.config;

import com.devkbil.mtssbj.board.BoardSearchVO;
import com.devkbil.mtssbj.board.BoardService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

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
     * Preloads specific application caches during the ApplicationStartedEvent.
     * This method is designed to enhance application performance by ensuring that
     * frequently accessed data is loaded into cache upon application startup.
     *
     * Functionality:
     * - Loads the list of notices into the cache.
     * - Iterates through predefined common board group IDs and loads the first page 
     *   of the board list for each group into the cache.
     *
     * Exception Handling:
     * - Logs an error message if any issue occurs during the cache loading process.
     *
     * Logging:
     * - Logs each step of the process, including the start and successful completion
     *   of cache loading operations.
     * - Logs details for each board group processed.
     *
     * Invocation:
     * - Automatically invoked during the ApplicationStartedEvent.
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