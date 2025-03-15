package com.devkbil.mtssbj;

import com.devkbil.common.util.IfUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("IfUtil Tests")
public class IfUtilTest {

    @Test
    @DisplayName("Test decode method with various argument combinations")
    public void testDecode() {
        // Test with two arguments
        assertEquals("X", IfUtil.decode("A", "X"),
            "Two arguments should return second argument");

        // Test with three arguments
        assertEquals("A", IfUtil.decode("A", "B", "X"),
            "Three arguments should return first argument if not equal to second");

        // Test with four arguments
        assertEquals("Y", IfUtil.decode("A", "B", "X", "Y"),
            "Four arguments should return fourth argument if first != second");

        // Test with five arguments
        assertEquals("Y", IfUtil.decode("A", "B", "X", "A", "Y"),
            "Five arguments should return fifth argument if first matches fourth");

        // Test with null values
        assertEquals("Y", IfUtil.decode(null, "B", "X", null, "Y"),
            "Should handle null values correctly");
    }

    @Test
    @DisplayName("Test contains method with String input")
    public void testContainsString() {
        // Test when substring is present
        assertEquals("OK", IfUtil.contains("ABC", "B", "OK", "NOT OK"),
            "Should return OK when substring is found");

        // Test when substring is not present
        assertEquals("NOT OK", IfUtil.contains("ABC", "D", "OK", "NOT OK"),
            "Should return NOT OK when substring is not found");
    }

    @Test
    @DisplayName("Test contains method with List input")
    public void testContainsList() {
        // Test when element is present in list
        assertEquals("OK", IfUtil.contains(Arrays.asList("A", "B", "C"), "B", "OK", "NOT OK"),
            "Should return OK when element is in list");

        // Test when element is not present in list
        assertEquals("NOT OK", IfUtil.contains(Arrays.asList("A", "B", "C"), "D", "OK", "NOT OK"),
            "Should return NOT OK when element is not in list");
    }

    @Test
    @DisplayName("Test nvl (null value logic) method")
    public void testNvl() {
        // Test with non-null value
        assertEquals("A", IfUtil.nvl("A", "IS NOT NULL"),
            "Should return first argument when it's not null");

        // Test with null value
        assertEquals("IS NULL", IfUtil.nvl(null, "IS NULL"),
            "Should return second argument when first is null");
    }

    @Test
    @DisplayName("Test evl (empty value logic) method")
    public void testEvl() {
        // Test with non-empty value
        assertEquals("A", IfUtil.evl("A", "IS NOT EMPTY"),
            "Should return first argument when it's not empty");

        // Test with empty string
        assertEquals("IS EMPTY", IfUtil.evl("", "IS EMPTY"),
            "Should return second argument when first is empty");

        // Test with null value
        assertEquals("IS EMPTY", IfUtil.evl(null, "IS EMPTY"),
            "Should return second argument when first is null");
    }
}