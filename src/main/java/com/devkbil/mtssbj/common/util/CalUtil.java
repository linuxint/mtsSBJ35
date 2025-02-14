package com.devkbil.mtssbj.common.util;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class CalUtil {
    public static String divideS(Object numerator, Object denominator, int scale) {
        return newBigDecimal(numerator).divide(newBigDecimal(denominator), scale, RoundingMode.HALF_UP).toPlainString();
    }

    public static Double divideD(Object numerator, Object denominator, int scale) {
        return newBigDecimal(numerator).divide(newBigDecimal(denominator), scale, RoundingMode.HALF_UP).doubleValue();
    }

    public static Long divideL(Object numerator, Object denominator) {
        return newBigDecimal(numerator).divide(newBigDecimal(denominator), 0, RoundingMode.HALF_UP).longValue();
    }

    public static Integer divideI(Object numerator, Object denominator) {
        return newBigDecimal(numerator).divide(newBigDecimal(denominator), 0, RoundingMode.HALF_UP).intValue();
    }

    public static Integer divideIF(Object numerator, Object denominator) {
        return newBigDecimal(numerator).divide(newBigDecimal(denominator), 0, RoundingMode.FLOOR).intValue();
    }

    public static Double subtractDR(Object numerator, Object denominator, int scale) {
        return newBigDecimal(numerator).subtract(newBigDecimal(denominator), new MathContext(scale, RoundingMode.HALF_UP)).doubleValue();
    }

    public static int multiplyI(Object numerator, Object denominator) {
        return newBigDecimal(numerator).multiply(newBigDecimal(denominator)).intValue();
    }

    public static BigDecimal newBigDecimal(Object numerator) {
        if (numerator instanceof String) {
            return new BigDecimal((String) numerator);
        }
        if (numerator instanceof Long) {
            return new BigDecimal((Long) numerator);
        }
        if (numerator instanceof Integer) {
            return new BigDecimal((Integer) numerator);
        }
        if (numerator instanceof Float) {
            return BigDecimal.valueOf((Float) numerator);
        }
        if (numerator instanceof Double) {
            return BigDecimal.valueOf((Double) numerator);
        }
        throw new RuntimeException(numerator.getClass().getName() + " is not supported.");
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
