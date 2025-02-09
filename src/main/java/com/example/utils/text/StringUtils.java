package com.example.utils.text;

import com.example.information.AppInfo;

public class StringUtils {
    public static boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }

    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }

    public static boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }

    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    public static String profilerTag(String... keys) {
        return String.join(".", AppInfo.NAME_LOWERCASE, String.join(".", keys));
    }
}
