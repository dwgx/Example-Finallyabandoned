package com.example.utils.text;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;

public class I18n {
    private static final Map<String, ResourceBundle> BUNDLE_MAP = new ConcurrentHashMap<>();

    public static String translate(String key, Object... args) {
        return translate(Locale.getDefault(), key, args);
    }

    public static String translate(Locale locale, String key, Object... args) {
        String cacheKey = locale.toString();
        ResourceBundle bundle = BUNDLE_MAP.computeIfAbsent(cacheKey, k -> ResourceBundle.getBundle("messages", locale));

        String value = bundle.getString(key);
        if (args != null && args.length > 0) {
            value = MessageFormat.format(value, args);
        }
        return value;
    }
}
