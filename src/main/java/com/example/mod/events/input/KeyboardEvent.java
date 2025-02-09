package com.example.mod.events.input;

import com.example.event.Event;
import com.example.utils.input.KeyAction;

public class KeyboardEvent extends Event.Cancellable {
    private final long window;
    private int key, scancode, modifiers;
    private KeyAction action;

    public KeyboardEvent(long window, int key, int scancode, int modifiers, KeyAction action) {
        this.window = window;
        this.key = key;
        this.scancode = scancode;
        this.modifiers = modifiers;
        this.action = action;
    }

    public long getWindow() {
        return window;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public int getScancode() {
        return scancode;
    }

    public void setScancode(int scancode) {
        this.scancode = scancode;
    }

    public int getModifiers() {
        return modifiers;
    }

    public void setModifiers(int modifiers) {
        this.modifiers = modifiers;
    }

    public KeyAction getAction() {
        return action;
    }

    public void setAction(KeyAction action) {
        this.action = action;
    }
}
