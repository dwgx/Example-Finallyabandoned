package com.example.mod.managers;

import com.example.utils.pattern.Singleton;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;

public class RenderStateManager {
    private LivingEntityRenderState localPlayerRenderState;

    public LivingEntityRenderState getLocalPlayerRenderState() {
        return localPlayerRenderState;
    }

    public void setLocalPlayerRenderState(LivingEntityRenderState state) {
        this.localPlayerRenderState = state;
    }

    public static RenderStateManager getInstance() {
        return Singleton.getInstance(RenderStateManager.class);
    }
}
