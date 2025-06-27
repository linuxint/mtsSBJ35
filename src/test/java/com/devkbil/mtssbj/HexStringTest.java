package com.devkbil.mtssbj;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import java.util.Locale;

@DisplayName("Hex String Operation Tests")
public class HexStringTest {

    @Test
    @DisplayName("Test substring extraction from file keys")
    public void testSubstringExtraction() {
        // Given
        String fileKey = "80467011348CC3130923Ok99LhMyCq";
        String fileKey1 = "804660100120221123131333000582";

        // When
        String hexPart = fileKey.substring(7, 14);
        String datePart = fileKey1.substring(10, 18);

        // Then
        assertEquals("1348CC3", hexPart, "Should extract correct hex substring");
        assertEquals("20221123", datePart, "Should extract correct date substring");
    }

    @Test
    @DisplayName("Test hex string to integer conversion")
    public void testHexToInteger() {
        // Given
        String hexString = "1348CC3";

        // When
        int result = Integer.valueOf(hexString, 16);

        // Then
        assertEquals(20221123, result, "Should convert hex string to correct integer value");
    }

    @Test
    @DisplayName("Test integer to hex string conversion")
    public void testIntegerToHex() {
        // Given
        int value = 20221123;

        // When
        String result = Integer.toString(value, 16).toUpperCase(Locale.ROOT);

        // Then
        assertEquals("1348CC3", result, "Should convert integer to correct hex string");
    }

    @Test
    @DisplayName("Test invalid hex string handling")
    public void testInvalidHexString() {
        // Given
        String invalidHex = "GHIJK";  // Contains invalid hex characters

        // Then
        assertThrows(NumberFormatException.class, () -> {
            // When
            Integer.valueOf(invalidHex, 16);
        }, "Should throw NumberFormatException for invalid hex string");
    }

    @Test
    @DisplayName("Test substring bounds")
    public void testSubstringBounds() {
        // Given
        String fileKey = "80467011348CC3130923Ok99LhMyCq";

        // Then
        assertThrows(IndexOutOfBoundsException.class, () -> {
            // When
            fileKey.substring(7, 35);  // Out of bounds
        }, "Should throw IndexOutOfBoundsException for invalid substring bounds");
    }
}
