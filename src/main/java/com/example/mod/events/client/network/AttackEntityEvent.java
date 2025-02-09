package com.example.mod.events.client.network;

import com.example.event.Event;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

public class AttackEntityEvent extends Event.Cancellable {
    private final PlayerEntity player;
    private Entity target;

    public AttackEntityEvent(PlayerEntity player, Entity target) {
        this.player = player;
        this.target = target;
    }

    public PlayerEntity getPlayer() {
        return player;
    }

    public Entity getTarget() {
        return target;
    }

    public void setTarget(Entity target) {
        this.target = target;
    }
}
