package com.devkbil.mtssbj.common.util;

import com.devkbil.common.util.TimeUtil;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TimeUtilTest {

    @AfterEach
    void cleanup() {
        TimeUtil.cleanup();
    }

    @Test
    void testTimeMeasurement() throws InterruptedException {
        TimeUtil.setStartTime();
        Thread.sleep(100); // Wait for 100ms
        TimeUtil.setEndTime();

        long diff = TimeUtil.getDiffTime();
        assertTrue(diff >= 100, "측정된 시간이 100ms 이상이어야 합니다");
        assertTrue(diff < 150, "측정된 시간이 150ms 미만이어야 합니다");
    }

    @Test
    void testFormatTime() {
        assertEquals("00:00:00.100", TimeUtil.formatTime(100));
        assertEquals("00:00:01.000", TimeUtil.formatTime(1000));
        assertEquals("00:01:00.000", TimeUtil.formatTime(60000));
        assertEquals("01:00:00.000", TimeUtil.formatTime(3600000));
        assertEquals("01:30:45.123", TimeUtil.formatTime(5445123));
    }

    @Test
    void testDiffTimeWithDateTimeFormat() {
        String fromTime = "2023-01-01 10:00:00";
        String toTime = "2023-01-01 10:01:30";

        long diff = TimeUtil.diffTime(fromTime, toTime);
        assertEquals(90, diff, "시간 차이가 90초여야 합니다");
    }

    @Test
    void testDiffTimeWithDateFormat() {
        String fromTime = "2023-01-01";
        String toTime = "2023-01-02";

        long diff = TimeUtil.diffTime(fromTime, toTime);
        assertEquals(86400, diff, "시간 차이가 86400초(24시간)여야 합니다");
    }

    @Test
    void testDiffTimeWithInvalidFormat() {
        String fromTime = "invalid";
        String toTime = "2023-01-01";

        long diff = TimeUtil.diffTime(fromTime, toTime);
        assertEquals(0, diff, "잘못된 형식의 경우 0을 반환해야 합니다");
    }

    @Test
    void testThreadLocalIsolation() throws InterruptedException {
        TimeUtil.setStartTime();
        Thread.sleep(50);

        Thread otherThread = new Thread(
            () -> {
                TimeUtil.setStartTime();
                TimeUtil.setEndTime();
                assertEquals(0L, TimeUtil.getDiffTime(), "다른 스레드의 시간 측정은 독립적이어야 합니다");
            });

        otherThread.start();
        otherThread.join();

        TimeUtil.setEndTime();
        assertTrue(TimeUtil.getDiffTime() >= 50, "메인 스레드의 시간 측정은 영향받지 않아야 합니다");
    }

    @Test
    void testCleanup() {
        TimeUtil.setStartTime();
        TimeUtil.setEndTime();
        assertTrue(TimeUtil.getDiffTime() >= 0);

        TimeUtil.cleanup();
        assertEquals(0, TimeUtil.getDiffTime(), "cleanup 후에는 0을 반환해야 합니다");
    }
}