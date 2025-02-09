package com.example.mod.events.entity;

import com.example.event.Event;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Vec3d;

import static com.example.mod.client.GameAccessor.mc;

public class TravelEvent extends Event.Cancellable {
    private final LivingEntity entity;
    private Vec3d movementInput;

    public TravelEvent(LivingEntity entity, Vec3d movementInput) {
        this.entity = entity;
        this.movementInput = movementInput;
    }

    public LivingEntity getEntity() {
        return entity;
    }

    public Vec3d getMovementInput() {
        return movementInput;
    }

    public void setMovementInput(Vec3d movementInput) {
        this.movementInput = movementInput;
    }

    public boolean isLocalPlayer() {
        return mc.player != null && mc.player.equals(entity);
    }
}
