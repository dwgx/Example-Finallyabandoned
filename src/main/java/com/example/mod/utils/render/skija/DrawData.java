package com.example.mod.utils.render.skija;

import io.github.humbleui.skija.Canvas;

import java.util.PriorityQueue;
import java.util.Queue;
import java.util.function.Consumer;

public class DrawData {
    private final Queue<Consumer<Canvas>> cmdQueue = new PriorityQueue<>();
    private boolean valid;

    public void reset() {
        this.cmdQueue.clear();
        this.valid = false;
    }

    public void submit(Consumer<Canvas> cmd) {
        cmdQueue.add(cmd);
    }

    public Queue<Consumer<Canvas>> getCmdQueue() {
        return cmdQueue;
    }

    public boolean isValid() {
        return valid;
    }

    public void valid() {
        this.valid = !this.cmdQueue.isEmpty();
    }
}
