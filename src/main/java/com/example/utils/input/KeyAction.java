package com.example.utils.input;

import org.lwjgl.glfw.GLFW;

/*
public static final int GLFW_RELEASE = 0;

public static final int GLFW_PRESS = 1;

public static final int GLFW_REPEAT = 2;
 */
public enum KeyAction {
    PRESS,
    RELEASE,
    REPEAT,
    UNKNOWN;

    private static final KeyAction[] actions = new KeyAction[4];

    static {
        actions[GLFW.GLFW_PRESS] = PRESS;
        actions[GLFW.GLFW_RELEASE] = RELEASE;
        actions[GLFW.GLFW_REPEAT] = REPEAT;
    }

    public static KeyAction of(int action) {
        if (action >= 0 && action < actions.length && actions[action] != null) {
            return actions[action];
        }
        return UNKNOWN;
    }

    public boolean isPress() {
        return this == PRESS;
    }

    public boolean isRelease() {
        return this == RELEASE;
    }

    public boolean isRepeat() {
        return this == REPEAT;
    }
}
