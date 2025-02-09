package com.example.mod.events.network;

import com.example.event.Event;
import net.minecraft.entity.player.PlayerEntity;

import static com.example.mod.client.GameAccessor.mc;

public class PlayerTickMovementEvent extends Event.Cancellable {
    private final PlayerEntity entity;

    public PlayerTickMovementEvent(State state, PlayerEntity entity) {
        super(state);
        this.entity = entity;
    }

    public PlayerEntity getEntity() {
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
