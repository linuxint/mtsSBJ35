package com.devkbil.mtssbj;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

/**
 * JAVA 소수점 곱하기 테스트
 */
@DisplayName("Decimal Multiplication Tests")
public class DecimalMultiplyTest {

    @Test
    @DisplayName("Test decimal multiplication with int values")
    public void testIntegerMultiplication() {
        // Given
        int intValue = 14000;
        int multiplier = 4;
        float floatFactor = 1.1f;
        double doubleFactor = 1.1d;
        BigDecimal expectedResult = new BigDecimal("61600.0");

        // When
        BigDecimal resultWithFloat = new BigDecimal(String.valueOf(intValue))
                .multiply(new BigDecimal(String.valueOf(multiplier)))
                .multiply(new BigDecimal(String.valueOf(floatFactor)));

        BigDecimal resultWithDouble = new BigDecimal(String.valueOf(intValue))
                .multiply(new BigDecimal(String.valueOf(multiplier)))
                .multiply(new BigDecimal(String.valueOf(doubleFactor)));

        // Then
        assertEquals(expectedResult, resultWithFloat, "Float multiplication should give exact result when using String conversion");
        assertEquals(expectedResult, resultWithDouble, "Double multiplication should give exact result when using String conversion");
    }

    @Test
    @DisplayName("Test decimal multiplication with larger numbers")
    public void testLargeNumberMultiplication() {
        // Given
        Integer intValue = 327273;
        Long longMultiplier = 60L;
        float floatFactor = 1.1f;
        double doubleFactor = 1.1d;
        BigDecimal expectedResult = new BigDecimal("21600018.0");

        // When
        BigDecimal resultWithFloat = new BigDecimal(String.valueOf(intValue))
                .multiply(new BigDecimal(String.valueOf(longMultiplier)))
                .multiply(new BigDecimal(String.valueOf(floatFactor)));

        BigDecimal resultWithDouble = new BigDecimal(String.valueOf(intValue))
                .multiply(new BigDecimal(String.valueOf(longMultiplier)))
                .multiply(new BigDecimal(String.valueOf(doubleFactor)));

        // Then
        assertEquals(expectedResult, resultWithFloat, "Float multiplication with large numbers should be precise when using String conversion");
        assertEquals(expectedResult, resultWithDouble, "Double multiplication with large numbers should be precise when using String conversion");
    }

    @Test
    @DisplayName("Test decimal multiplication precision differences")
    public void testPrecisionDifferences() {
        // Given
        int intValue = 14000;
        int multiplier = 4;
        float floatFactor = 1.1f;

        // When
        BigDecimal directBigDecimal = new BigDecimal(intValue)
                .multiply(new BigDecimal(multiplier))
                .multiply(new BigDecimal(floatFactor));

        BigDecimal stringBigDecimal = new BigDecimal(String.valueOf(intValue))
                .multiply(new BigDecimal(String.valueOf(multiplier)))
                .multiply(new BigDecimal(String.valueOf(floatFactor)));

        // Then
        // Verify that direct BigDecimal construction produces different results than String conversion
        assertNotEquals(0, directBigDecimal.compareTo(stringBigDecimal),
            "Direct BigDecimal construction should produce different results than String conversion");
    }
}
