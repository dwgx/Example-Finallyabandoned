package com.example.utils.input;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class KeyMapper {
    private static final Map<String, Integer> KEY_CODE_MAP = new ConcurrentHashMap<>();

    static {
        KEY_CODE_MAP.put("unknown", -1);
        KEY_CODE_MAP.put("none", -1);

        KEY_CODE_MAP.put("0", 48);
        KEY_CODE_MAP.put("1", 49);
        KEY_CODE_MAP.put("2", 50);
        KEY_CODE_MAP.put("3", 51);
        KEY_CODE_MAP.put("4", 52);
        KEY_CODE_MAP.put("5", 53);
        KEY_CODE_MAP.put("6", 54);
        KEY_CODE_MAP.put("7", 55);
        KEY_CODE_MAP.put("8", 56);
        KEY_CODE_MAP.put("9", 57);

        KEY_CODE_MAP.put("a", 65);
        KEY_CODE_MAP.put("b", 66);
        KEY_CODE_MAP.put("c", 67);
        KEY_CODE_MAP.put("d", 68);
        KEY_CODE_MAP.put("e", 69);
        KEY_CODE_MAP.put("f", 70);
        KEY_CODE_MAP.put("g", 71);
        KEY_CODE_MAP.put("h", 72);
        KEY_CODE_MAP.put("i", 73);
        KEY_CODE_MAP.put("j", 74);
        KEY_CODE_MAP.put("k", 75);
        KEY_CODE_MAP.put("l", 76);
        KEY_CODE_MAP.put("m", 77);
        KEY_CODE_MAP.put("n", 78);
        KEY_CODE_MAP.put("o", 79);
        KEY_CODE_MAP.put("p", 80);
        KEY_CODE_MAP.put("q", 81);
        KEY_CODE_MAP.put("r", 82);
        KEY_CODE_MAP.put("s", 83);
        KEY_CODE_MAP.put("t", 84);
        KEY_CODE_MAP.put("u", 85);
        KEY_CODE_MAP.put("v", 86);
        KEY_CODE_MAP.put("w", 87);
        KEY_CODE_MAP.put("x", 88);
        KEY_CODE_MAP.put("y", 89);
        KEY_CODE_MAP.put("z", 90);

        KEY_CODE_MAP.put("f1", 290);
        KEY_CODE_MAP.put("f2", 291);
        KEY_CODE_MAP.put("f3", 292);
        KEY_CODE_MAP.put("f4", 293);
        KEY_CODE_MAP.put("f5", 294);
        KEY_CODE_MAP.put("f6", 295);
        KEY_CODE_MAP.put("f7", 296);
        KEY_CODE_MAP.put("f8", 297);
        KEY_CODE_MAP.put("f9", 298);
        KEY_CODE_MAP.put("f10", 299);
        KEY_CODE_MAP.put("f11", 300);
        KEY_CODE_MAP.put("f12", 301);

        KEY_CODE_MAP.put("numlock", 282);
        KEY_CODE_MAP.put("keypad_0", 320);
        KEY_CODE_MAP.put("keypad_1", 321);
        KEY_CODE_MAP.put("keypad_2", 322);
        KEY_CODE_MAP.put("keypad_3", 323);
        KEY_CODE_MAP.put("keypad_4", 324);
        KEY_CODE_MAP.put("keypad_5", 325);
        KEY_CODE_MAP.put("keypad_6", 326);
        KEY_CODE_MAP.put("keypad_7", 327);
        KEY_CODE_MAP.put("keypad_8", 328);
        KEY_CODE_MAP.put("keypad_9", 329);
        KEY_CODE_MAP.put("keypad_add", 334);
        KEY_CODE_MAP.put("keypad_decimal", 330);
        KEY_CODE_MAP.put("keypad_enter", 335);
        KEY_CODE_MAP.put("keypad_equal", 336);
        KEY_CODE_MAP.put("keypad_multiply", 332);
        KEY_CODE_MAP.put("keypad_divide", 331);
        KEY_CODE_MAP.put("keypad_subtract", 333);

        KEY_CODE_MAP.put("down", 264);
        KEY_CODE_MAP.put("left", 263);
        KEY_CODE_MAP.put("right", 262);
        KEY_CODE_MAP.put("up", 265);

        KEY_CODE_MAP.put("apostrophe", 39);      // '
        KEY_CODE_MAP.put("backslash", 92);       // \
        KEY_CODE_MAP.put("comma", 44);           // ,
        KEY_CODE_MAP.put("equal", 61);           // =
        KEY_CODE_MAP.put("grave_accent", 96);    // `
        KEY_CODE_MAP.put("left_bracket", 91);    // [
        KEY_CODE_MAP.put("minus", 45);           // -
        KEY_CODE_MAP.put("period", 46);          // .
        KEY_CODE_MAP.put("right_bracket", 93);   // ]
        KEY_CODE_MAP.put("semicolon", 59);       // ;
        KEY_CODE_MAP.put("slash", 47);           // /


        KEY_CODE_MAP.put("'", 39);
        KEY_CODE_MAP.put("\\", 92);
        KEY_CODE_MAP.put(",", 44);
        KEY_CODE_MAP.put("=", 61);
        KEY_CODE_MAP.put("`", 96);
        KEY_CODE_MAP.put("[", 91);
        KEY_CODE_MAP.put("-", 45);
        KEY_CODE_MAP.put(".", 46);
        KEY_CODE_MAP.put("]", 93);
        KEY_CODE_MAP.put(";", 59);
        KEY_CODE_MAP.put("/", 47);

        KEY_CODE_MAP.put("space", 32);
        KEY_CODE_MAP.put("enter", 257);
        KEY_CODE_MAP.put("escape", 256);
        KEY_CODE_MAP.put("backspace", 259);
        KEY_CODE_MAP.put("delete", 261);
        KEY_CODE_MAP.put("tab", 258);
        KEY_CODE_MAP.put("capslock", 280);
        KEY_CODE_MAP.put("pause", 284);
        KEY_CODE_MAP.put("scroll_lock", 281);
        KEY_CODE_MAP.put("print_screen", 283);
    }

    public static Integer getKeyCode(String key) {
        return KEY_CODE_MAP.getOrDefault(key.toLowerCase(), null);
    }

    public static String getKeyName(int keyCode) {
        for (Map.Entry<String, Integer> entry : KEY_CODE_MAP.entrySet()) {
            if (entry.getValue().equals(keyCode)) {
                return entry.getKey();
            }
        }
        return "unknown";
    }

    public static Collection<String> getKeyNames() {
        return KEY_CODE_MAP.keySet();
    }

    public static Collection<Integer> getKeyCodes() {
        return KEY_CODE_MAP.values();
    }
}