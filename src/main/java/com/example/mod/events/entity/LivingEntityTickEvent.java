package com.example.mod.events.entity;

import com.example.event.Event;
import net.minecraft.entity.LivingEntity;

import static com.example.mod.client.GameAccessor.mc;

public class LivingEntityTickEvent extends Event.Cancellable {
    private final LivingEntity entity;

    public LivingEntityTickEvent(State state, LivingEntity entity) {
        super(state);
        this.entity = entity;
    }

    public LivingEntity getEntity() {
        return entity;
    }

    public boolean isLocalPlayer() {
        return mc.player != null && mc.player.equals(entity);
    }

    @Override
    public void cancel(String reason) {
        if (getState() == State.POST) {
            throw new UnsupportedOperationException("Cannot cancel POST state.");
        }
        super.cancel(reason);
    }
}
