package com.example.mod.utils;

import java.util.HashMap;
import java.util.Map;

public class KeyMapper {
    private static final Map<Integer, String> keyMap = new HashMap<>();

    static {
        keyMap.put(8, "BACKSPACE");
        keyMap.put(9, "TAB");
        keyMap.put(13, "ENTER");
        keyMap.put(16, "SHIFT");
        keyMap.put(17, "CTRL");
        keyMap.put(18, "ALT");
        keyMap.put(19, "PAUSE");
        keyMap.put(20, "CAPS_LOCK");
        keyMap.put(27, "ESCAPE");
        keyMap.put(32, "SPACE");
        keyMap.put(33, "PAGE_UP");
        keyMap.put(34, "PAGE_DOWN");
        keyMap.put(35, "END");
        keyMap.put(36, "HOME");
        keyMap.put(37, "LEFT_ARROW");
        keyMap.put(38, "UP_ARROW");
        keyMap.put(39, "RIGHT_ARROW");
        keyMap.put(40, "DOWN_ARROW");
        keyMap.put(45, "INSERT");
        keyMap.put(46, "DELETE");
        // Function keys
        keyMap.put(112, "F1");
        keyMap.put(113, "F2");
        keyMap.put(114, "F3");
        keyMap.put(115, "F4");
        keyMap.put(116, "F5");
        keyMap.put(117, "F6");
        keyMap.put(118, "F7");
        keyMap.put(119, "F8");
        keyMap.put(120, "F9");
        keyMap.put(121, "F10");
        keyMap.put(122, "F11");
        keyMap.put(123, "F12");
        // Letters
        for (int i = 65; i <= 90; i++) { // A-Z
            keyMap.put(i, String.valueOf((char) i));
        }
        // Numbers
        for (int i = 48; i <= 57; i++) { // 0-9
            keyMap.put(i, String.valueOf((char) i));
        }
        // Add more keys as needed
    }

    public static String getKeyName(int keyCode) {
        return keyMap.getOrDefault(keyCode, "Unknown");
    }
}
