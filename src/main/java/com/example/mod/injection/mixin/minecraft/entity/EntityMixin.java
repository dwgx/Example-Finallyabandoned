package com.example.mod.injection.mixin.minecraft.entity;

import com.example.Global;
import com.example.mod.events.entity.VelocityUpdateEvent;
import com.example.mod.features.module.exploit.ModuleNoPitchLimit;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import static com.example.mod.client.GameAccessor.mc;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Shadow private float yaw;
    @Shadow private float pitch;

    @Shadow
    protected static Vec3d movementInputToVelocity(Vec3d movementInput, float speed, float yaw) {
        return null;
    }

    @Redirect(
            method = "changeLookDirection",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/math/MathHelper;clamp(FFF)F"
            )
    )
    public float onChangeLookDirectionClamp(float value, float min, float max) {
        if (ModuleNoPitchLimit.getInstance().isEnabled()) {
            return value;
        }

        return MathHelper.clamp(value, min, max);
    }

    @Redirect(
            method = "updateVelocity",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/Entity;movementInputToVelocity(Lnet/minecraft/util/math/Vec3d;FF)Lnet/minecraft/util/math/Vec3d;"
            )
    )
    public Vec3d onUpdateVelocity(Vec3d movementInput, float speed, float yaw) {
        VelocityUpdateEvent event = new VelocityUpdateEvent((Entity) (Object) this, movementInput, speed, yaw, movementInputToVelocity(movementInput, speed, yaw));
        Global.getEventBus().post(event).now();

        if ((Object) this == mc.player) {
            return event.getVelocity();
        }

        return movementInputToVelocity(movementInput, speed, yaw);
    }
}
