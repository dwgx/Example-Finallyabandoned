package com.example.mod.utils.render.gl;

import java.util.Stack;

public class GLStateCacheManager {
    private final Stack<GLStateCache> stack = new Stack<>();

    public void save() {
        GLStateCache cache = new GLStateCache();
        cache.save();
        this.stack.push(cache);
    }

    public void restore() {
        if (this.stack.isEmpty()) {
            return;
        }

        GLStateCache cache = this.stack.pop();
        cache.restore();
    }

    public Stack<GLStateCache> getStateStack() {
        return stack;
    }
}
