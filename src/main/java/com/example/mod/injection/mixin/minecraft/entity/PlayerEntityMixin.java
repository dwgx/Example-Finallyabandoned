package com.example.mod.injection.mixin.minecraft.entity;

import com.example.Global;
import com.example.event.Event;
import com.example.mod.events.network.PlayerTickMovementEvent;
import com.example.mod.features.module.movement.ModuleKeepSprint;
import com.example.mod.managers.RotationManager;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.example.mod.client.GameAccessor.mc;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {
    @ModifyExpressionValue(
            method = "attack",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/player/PlayerEntity;getYaw()F"
            )
    )
    private float onAttackGetYaw(float original) {
        if ((Object) this == mc.player) {
            RotationManager rotationManager = RotationManager.getInstance();

            if (rotationManager.isNetwork() && rotationManager.getWorkingRotation().getVelocityCorrection().need()) {
                return rotationManager.getWorkingRotation().getYaw();
            }
        }

        return original;
    }

    @WrapWithCondition(
            method = "attack",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/player/PlayerEntity;setVelocity(Lnet/minecraft/util/math/Vec3d;)V"
            )
    )
    private boolean onAttackSetVelocity(PlayerEntity instance, Vec3d vec3d) {
        return !ModuleKeepSprint.getInstance().isEnabled();
    }

    @WrapWithCondition(
            method = "attack",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/player/PlayerEntity;setSprinting(Z)V"
            )
    )
    private boolean onAttackSetSprint(PlayerEntity instance, boolean b) {
        return !ModuleKeepSprint.getInstance().isEnabled();
    }

    @Inject(
            method = "tickMovement",
            at = @At(
                    value = "HEAD"
            ),
            cancellable = true
    )
    private void onPreTickMovement(CallbackInfo ci) {
        PlayerTickMovementEvent event = new PlayerTickMovementEvent(Event.State.PRE, (PlayerEntity) (Object) this);
        Global.getEventBus().post(event).now();

        if (event.isCanceled()) {
            ci.cancel();
        }
    }

    @Inject(
            method = "tickMovement",
            at = @At(
                    value = "TAIL"
            )
    )
    private void onPostTickMovement(CallbackInfo ci) {
        PlayerTickMovementEvent event = new PlayerTickMovementEvent(Event.State.POST, (PlayerEntity) (Object) this);
        Global.getEventBus().post(event).now();
    }
}
