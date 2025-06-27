package com.devkbil.mtssbj;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.util.StringUtils;
import com.google.common.base.Splitter;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("String Utility Tests")
public class StringUtilTest {

    @Test
    @DisplayName("Test hasText with various string inputs")
    public void testHasText() {
        // Given
        String emptyString = "";
        String spaceString = " ";
        String nullString = null;
        String normalString = "text";

        // When & Then
        assertFalse(StringUtils.hasText(emptyString), "Empty string should not have text");
        assertFalse(StringUtils.hasText(spaceString), "Space-only string should not have text");
        assertFalse(StringUtils.hasText(nullString), "Null string should not have text");
        assertTrue(StringUtils.hasText(normalString), "Normal string should have text");
    }

    @Test
    @DisplayName("Test string split functionality")
    public void testStringSplit() {
        // Given
        String input = "011,008,019,020,040,025,037,038";

        // When
        java.util.List<String> elements = Splitter.on(',').splitToList(input);

        // Then
        assertTrue(elements.size() == 8, "Split should produce 8 elements");
        assertTrue(elements.get(0).equals("011"), "First element should be '011'");
        assertTrue(elements.get(7).equals("038"), "Last element should be '038'");
    }
}