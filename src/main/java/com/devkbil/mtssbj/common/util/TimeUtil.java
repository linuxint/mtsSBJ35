package com.devkbil.mtssbj.common.util;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TimeUtil {

    private static long startTime;
    private static long endTime;

    public void setStartTime() {
        startTime = System.currentTimeMillis();
    }

    public void setEndTime() {
        endTime = System.currentTimeMillis();
    }

    public long getDiffTime() {
        return endTime - startTime;
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
     *
     * 두 시각과의 차이를 구한다.
     *
     * @param fromTime String - 시작시각
     * @param toTime String - 종료시각
     * @return 시간차이
     */
    public long diffTime(String fromTime, String toTime) {

        long lDiff;

        try {
            long lFromTime = DateUtil.convDate(fromTime).getTime() / 1000;

            long lToTime = DateUtil.convDate(toTime).getTime() / 1000;

            lDiff = lToTime - lFromTime;

            return lDiff;

        } catch (Exception e) {
            return 0;
        }
    }

}
