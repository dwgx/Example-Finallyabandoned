package com.example.mod.managers;

import com.example.mod.datatypes.Rotation;
import com.example.mod.enums.cmd.CmdType;
import com.example.utils.pattern.Singleton;
import net.minecraft.client.util.math.Vector2f;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class RotationManager {
    private final PriorityQueue<Rotation> rotations = new PriorityQueue<>(Comparator.comparingInt(Rotation::getPriority).reversed());
    private Rotation workingRotation, lastRotation, previousRotation;
    private Vector2f serverRotation, previousServerRotation;

    private AtomicInteger rotationDuration = new AtomicInteger();

    public boolean offer(Rotation rotation, boolean keep) {
        if (rotations.isEmpty()) {
            return rotations.offer(rotation);
        }

        boolean removed = rotations.removeIf(current -> {
            int comparison = Integer.compare(current.getPriority(), rotation.getPriority());
            return comparison < 0 || (comparison == 0 && !keep);
        });

        return rotations.offer(rotation);
    }

    public boolean offer(Rotation rotation) {
        return offer(rotation, false);
    }

    public void update() {
        if (this.allowedRotation()) {
            this.workingRotation = this.rotations.poll();
        }

        if (this.workingRotation != null && this.workingRotation.isFinished()) {
            this.workingRotation = null;
        }
    }

    public Rotation peek() {
        return rotations.peek();
    }

    public Rotation poll() {
        return rotations.poll();
    }

    public Vector2f getPreviousServerRotation() {
        return previousServerRotation;
    }

    public void setPreviousServerRotation(Vector2f previousServerRotation) {
        this.previousServerRotation = previousServerRotation;
    }

    public Rotation getWorkingRotation() {
        return workingRotation;
    }

    public Rotation getLastRotation() {
        return lastRotation;
    }

    public void setLastRotation(Rotation lastRotation) {
        this.lastRotation = lastRotation;
    }

    public Rotation getPreviousRotation() {
        return previousRotation;
    }

    public void setPreviousRotation(Rotation previousRotation) {
        this.previousRotation = previousRotation;
    }

    public Vector2f getServerRotation() {
        return serverRotation;
    }

    public void setServerRotation(Vector2f serverRotation) {
        this.serverRotation = serverRotation;
    }

    public float getServerYaw() {
        return serverRotation != null ? serverRotation.x : 0.0f;
    }

    public float getServerPitch() {
        return serverRotation != null ? serverRotation.y : 0.0f;
    }

    public AtomicInteger getRotationDuration() {
        return rotationDuration;
    }

    public void setRotationDuration(AtomicInteger rotationDuration) {
        this.rotationDuration = rotationDuration;
    }

    public boolean allowedRotation() {
        return !this.rotations.isEmpty();
    }

    public boolean isRotating() {
        return getWorkingRotation() != null;
    }

    // see CmdType

    public CmdType getCmdType() {
        return isRotating() ? getWorkingRotation().getCmdType() : CmdType.NOOP;
    }

    public boolean isNetwork() {
        return getCmdType().has(CmdType.NETWORK);
    }

    public PriorityQueue<Rotation> getRotations() {
        return rotations;
    }

    public static RotationManager getInstance() {
        return Singleton.getInstance(RotationManager.class);
    }
}
