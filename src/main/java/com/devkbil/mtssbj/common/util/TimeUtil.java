package com.devkbil.mtssbj.common.util;

import lombok.Getter;
import lombok.Setter;

import java.util.Calendar;

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
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(lTime);
        return String.format("%02d:%02d:%02d.%03d", c.get(Calendar.HOUR), c.get(Calendar.MINUTE),
                c.get(Calendar.SECOND), c.get(Calendar.MILLISECOND));
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
