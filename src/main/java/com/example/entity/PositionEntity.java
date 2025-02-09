package com.example.entity;

import net.minecraft.util.math.Vec3d;

public class PositionEntity {
    private Vec3d position;

    public PositionEntity(Vec3d position) {
        this.position = position;
    }

    public PositionEntity(double x, double y, double z) {
        this(new Vec3d(x, y, z));
    }

    public Vec3d getPosition() {
        return position;
    }

    public void setPosition(Vec3d position) {
        this.position = position;
    }
}
