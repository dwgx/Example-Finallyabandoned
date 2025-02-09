package com.example.mod.events.client.render;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;

public class LayerRenderEvent {
    private DrawContext context;
    private RenderTickCounter tickCounter;
    private float tickDelta;

    public LayerRenderEvent(DrawContext context, RenderTickCounter tickCounter, float tickDelta) {
        this.context = context;
        this.tickCounter = tickCounter;
        this.tickDelta = tickDelta;
    }

    public DrawContext getContext() {
        return context;
    }

    public void setContext(DrawContext context) {
        this.context = context;
    }

    public RenderTickCounter getTickCounter() {
        return tickCounter;
    }

    public void setTickCounter(RenderTickCounter tickCounter) {
        this.tickCounter = tickCounter;
    }

    public float getTickDelta() {
        return tickDelta;
    }

    public void setTickDelta(float tickDelta) {
        this.tickDelta = tickDelta;
    }
}
