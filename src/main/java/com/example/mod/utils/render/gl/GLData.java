package com.example.mod.utils.render.gl;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

public class GLData {
    private int major = 0;
    private int minor = 0;
    private int version = 0;

    public void update() {
        this.major = GL11.glGetInteger(GL30.GL_MAJOR_VERSION);
        this.minor = GL11.glGetInteger(GL30.GL_MINOR_VERSION);
        this.version = major * 100 + minor * 10;
    }

    public int getMajor() {
        return major;
    }

    public int getMinor() {
        return minor;
    }

    public int getVersion() {
        return version;
    }
}
