package com.devkbil.mtssbj.common.util;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class CalUtil {
    public static String divideS(Object a, Object b, int scale) {
        return newBigDecimal(a).divide(newBigDecimal(b), scale, RoundingMode.HALF_UP).toPlainString();
    }

    public static Double divideD(Object a, Object b, int scale) {
        return newBigDecimal(a).divide(newBigDecimal(b), scale, RoundingMode.HALF_UP).doubleValue();
    }

    public static Long divideL(Object a, Object b) {
        return newBigDecimal(a).divide(newBigDecimal(b), 0, RoundingMode.HALF_UP).longValue();
    }

    public static Integer divideI(Object a, Object b) {
        return newBigDecimal(a).divide(newBigDecimal(b), 0, RoundingMode.HALF_UP).intValue();
    }

    public static Integer divideIF(Object a, Object b) {
        return newBigDecimal(a).divide(newBigDecimal(b), 0, RoundingMode.FLOOR).intValue();
    }

    public static Double subtractDR(Object a, Object b, int scale) {
        return newBigDecimal(a).subtract(newBigDecimal(b), new MathContext(scale, RoundingMode.HALF_UP)).doubleValue();
    }

    public static int multiplyI(Object a, Object b) {
        return newBigDecimal(a).multiply(newBigDecimal(b)).intValue();
    }

    public static BigDecimal newBigDecimal(Object a) {
        if (a instanceof String) {
            return new BigDecimal((String) a);
        }
        if (a instanceof Long) {
            return new BigDecimal((Long) a);
        }
        if (a instanceof Integer) {
            return new BigDecimal((Integer) a);
        }
        if (a instanceof Float) {
            return BigDecimal.valueOf((Float) a);
        }
        if (a instanceof Double) {
            return BigDecimal.valueOf((Double) a);
        }
        throw new RuntimeException(a.getClass().getName() + " is not supported.");
    }

    /**
     * 직각 삼각형 아랫변 길이 구하기
     *
     * @param slope      기울기
     * @param hypotenuse 빗변길이
     * @return
     */
    public static Double getTriangleBaseWidth(Double slope, Double hypotenuse) {
        return Math.abs(CalUtil.divideD(hypotenuse, Math.sqrt(Math.pow(slope, 2) + 1), 8));
    }

    /**
     * 직각 삼각형 높이 길이 구하기
     *
     * @param slope      기울기
     * @param hypotenuse 빗변길이
     * @return
     */
    public static Double getTriangleHeight(Double slope, Double hypotenuse) {
        return Math.abs(CalUtil.divideD(hypotenuse * slope, Math.sqrt(Math.pow(slope, 2) + 1), 8));
    }

}
