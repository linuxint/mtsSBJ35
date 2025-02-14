package com.devkbil.mtssbj.common.util;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TimeUtil {

    private static long startTime;
    private static long endTime;
    private static long diffTime;

    public void setStartTime() {
        startTime = System.currentTimeMillis();
    }

    public void setEndTime() {
        endTime = System.currentTimeMillis();
    }

    public long getDiffTime() {
        diffTime = endTime - startTime;
        return diffTime;
    }

    public String formatTime(long lTime) {
        return String.format("%02d:%02d:%02d.%03d",
            (lTime / (1000 * 60 * 60)) % 24,
            (lTime / (1000 * 60)) % 60,
            (lTime / 1000) % 60,
            lTime % 1000);
    }

    /**
     * diffTime
     * <p>
     * 두 시각과의 차이를 구한다.
     *
     * @return 시간차이
     * @throws
     * @praam fromTime String - 시작시각
     * @praam toTime String - 종료시각
     */
    public long diffTime(String fromTime, String toTime) {

        long lDiff = 0;

        try {
            long lFromTime = DateUtil.convDate(fromTime).getTime() / 1000;

            long lToTime = DateUtil.convDate(toTime).getTime() / 1000;

            lDiff = lToTime - lFromTime;
        } catch (Exception e) {
        }

        return lDiff;
    }

}
