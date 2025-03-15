package com.devkbil.common.util;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 * 다양한 수학 연산(나눗셈, 곱셈, 뺄셈, 삼각형 계산 등)을 위한 유틸리티 클래스입니다.
 */
public class CalUtil {
    /**
     * 두 숫자의 나눗셈을 수행하고 지정된 소수점 자리수를 포함한 문자열 결과를 반환합니다.
     *
     * @param numerator  나눗셈의 분자. BigDecimal로 변환 가능한 객체로 예상됩니다.
     * @param denominator 나눗셈의 분모. BigDecimal로 변환 가능한 객체로 예상됩니다.
     * @param scale  결과의 소수점 자리수(소수점 오른쪽 자리수). 반올림에 사용됩니다.
     * @return 지정된 소수점 자리수와 {@code RoundingMode.HALF_UP}에 따라 반올림된 나눗셈 결과 문자열
     * @throws ArithmeticException 0으로 나누기가 시도될 경우 예외가 발생합니다.
     * @throws RuntimeException 제공된 분자 또는 분모가 BigDecimal로 변환할 수 없는 경우 예외가 발생합니다.
     */
    public static String divideS(Object numerator, Object denominator, int scale) {
        return newBigDecimal(numerator).divide(newBigDecimal(denominator), scale, RoundingMode.HALF_UP).toPlainString();
    }

    /**
     * 두 숫자의 나눗셈을 수행하고 지정된 소수점 자리수를 포함한 {@code Double} 결과를 반환합니다.
     *
     * @param numerator  나눗셈의 분자. BigDecimal로 변환 가능한 객체로 예상됩니다.
     * @param denominator 나눗셈의 분모. BigDecimal로 변환 가능한 객체로 예상됩니다.
     * @param scale  결과의 소수점 자리수(소수점 오른쪽 자리수). 반올림에 사용됩니다.
     * @return 지정된 소수점 자리수와 {@code RoundingMode.HALF_UP}에 따라 반올림된 나눗셈 결과 {@code Double}
     * @throws ArithmeticException 0으로 나누기가 시도될 경우 예외가 발생합니다.
     * @throws RuntimeException 제공된 분자 또는 분모가 BigDecimal로 변환할 수 없는 경우 예외가 발생합니다.
     */
    public static Double divideD(Object numerator, Object denominator, int scale) {
        return newBigDecimal(numerator).divide(newBigDecimal(denominator), scale, RoundingMode.HALF_UP).doubleValue();
    }

    /**
     * 두 숫자의 나눗셈을 수행하고 반올림({@code RoundingMode.HALF_UP})된 Long 결과를 반환합니다.
     *
     * @param numerator   나눗셈의 분자. BigDecimal로 변환 가능한 객체로 예상됩니다.
     * @param denominator 나눗셈의 분모. BigDecimal로 변환 가능한 객체로 예상됩니다.
     * @return 반올림된 나눗셈 결과 Long
     * @throws ArithmeticException 0으로 나누기가 시도될 경우 예외가 발생합니다.
     * @throws RuntimeException 제공된 분자 또는 분모가 BigDecimal로 변환할 수 없는 경우 예외가 발생합니다.
     */
    public static Long divideL(Object numerator, Object denominator) {
        return newBigDecimal(numerator).divide(newBigDecimal(denominator), 0, RoundingMode.HALF_UP).longValue();
    }

    /**
     * 두 숫자의 나눗셈을 수행하고 반올림({@code RoundingMode.HALF_UP})된 Integer 결과를 반환합니다.
     *
     * @param numerator   나눗셈의 분자. BigDecimal로 변환 가능한 객체로 예상됩니다.
     * @param denominator 나눗셈의 분모. BigDecimal로 변환 가능한 객체로 예상됩니다.
     * @return 반올림된 나눗셈 결과 Integer
     * @throws ArithmeticException 0으로 나누기가 시도될 경우 예외가 발생합니다.
     * @throws RuntimeException 제공된 분자 또는 분모가 BigDecimal로 변환할 수 없는 경우 예외가 발생합니다.
     */
    public static Integer divideI(Object numerator, Object denominator) {
        return newBigDecimal(numerator).divide(newBigDecimal(denominator), 0, RoundingMode.HALF_UP).intValue();
    }

    /**
     * 두 숫자의 나눗셈을 수행하고 {@code RoundingMode.FLOOR}로 반올림된 Integer 결과를 반환합니다.
     *
     * @param numerator   나눗셈의 분자. BigDecimal로 변환 가능한 객체로 예상됩니다.
     * @param denominator 나눗셈의 분모. BigDecimal로 변환 가능한 객체로 예상됩니다.
     * @return 반올림된 나눗셈 결과 Integer
     * @throws ArithmeticException 0으로 나누기가 시도될 경우 예외가 발생합니다.
     * @throws RuntimeException 제공된 분자 또는 분모가 BigDecimal로 변환할 수 없는 경우 예외가 발생합니다.
     */
    public static Integer divideIF(Object numerator, Object denominator) {
        return newBigDecimal(numerator).divide(newBigDecimal(denominator), 0, RoundingMode.FLOOR).intValue();
    }

