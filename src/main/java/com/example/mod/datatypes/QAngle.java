package com.example.mod.datatypes;

import net.minecraft.client.util.math.Vector2f;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class QAngle {
    private float yaw;
    private float pitch;
    private float roll;

    public QAngle(float yaw, float pitch, float roll) {
        this.yaw = yaw;
        this.pitch = pitch;
        this.roll = roll;
    }

    public QAngle(float yaw, float pitch) {
        this(yaw, pitch, 0.0f);
    }

    public QAngle(Vector2f rotation) {
        this(rotation.getX(), rotation.getY(), 0.0f);
    }

    public QAngle() {
        this(0.0f, 0.0f);
    }

    public float getYaw() {
        return yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public float getRoll() {
        return roll;
    }

    public void setRoll(float roll) {
        this.roll = roll;
    }

    public Vec3d getVec() {
        double yawRad = Math.toRadians(-this.yaw);
        double pitchRad = Math.toRadians(this.pitch);
        double pitchCos = MathHelper.cos((float) pitchRad);

        return new Vec3d(
                MathHelper.sin((float) yawRad) * pitchCos,
                -MathHelper.sin((float) pitchRad),
                MathHelper.cos((float) yawRad) * pitchCos
        );
    }

    public QAngle add(QAngle other) {
        return new QAngle(
                this.yaw + other.yaw,
                this.pitch + other.pitch,
                this.roll + other.roll
        );
    }

    public QAngle subtract(QAngle other) {
        return new QAngle(
                this.yaw - other.yaw,
                this.pitch - other.pitch,
                this.roll - other.roll
        );
    }

    public void normalize() {
        this.yaw = (this.yaw + 180) % 360 - 180;
        this.pitch = MathHelper.clamp(this.pitch, -89.0f, 89.0f);
        this.roll = (this.roll + 180) % 360 - 180;
    }

    public boolean equals(QAngle other, float tolerance) {
        return Math.abs(this.yaw - other.yaw) < tolerance &&
                Math.abs(this.pitch - other.pitch) < tolerance &&
                Math.abs(this.roll - other.roll) < tolerance;
    }
}