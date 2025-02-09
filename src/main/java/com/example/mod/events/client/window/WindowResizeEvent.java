package com.example.mod.events.client.window;

import com.example.event.Event;

public class WindowResizeEvent extends Event.Cancellable {
    private final long window;
    private int width, height;

    public WindowResizeEvent(long window, int width, int height) {
        this.window = window;
        this.width = width;
        this.height = height;
    }

    public long getWindow() {
        return window;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
