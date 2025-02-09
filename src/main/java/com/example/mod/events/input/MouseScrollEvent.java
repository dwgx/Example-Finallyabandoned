package com.example.mod.events.input;

import com.example.event.Event;

public class MouseScrollEvent extends Event.Cancellable {
    private final long window;
    private double horizontal, vertical;

    public MouseScrollEvent(long window, double horizontal, double vertical) {
        this.window = window;
        this.horizontal = horizontal;
        this.vertical = vertical;
    }

    public long getWindow() {
        return window;
    }

    public double getHorizontal() {
        return horizontal;
    }

    public void setHorizontal(double horizontal) {
        this.horizontal = horizontal;
    }

    public double getVertical() {
        return vertical;
    }

    public void setVertical(double vertical) {
        this.vertical = vertical;
    }
}
