package com.example.mod.utils.render.gl;

import java.util.Stack;

public class GLContextCacheManager {
    private final Stack<GLContextCache> stack = new Stack<>();

    public void save() {
        GLContextCache cache = new GLContextCache();
        cache.save();
        this.stack.push(cache);
    }

    public void restore() {
        if (this.stack.isEmpty()) {
            return;
        }

        GLContextCache cache = this.stack.pop();
        cache.restore();
    }

    public Stack<GLContextCache> getStateStack() {
        return stack;
    }
}
