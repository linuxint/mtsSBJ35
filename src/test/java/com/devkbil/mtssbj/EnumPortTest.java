package com.devkbil.mtssbj;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("EnumPort Tests")
public class EnumPortTest {

    @Test
    @DisplayName("Test valid business code for DEP port")
    public void testValidDepPort() {
        // Given
        String depCode = "02";
        int expectedPort = 18912;  // DEP port number

        // When
        int actualPort = EnumPort.getBwcdOfPort(depCode);

        // Then
        assertEquals(expectedPort, actualPort, "DEP business code should return correct port number");
    }

    @Test
    @DisplayName("Test valid business code for CARD port")
    public void testValidCardPort() {
        // Given
        String cardCode = "04";
        int expectedPort = 18911;  // CARD port number

        // When
        int actualPort = EnumPort.getBwcdOfPort(cardCode);

        // Then
        assertEquals(expectedPort, actualPort, "CARD business code should return correct port number");
    }

    @Test
    @DisplayName("Test invalid business code")
    public void testInvalidBusinessCode() {
        // Given
        String invalidCode = "99";
        int expectedPort = 0;  // Default return value for invalid code

        // When
        int actualPort = EnumPort.getBwcdOfPort(invalidCode);

        // Then
        assertEquals(expectedPort, actualPort, "Invalid business code should return 0");
    }

    @Test
    @DisplayName("Test string comparison behavior")
    public void testStringComparisonBehavior() {
        // Given
        String depCode = new String("02");  // Creating new String object to test == behavior
        int expectedPort = 18912;  // DEP port number

        // When
        int actualPort = EnumPort.getBwcdOfPort(depCode);

        // Then
        assertEquals(expectedPort, actualPort, "String comparison should work with different String objects");
    }

    @Test
    @DisplayName("Test null business code")
    public void testNullBusinessCode() {
        // Given
        String nullCode = null;
        int expectedPort = 0;  // Default return value for null input

        // When
        int actualPort = EnumPort.getBwcdOfPort(nullCode);

        // Then
        assertEquals(expectedPort, actualPort, "Null business code should return 0");
    }
}
