package com.devkbil.mtssbj.common;


import com.devkbil.mtssbj.common.util.DateUtil;
import org.springframework.format.Formatter;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class LocalDateFormatter implements Formatter<LocalDate> {
    @Override
    public LocalDate parse(String text, Locale locale) throws ParseException {
        return LocalDate.parse(text, DateTimeFormatter.ofPattern(DateUtil.getYyyyMMdd_bar()));
    }

    @Override
    public String print(LocalDate object, Locale locale) {
        return DateTimeFormatter.ofPattern(DateUtil.getYyyyMMdd_bar()).format(object);
    }
}
