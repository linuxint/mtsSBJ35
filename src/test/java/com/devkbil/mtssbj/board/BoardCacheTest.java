package com.devkbil.mtssbj.board;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class BoardCacheTest {

    @Autowired
    private BoardService boardService;

    @Autowired
    private CacheManager cacheManager;

    @Test
    void testBoardListCaching() {
        // Given
        BoardSearchVO searchVO = new BoardSearchVO();
        searchVO.setBgno("1");
        searchVO.setRowStart(1);

        // When - First call (cache miss)
        var firstResult = boardService.selectBoardList(searchVO);

        // Then - Verify cache entry
        CaffeineCache cache = (CaffeineCache)cacheManager.getCache("boardList");
        assertNotNull(cache);

        String cacheKey = "1__1"; // matches the key pattern in @Cacheable
        assertNotNull(cache.get(cacheKey));

        // When - Second call (cache hit)
        var secondResult = boardService.selectBoardList(searchVO);

        // Then - Verify results are the same
        assertEquals(firstResult, secondResult);

        System.out.println(
            "[DEBUG_LOG] Cache stats for boardList: "
                + ((com.github.benmanes.caffeine.cache.Cache<?, ?>)cache.getNativeCache()).stats());
    }

    @Test
    void testNoticeListCaching() {
        // Given
        BoardSearchVO searchVO = new BoardSearchVO();
        searchVO.setBgno("1");

        // When - First call (cache miss)
        var firstResult = boardService.selectNoticeList(searchVO);

        // Then - Verify cache entry
        CaffeineCache cache = (CaffeineCache)cacheManager.getCache("boardNotices");
        assertNotNull(cache);

        String cacheKey = "1"; // matches the key pattern in @Cacheable
        assertNotNull(cache.get(cacheKey));

        // When - Second call (cache hit)
        var secondResult = boardService.selectNoticeList(searchVO);

        // Then - Verify results are the same
        assertEquals(firstResult, secondResult);

        System.out.println(
            "[DEBUG_LOG] Cache stats for boardNotices: "
                + ((com.github.benmanes.caffeine.cache.Cache<?, ?>)cache.getNativeCache()).stats());
    }

    @Test
    void testCacheEviction() {
        // Given
        BoardSearchVO searchVO = new BoardSearchVO();
        searchVO.setBgno("1");

        // When - Cache the notice list
        boardService.selectNoticeList(searchVO);

        // Then - Verify cache has entry
        CaffeineCache noticeCache = (CaffeineCache)cacheManager.getCache("boardNotices");
        assertNotNull(noticeCache.get("1"));

        // When - Insert new board (should evict cache)
        BoardVO newBoard = new BoardVO();
        newBoard.setBgno("1");
        newBoard.setBrdtitle("Test Board");
        newBoard.setUserno("1");
        boardService.insertBoard(newBoard, null, null);

        // Then - Cache should be evicted
        assertNull(noticeCache.get("1"));

        System.out.println("[DEBUG_LOG] Cache eviction test completed");
    }
}