    /**
     * 두 숫자를 빼고 결과를 {@code Double}로 반환합니다.
     * 지정된 소수점 자리수와 {@code RoundingMode.HALF_UP}로 반올림됩니다.
     *
     * @param numerator   빼기의 첫 번째 숫자(감산수). {@code BigDecimal}로 변환 가능한 {@code Object}로 예상됩니다.
     * @param denominator 빼기의 두 번째 숫자(감할 값). {@code BigDecimal}로 변환 가능한 {@code Object}로 예상됩니다.
     * @param scale       결과의 소수점 자리수(소수점 오른쪽 자리수). 반올림에 사용됩니다.
     * @return 지정된 소수점 자리수와 함께 반올림된 {@code Double} 형태의 빼기 결과
     * @throws RuntimeException 제공된 분자 또는 분모가 {@code BigDecimal}로 변환할 수 없는 경우 예외가 발생합니다.
     */
    public static Double subtractDR(Object numerator, Object denominator, int scale) {
        return newBigDecimal(numerator).subtract(newBigDecimal(denominator), new MathContext(scale, RoundingMode.HALF_UP)).doubleValue();
    }

    /**
     * 두 숫자를 곱하고 결과를 정수(int)로 반환합니다.
     * 입력 매개변수는 {@code BigDecimal}로 변환 가능한 객체여야 합니다.
     *
     * @param numerator   곱할 첫 번째 숫자. {@code BigDecimal}로 변환 가능한 {@code Object}로 예상됩니다.
     * @param denominator 곱할 두 번째 숫자. {@code BigDecimal}로 변환 가능한 {@code Object}로 예상됩니다.
     * @return 두 숫자를 곱한 결과를 정수(int)로 반환
     * @throws RuntimeException 제공된 분자 또는 분모가 {@code BigDecimal}로 변환할 수 없는 경우 예외가 발생합니다.
     */
    public static int multiplyI(Object numerator, Object denominator) {
        return newBigDecimal(numerator).multiply(newBigDecimal(denominator)).intValue();
    }

    /**
     * 주어진 객체를 BigDecimal로 변환합니다.
     * 입력 객체는 String, Long, Integer, Float 또는 Double과 같은 형식이어야 합니다.
     *
     * @param numerator BigDecimal로 변환할 객체. 지원되는 형식은 다음과 같습니다:
     *                  String, Long, Integer, Float, Double.
     * @return 제공된 객체의 BigDecimal 표현.
     * @throws RuntimeException 제공된 객체 형식이 지원되지 않는 경우 예외가 발생합니다.
     */
    public static BigDecimal newBigDecimal(Object numerator) {
        if (numerator instanceof String) {
            return new BigDecimal((String)numerator);
        }
        if (numerator instanceof Long) {
            return new BigDecimal((Long)numerator);
        }
        if (numerator instanceof Integer) {
            return new BigDecimal((Integer)numerator);
        }
        if (numerator instanceof Float) {
            return BigDecimal.valueOf((Float)numerator);
        }
        if (numerator instanceof Double) {
            return BigDecimal.valueOf((Double)numerator);
        }
        throw new RuntimeException(numerator.getClass().getName() + " 형식은 지원되지 않습니다.");
    }

    /**
     * 주어진 경사와 빗변 길이를 이용해 직각 삼각형의 밑변 길이를 계산합니다.
     *
     * @param slope      삼각형의 경사
     * @param hypotenuse 삼각형의 빗변 길이
     * @return 계산된 삼각형의 밑변 길이 ({@code Double} 형식)
     */
    public static Double getTriangleBaseWidth(Double slope, Double hypotenuse) {
        return Math.abs(CalUtil.divideD(hypotenuse, Math.sqrt(Math.pow(slope, 2) + 1), 8));
    }

    /**
     * 주어진 경사와 빗변 길이를 이용해 직각 삼각형의 높이를 계산합니다.
     *
     * @param slope      삼각형의 경사
     * @param hypotenuse 삼각형의 빗변 길이
     * @return 계산된 삼각형의 높이 ({@code Double} 형식)
     */
    public static Double getTriangleHeight(Double slope, Double hypotenuse) {
        return Math.abs(CalUtil.divideD(hypotenuse * slope, Math.sqrt(Math.pow(slope, 2) + 1), 8));
    }

}