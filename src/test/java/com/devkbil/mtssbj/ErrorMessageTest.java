package com.devkbil.mtssbj;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.validation.DefaultMessageCodesResolver;
import org.springframework.validation.MessageCodesResolver;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Error Message Resolution Tests")
public class ErrorMessageTest {

    private MessageCodesResolver codesResolver;

    @BeforeEach
    void setUp() {
        codesResolver = new DefaultMessageCodesResolver();
    }

    @Test
    @DisplayName("Test default message codes resolution for Min constraint")
    public void testDefaultMessageCodesForMin() {
        // Given
        String errorCode = "Min";
        String objectName = "productRequest";
        String field = "price";
        Class<?> fieldType = int.class;

        // When
        String[] codes = codesResolver.resolveMessageCodes(errorCode, objectName, field, fieldType);

        // Then
        String[] expectedCodes = {
            "Min.productRequest.price",
            "Min.price",
            "Min.int",
            "Min"
        };
        assertArrayEquals(expectedCodes, codes, "Message codes should be resolved in correct order");
    }

    @Test
    @DisplayName("Test message codes resolution for NotNull constraint")
    public void testMessageCodesForNotNull() {
        // Given
        String errorCode = "NotNull";
        String objectName = "userForm";
        String field = "username";
        Class<?> fieldType = String.class;

        // When
        String[] codes = codesResolver.resolveMessageCodes(errorCode, objectName, field, fieldType);

        // Then
        String[] expectedCodes = {
            "NotNull.userForm.username",
            "NotNull.username",
            "NotNull.java.lang.String",
            "NotNull"
        };
        assertArrayEquals(expectedCodes, codes, "NotNull message codes should be resolved in correct order");
    }

    @Test
    @DisplayName("Test message codes resolution with nested field path")
    public void testMessageCodesWithNestedField() {
        // Given
        String errorCode = "NotEmpty";
        String objectName = "orderForm";
        String field = "customer.address.street";
        Class<?> fieldType = String.class;

        // When
        String[] codes = codesResolver.resolveMessageCodes(errorCode, objectName, field, fieldType);

        // Then
        String[] expectedCodes = {
            "NotEmpty.orderForm.customer.address.street",
            "NotEmpty.customer.address.street",
            "NotEmpty.street",
            "NotEmpty.java.lang.String",
            "NotEmpty"
        };
        assertArrayEquals(expectedCodes, codes, "Nested field message codes should be resolved correctly");
    }
}