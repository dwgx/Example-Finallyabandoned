package com.example.mod.events.client.render.entity;

import com.example.event.Event;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;

public class RenderStateUpdateEvent extends Event.Cancellable {
    private Entity entity;
    private EntityRenderState renderState;
    private float delta;
    private RenderStateType type;

    public RenderStateUpdateEvent(Event.State state, Entity entity, EntityRenderState renderState, float delta, RenderStateType type) {
        super(state);
        this.entity = entity;

        this.renderState = renderState;
        this.delta = delta;
        this.type = type;
    }

    public EntityRenderState getRenderState() {
        return renderState;
    }

    public void setRenderState(EntityRenderState renderState) {
        this.renderState = renderState;
    }

    public Entity getEntity() {
        return entity;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    public float getDelta() {
        return delta;
    }

    public void setDelta(float delta) {
        this.delta = delta;
    }

    public RenderStateType getType() {
        return type;
    }

    public void setType(RenderStateType type) {
        this.type = type;
    }

    public LivingEntity asLivingEntity() {
        if (this.entity instanceof LivingEntity wrapper) {
            return wrapper;
        }

        return null;
    }

    public LivingEntityRenderState asLivingEntityRenderState() {
        if (this.renderState instanceof LivingEntityRenderState wrapper) {
            return wrapper;
        }

        return null;
    }

    @Override
    public void cancel(String reason) {
        if (getState() == State.POST) {
            throw new UnsupportedOperationException("Cannot cancel POST state.");
        }
        super.cancel(reason);
    }

    public enum RenderStateType {
        ENTITY,
        LIVING_ENTITY,
        LOCAL_PLAYER
    }
}
