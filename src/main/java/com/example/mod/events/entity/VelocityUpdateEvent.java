package com.example.mod.events.entity;

import com.example.event.Event;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

import static com.example.mod.client.GameAccessor.mc;

public class VelocityUpdateEvent extends Event {
    private Entity entity;
    private Vec3d movementInput;
    private float speed, yaw;
    private Vec3d velocity;

    public VelocityUpdateEvent(Entity entity, Vec3d movementInput, float speed, float yaw, Vec3d velocity) {
        this.entity = entity;
        this.movementInput = movementInput;
        this.speed = speed;
        this.yaw = yaw;
        this.velocity = velocity;
    }

    public Entity getEntity() {
        return entity;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    public Vec3d getMovementInput() {
        return movementInput;
    }

    public void setMovementInput(Vec3d movementInput) {
        this.movementInput = movementInput;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public float getYaw() {
        return yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public Vec3d getVelocity() {
        return velocity;
    }

    public void setVelocity(Vec3d velocity) {
        this.velocity = velocity;
    }

    public boolean isLocalPlayer() {
        return mc.player != null && mc.player.equals(entity);
    }
}
