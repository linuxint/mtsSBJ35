package com.devkbil.mtssbj.common;

import org.springframework.context.support.ReloadableResourceBundleMessageSource;

import java.util.Locale;
import java.util.Properties;

public class ExtendReloadableResourceBundleMessageSource extends ReloadableResourceBundleMessageSource {
    public Properties getMessages(Locale locale) {
        return getMergedProperties(locale).getProperties();
    }
}