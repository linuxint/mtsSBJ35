package com.devkbil.mtssbj.common.util;

import com.devkbil.mtssbj.schedule.DateVO;
import com.devkbil.mtssbj.schedule.MonthVO;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class DateUtilTest {

    @Test
    void testGetCurrentDate() {
        LocalDate result = DateUtil.getCurrentDate();
        assertNotNull(result);
        assertEquals(LocalDate.now(), result);
    }

    @Test
    void testGetCurrentDateTime() {
        LocalDateTime result = DateUtil.getCurrentDateTime();
        assertNotNull(result);
        assertTrue(LocalDateTime.now().minusSeconds(1).isBefore(result));
        assertTrue(LocalDateTime.now().plusSeconds(1).isAfter(result));
    }

    @Test
    void testParseDate() {
        LocalDate date = DateUtil.parseDate("2023-12-25");
        assertNotNull(date);
        assertEquals(2023, date.getYear());
        assertEquals(12, date.getMonthValue());
        assertEquals(25, date.getDayOfMonth());

        // Invalid format
        assertNull(DateUtil.parseDate("invalid-date"));
    }

    @Test
    void testParseDateTime() {
        LocalDateTime dateTime = DateUtil.parseDateTime("2023-12-25 15:30:45");
        assertNotNull(dateTime);
        assertEquals(2023, dateTime.getYear());
        assertEquals(12, dateTime.getMonthValue());
        assertEquals(25, dateTime.getDayOfMonth());
        assertEquals(15, dateTime.getHour());
        assertEquals(30, dateTime.getMinute());
        assertEquals(45, dateTime.getSecond());

        // Invalid format
        assertNull(DateUtil.parseDateTime("invalid-datetime"));
    }

    @Test
    void testFormatDate() {
        LocalDate date = LocalDate.of(2023, 12, 25);
        assertEquals("2023-12-25", DateUtil.formatDate(date));
        assertEquals("20231225", DateUtil.formatDate(date, "yyyyMMdd"));

        // Null handling
        assertNull(DateUtil.formatDate(null));
    }

    @Test
    void testFormatDateTime() {
        LocalDateTime dateTime = LocalDateTime.of(2023, 12, 25, 15, 30, 45);
        assertEquals("2023-12-25 15:30:45", DateUtil.formatDateTime(dateTime));
        assertEquals("20231225153045", DateUtil.formatDateTime(dateTime, "yyyyMMddHHmmss"));

        // Null handling
        assertNull(DateUtil.formatDateTime(null));
    }

    @Test
    void testDaysBetween() {
        LocalDate date1 = LocalDate.of(2023, 12, 25);
        LocalDate date2 = LocalDate.of(2023, 12, 28);
        assertEquals(3, DateUtil.daysBetween(date1, date2));
        assertEquals(-3, DateUtil.daysBetween(date2, date1));
    }

    @Test
    void testAddDays() {
        LocalDate date = LocalDate.of(2023, 12, 25);
        assertEquals(LocalDate.of(2023, 12, 28), DateUtil.addDays(date, 3));
        assertEquals(LocalDate.of(2023, 12, 22), DateUtil.addDays(date, -3));
    }

    @Test
    void testAddMonths() {
        LocalDate date = LocalDate.of(2023, 12, 25);
        assertEquals(LocalDate.of(2024, 3, 25), DateUtil.addMonths(date, 3));
        assertEquals(LocalDate.of(2023, 9, 25), DateUtil.addMonths(date, -3));
    }

    @Test
    void testGetFirstDayOfWeek() {
        LocalDate date = LocalDate.of(2023, 12, 25); // Monday
        assertEquals(date, DateUtil.getFirstDayOfWeek(date));

        LocalDate date2 = LocalDate.of(2023, 12, 27); // Wednesday
        assertEquals(date, DateUtil.getFirstDayOfWeek(date2));
    }

    @Test
    void testGetLastDayOfWeek() {
        LocalDate date = LocalDate.of(2023, 12, 25); // Monday
        LocalDate expected = LocalDate.of(2023, 12, 31); // Sunday
        assertEquals(expected, DateUtil.getLastDayOfWeek(date));
    }

    @Test
    void testGetEndDayOfMonth() {
        assertEquals(31, DateUtil.getEndDayOfMonth(2023, 12));
        assertEquals(30, DateUtil.getEndDayOfMonth(2023, 11));
        assertEquals(28, DateUtil.getEndDayOfMonth(2023, 2));
        assertEquals(29, DateUtil.getEndDayOfMonth(2024, 2)); // Leap year
    }

    @Test
    void testToDateVO() {
        LocalDate date = LocalDate.of(2023, 12, 25);
        DateVO vo = DateUtil.toDateVO(date);

        assertNotNull(vo);
        assertEquals(2023, vo.getYear());
        assertEquals(12, vo.getMonth());
        assertEquals(25, vo.getDay());
        assertEquals("2023-12-25", vo.getDate());
        assertNotNull(vo.getWeek());
        assertEquals(date.equals(LocalDate.now()), vo.isIstoday());

        // Null handling
        assertNull(DateUtil.toDateVO(null));
    }

    @Test
    void testToMonthVO() {
        LocalDate date = LocalDate.of(2023, 12, 25);
        MonthVO vo = DateUtil.toMonthVO(date);

        assertNotNull(vo);
        assertEquals("2023", vo.getYear());
        assertEquals("12", vo.getMonth());

        // Single digit month
        MonthVO vo2 = DateUtil.toMonthVO(LocalDate.of(2023, 1, 1));
        assertEquals("01", vo2.getMonth());

        // Null handling
        assertNull(DateUtil.toMonthVO(null));
    }

    @Test
    void testToLocalDate() {
        MonthVO vo = new MonthVO();
        vo.setYear("2023");
        vo.setMonth("12");

        LocalDate date = DateUtil.toLocalDate(vo);
        assertNotNull(date);
        assertEquals(2023, date.getYear());
        assertEquals(12, date.getMonthValue());
        assertEquals(1, date.getDayOfMonth());

        // Invalid input
        vo.setMonth("invalid");
        assertNull(DateUtil.toLocalDate(vo));

        // Null handling
        assertNull(DateUtil.toLocalDate(null));
    }

    @Test
    void testMonthValid() {
        MonthVO vo = new MonthVO();
        vo.setYear("2023");

        // Test month < 1
        vo.setMonth("0");
        MonthVO result = DateUtil.monthValid(vo);
        assertNotNull(result);
        assertEquals("2022", result.getYear());
        assertEquals("12", result.getMonth());

        // Test month > 12
        vo.setYear("2023");
        vo.setMonth("13");
        result = DateUtil.monthValid(vo);
        assertNotNull(result);
        assertEquals("2024", result.getYear());
        assertEquals("01", result.getMonth());

        // Test valid month
        vo.setYear("2023");
        vo.setMonth("6");
        result = DateUtil.monthValid(vo);
        assertNotNull(result);
        assertEquals("2023", result.getYear());
        assertEquals("06", result.getMonth());

        // Invalid input
        vo.setMonth("invalid");
        assertNull(DateUtil.monthValid(vo));

        // Null handling
        assertNull(DateUtil.monthValid(null));
    }

    // Tests for legacy support methods
    @Test
    void testGetYyyyMMdd_bar() {
        String result = DateUtil.getYyyyMMdd_bar();
        assertEquals(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), result);
    }

    @Test
    void testGetToday() {
        Date result = DateUtil.getToday();
        assertNotNull(result);

        LocalDate today = LocalDate.now();
        LocalDate resultDate = result.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        assertEquals(today, resultDate);
    }

    @Test
    void testConvDate() {
        // Test with default format
        Date result = DateUtil.convDate("2023-12-25");
        assertNotNull(result);

        LocalDate expected = LocalDate.of(2023, 12, 25);
        LocalDate actual = result.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        assertEquals(expected, actual);

        // Test with custom format
        result = DateUtil.convDate("20231225", "yyyyMMdd");
        assertNotNull(result);
        assertEquals(expected, actual);

        // Test invalid date
        assertNull(DateUtil.convDate("invalid-date"));
        assertNull(DateUtil.convDate("20231225", "invalid-format"));
    }

    @Test
    void testGetYearAndMonth() {
        LocalDateTime dateTime = LocalDateTime.of(2023, 12, 25, 10, 30);
        Date date = Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());

        assertEquals(2023, DateUtil.getYear(date));
        assertEquals(12, DateUtil.getMonth(date));

        // Test null handling
        assertEquals(0, DateUtil.getYear(null));
        assertEquals(0, DateUtil.getMonth(null));
    }

    @Test
    void testDate2Str() {
        LocalDateTime dateTime = LocalDateTime.of(2023, 12, 25, 10, 30);
        Date date = Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());

        assertEquals("2023-12-25", DateUtil.date2Str(date));
        assertNull(DateUtil.date2Str(null));
    }

    @Test
    void testStr2DateAndStringToDate() {
        String dateStr = "2023-12-25";
        LocalDate expected = LocalDate.of(2023, 12, 25);

        // Test str2Date
        Date result1 = DateUtil.str2Date(dateStr);
        assertNotNull(result1);
        assertEquals(expected, result1.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());

        // Test stringToDate
        Date result2 = DateUtil.stringToDate(dateStr);
        assertNotNull(result2);
        assertEquals(expected, result2.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());

        // Test invalid input
        assertNull(DateUtil.str2Date("invalid-date"));
        assertNull(DateUtil.stringToDate("invalid-date"));
        assertNull(DateUtil.str2Date(""));
        assertNull(DateUtil.stringToDate(""));
        assertNull(DateUtil.str2Date(null));
        assertNull(DateUtil.stringToDate(null));
    }

    @Test
    void testGetWeekOfMonth() {
        LocalDateTime dateTime = LocalDateTime.of(2023, 12, 25, 10, 30);
        Date date = Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());

        int weekOfMonth = DateUtil.getWeekOfMonth(date);
        assertTrue(weekOfMonth > 0 && weekOfMonth <= 5);

        assertEquals(0, DateUtil.getWeekOfMonth(null));
    }

    @Test
    void testGetFirstAndLastOfWeek() {
        LocalDateTime dateTime = LocalDateTime.of(2023, 12, 25, 10, 30); // Monday
        Date date = Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());

        // Test getFirstOfWeek
        Date firstDay = DateUtil.getFirstOfWeek(date);
        assertNotNull(firstDay);
        LocalDate firstLocalDate = firstDay.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        assertEquals(LocalDate.of(2023, 12, 25), firstLocalDate);

        // Test getLastOfWeek
        Date lastDay = DateUtil.getLastOfWeek(date);
        assertNotNull(lastDay);
        LocalDate lastLocalDate = lastDay.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        assertEquals(LocalDate.of(2023, 12, 31), lastLocalDate);

        // Test null handling
        assertNull(DateUtil.getFirstOfWeek(null));
        assertNull(DateUtil.getLastOfWeek(null));
    }

    @Test
    void testDateAdd() {
        LocalDateTime dateTime = LocalDateTime.of(2023, 12, 25, 10, 30);
        Date date = Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());

        // Add positive days
        Date result = DateUtil.dateAdd(date, 5);
        assertNotNull(result);
        LocalDate resultDate = result.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        assertEquals(LocalDate.of(2023, 12, 30), resultDate);

        // Add negative days
        result = DateUtil.dateAdd(date, -5);
        assertNotNull(result);
        resultDate = result.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        assertEquals(LocalDate.of(2023, 12, 20), resultDate);

        // Test null handling
        assertNull(DateUtil.dateAdd(null, 5));
    }

    @Test
    void testGetDayOfWeek() {
        // Test Monday (2023-12-25 was a Monday)
        LocalDateTime mondayDateTime = LocalDateTime.of(2023, 12, 25, 10, 30);
        Date monday = Date.from(mondayDateTime.atZone(ZoneId.systemDefault()).toInstant());
        assertEquals(1, DateUtil.getDayOfWeek(monday));

        // Test Sunday (2023-12-31 was a Sunday)
        LocalDateTime sundayDateTime = LocalDateTime.of(2023, 12, 31, 10, 30);
        Date sunday = Date.from(sundayDateTime.atZone(ZoneId.systemDefault()).toInstant());
        assertEquals(7, DateUtil.getDayOfWeek(sunday));

        // Test null handling
        assertEquals(0, DateUtil.getDayOfWeek(null));
    }

    @Test
    void testDate2VO() {
        LocalDateTime dateTime = LocalDateTime.of(2023, 12, 25, 10, 30);
        Date date = Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());

        DateVO vo = DateUtil.date2VO(date);
        assertNotNull(vo);
        assertEquals(2023, vo.getYear());
        assertEquals(12, vo.getMonth());
        assertEquals(25, vo.getDay());
        assertEquals("2023-12-25", vo.getDate());

        // Test null handling
        assertNull(DateUtil.date2VO(null));
    }

    @Test
    void testDateDiff() {
        LocalDateTime dateTime1 = LocalDateTime.of(2023, 12, 25, 10, 30);
        LocalDateTime dateTime2 = LocalDateTime.of(2023, 12, 28, 10, 30);
        Date date1 = Date.from(dateTime1.atZone(ZoneId.systemDefault()).toInstant());
        Date date2 = Date.from(dateTime2.atZone(ZoneId.systemDefault()).toInstant());

        assertEquals(3, DateUtil.dateDiff(date1, date2));
        assertEquals(-3, DateUtil.dateDiff(date2, date1));

        // Test null handling
        assertEquals(0, DateUtil.dateDiff(null, date2));
        assertEquals(0, DateUtil.dateDiff(date1, null));
        assertEquals(0, DateUtil.dateDiff(null, null));
    }

    @Test
    void testSafeStr2Date() {
        // Test valid date
        Date result = DateUtil.safeStr2Date("2023-12-25");
        assertNotNull(result);
        LocalDate resultDate = result.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        assertEquals(LocalDate.of(2023, 12, 25), resultDate);

        // Test invalid inputs
        assertNull(DateUtil.safeStr2Date("invalid-date"));
        assertNull(DateUtil.safeStr2Date(""));
        assertNull(DateUtil.safeStr2Date(null));
    }

    @Test
    void testDateAddMonth() {
        LocalDateTime dateTime = LocalDateTime.of(2023, 12, 25, 10, 30);
        Date date = Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());

        // Add positive months
        Date result = DateUtil.dateAddMonth(date, 2);
        assertNotNull(result);
        LocalDate resultDate = result.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        assertEquals(LocalDate.of(2024, 2, 25), resultDate);

        // Add negative months
        result = DateUtil.dateAddMonth(date, -2);
        assertNotNull(result);
        resultDate = result.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        assertEquals(LocalDate.of(2023, 10, 25), resultDate);

        // Test null handling
        assertNull(DateUtil.dateAddMonth(null, 2));
    }
}
