package com.example.mod.events.client.render;

import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.profiler.Profiler;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

public class RenderWorldEvent_Backup {
    private Profiler profiler;
    private RenderTickCounter tickCounter;
    private Camera camera;
    private float tickDelta;
    private MatrixStack matrixStack;
    private Matrix4f matrix4f, matrix4f2, matrix4f3;
    private Quaternionf quaternionf;

    public RenderWorldEvent_Backup(Profiler profiler, RenderTickCounter tickCounter, Camera camera, float tickDelta, MatrixStack matrixStack, Matrix4f matrix4f, Matrix4f matrix4f2, Matrix4f matrix4f3, Quaternionf quaternionf) {
        this.profiler = profiler;
        this.tickCounter = tickCounter;
        this.camera = camera;
        this.tickDelta = tickDelta;
        this.matrixStack = matrixStack;
        this.matrix4f = matrix4f;
        this.matrix4f2 = matrix4f2;
        this.matrix4f3 = matrix4f3;
        this.quaternionf = quaternionf;
    }

    public Profiler getProfiler() {
        return profiler;
    }

    public void setProfiler(Profiler profiler) {
        this.profiler = profiler;
    }

    public RenderTickCounter getTickCounter() {
        return tickCounter;
    }

    public void setTickCounter(RenderTickCounter tickCounter) {
        this.tickCounter = tickCounter;
    }

    public Camera getCamera() {
        return camera;
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    public float getTickDelta() {
        return tickDelta;
    }

    public void setTickDelta(float tickDelta) {
        this.tickDelta = tickDelta;
    }

    public MatrixStack getMatrixStack() {
        return matrixStack;
    }

    public void setMatrixStack(MatrixStack matrixStack) {
        this.matrixStack = matrixStack;
    }

    public Matrix4f getMatrix4f() {
        return matrix4f;
    }

    public void setMatrix4f(Matrix4f matrix4f) {
        this.matrix4f = matrix4f;
    }

    public Matrix4f getMatrix4f2() {
        return matrix4f2;
    }

    public void setMatrix4f2(Matrix4f matrix4f2) {
        this.matrix4f2 = matrix4f2;
    }

    public Matrix4f getMatrix4f3() {
        return matrix4f3;
    }

    public void setMatrix4f3(Matrix4f matrix4f3) {
        this.matrix4f3 = matrix4f3;
    }

    public Quaternionf getQuaternionf() {
        return quaternionf;
    }

    public void setQuaternionf(Quaternionf quaternionf) {
        this.quaternionf = quaternionf;
    }
}
