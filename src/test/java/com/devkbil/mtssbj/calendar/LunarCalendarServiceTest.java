package com.devkbil.mtssbj.calendar;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

public class LunarCalendarServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private LunarCalendarService lunarCalendarService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        // Set the required properties using ReflectionTestUtils
        ReflectionTestUtils.setField(lunarCalendarService, "apiDomain", "http://apis.data.go.kr/B090041/openapi");
        ReflectionTestUtils.setField(lunarCalendarService, "lunarServicePath", "/service/SpcdeInfoService/getRestDeInfo");
        ReflectionTestUtils.setField(lunarCalendarService, "apiKey", "test-api-key");

        // Mock the RestTemplate response
        // This XML response has no item elements, simulating the issue described
        String mockResponse = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<response>" +
                "  <header>" +
                "    <resultCode>00</resultCode>" +
                "    <resultMsg>NORMAL SERVICE.</resultMsg>" +
                "  </header>" +
                "  <body>" +
                "    <items>" +
                "      <!-- No item elements here -->" +
                "    </items>" +
                "    <numOfRows>10</numOfRows>" +
                "    <pageNo>1</pageNo>" +
                "    <totalCount>0</totalCount>" +
                "  </body>" +
                "</response>";

        when(restTemplate.getForObject(any(), eq(String.class))).thenReturn(mockResponse);
    }

    @Test
    public void testGetLunarDate_withNoItemsInResponse() {
        // Test with the date mentioned in the issue description
        LocalDate testDate = LocalDate.of(2026, 3, 23);

        // Call the service method
        Map<String, String> lunarInfo = lunarCalendarService.getLunarDate(testDate);

        // Print debug information
        System.out.println("[DEBUG_LOG] Lunar info for " + testDate + ": " + lunarInfo);

        // Verify that lunar information is not empty
        assertNotNull(lunarInfo);
        assertFalse(lunarInfo.isEmpty());

        // Verify that all required fields are present
        assertNotNull(lunarInfo.get("lunarYear"));
        assertNotNull(lunarInfo.get("lunarMonth"));
        assertNotNull(lunarInfo.get("lunarDay"));
        assertNotNull(lunarInfo.get("lunarLeap"));
    }
}
