package com.example.mod.events.client.render;

import com.example.event.Event;
import net.minecraft.client.render.BufferBuilderStorage;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.ObjectAllocator;
import org.joml.Matrix4f;

public class WorldRenderEvent extends Event.Cancellable {
    private ObjectAllocator allocator;
    private RenderTickCounter tickCounter;
    private boolean renderBlockOutline;
    private Camera camera;
    private GameRenderer gameRenderer;
    private Matrix4f positionMatrix, projectionMatrix;
    private BufferBuilderStorage bufferBuilders;

    public WorldRenderEvent(ObjectAllocator allocator, RenderTickCounter tickCounter, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, Matrix4f positionMatrix, Matrix4f projectionMatrix, BufferBuilderStorage bufferBuilders) {
        this.allocator = allocator;
        this.tickCounter = tickCounter;
        this.renderBlockOutline = renderBlockOutline;
        this.camera = camera;
        this.gameRenderer = gameRenderer;
        this.positionMatrix = positionMatrix;
        this.projectionMatrix = projectionMatrix;
        this.bufferBuilders = bufferBuilders;
    }

    public ObjectAllocator getAllocator() {
        return allocator;
    }

    public void setAllocator(ObjectAllocator allocator) {
        this.allocator = allocator;
    }

    public RenderTickCounter getTickCounter() {
        return tickCounter;
    }

    public void setTickCounter(RenderTickCounter tickCounter) {
        this.tickCounter = tickCounter;
    }

    public boolean isRenderBlockOutline() {
        return renderBlockOutline;
    }

    public void setRenderBlockOutline(boolean renderBlockOutline) {
        this.renderBlockOutline = renderBlockOutline;
    }

    public Camera getCamera() {
        return camera;
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    public GameRenderer getGameRenderer() {
        return gameRenderer;
    }

    public void setGameRenderer(GameRenderer gameRenderer) {
        this.gameRenderer = gameRenderer;
    }

    public Matrix4f getPositionMatrix() {
        return positionMatrix;
    }

    public void setPositionMatrix(Matrix4f positionMatrix) {
        this.positionMatrix = positionMatrix;
    }

    public Matrix4f getProjectionMatrix() {
        return projectionMatrix;
    }

    public void setProjectionMatrix(Matrix4f projectionMatrix) {
        this.projectionMatrix = projectionMatrix;
    }

    public BufferBuilderStorage getBufferBuilders() {
        return bufferBuilders;
    }

    public void setBufferBuilders(BufferBuilderStorage bufferBuilders) {
        this.bufferBuilders = bufferBuilders;
    }
}
