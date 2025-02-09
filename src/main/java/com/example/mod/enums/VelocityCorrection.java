package com.example.mod.enums;

public enum VelocityCorrection {
    NONE,
    STANDARD,
    BLENDED;

    public boolean need() {
        return !this.equals(NONE);
    }
}
