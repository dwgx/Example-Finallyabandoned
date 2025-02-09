package com.example.mod.injection.mixin.minecraft.entity;

import com.example.Global;
import com.example.event.Event;
import com.example.mod.enums.cmd.CmdType;
import com.example.mod.events.entity.LivingEntityTickEvent;
import com.example.mod.events.entity.TravelEvent;
import com.example.mod.events.network.TickMovementEvent;
import com.example.mod.managers.RotationManager;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.example.mod.client.GameAccessor.mc;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @Shadow public abstract void updateTrackedHeadRotation(float yaw, int interpolationSteps);

    @Inject(
            method = "travel",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/LivingEntity;isLogicalSideForUpdatingMovement()Z",
                    shift = At.Shift.AFTER
            ),
            cancellable = true
    )
    private void onTravel(Vec3d movementInput, CallbackInfo ci) {
        TravelEvent event = new TravelEvent((LivingEntity) (Object) this, movementInput);
        Global.getEventBus().post(event).now();

        if (event.isCanceled()) {
            ci.cancel();
        }
    }

    @Inject(
            method = "tick",
            at = @At(
                    value = "HEAD"
            ),
            cancellable = true
    )
    private void onPreTick(CallbackInfo ci) {
        LivingEntityTickEvent event = new LivingEntityTickEvent(Event.State.PRE, (LivingEntity) (Object) this);
        Global.getEventBus().post(event).now();

        if (event.isCanceled()) {
            ci.cancel();
        }
    }

    @Inject(
            method = "tick",
            at = @At(
                    value = "HEAD"
            )
    )
    private void onPostTick(CallbackInfo ci) {
        LivingEntityTickEvent event = new LivingEntityTickEvent(Event.State.POST, (LivingEntity) (Object) this);
        Global.getEventBus().post(event).now();
    }

    @ModifyExpressionValue(
            method = "turnHead",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/LivingEntity;getYaw()F"
            )
    )
    private float onTickHeadYaw(float original) {
        if ((Object) this == mc.player) {
            RotationManager rotationManager = RotationManager.getInstance();

            if (rotationManager.isNetwork() || rotationManager.getCmdType().has(CmdType.RENDER)) {
                return rotationManager.getWorkingRotation().getYaw();
            }
        }

        return original;
    }

    @Inject(
            method = "tickMovement",
            at = @At(
                    value = "HEAD"
            ),
            cancellable = true
    )
    private void onTickMovementHead(CallbackInfo ci) {
        TickMovementEvent event = new TickMovementEvent(Event.State.PRE, (LivingEntity) (Object) this);
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
    private void onTickMovementTail(CallbackInfo ci) {
        TickMovementEvent event = new TickMovementEvent(Event.State.POST, (LivingEntity) (Object) this);
        Global.getEventBus().post(event).now();
    }

    @ModifyExpressionValue(
            method = "jump",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/LivingEntity;getYaw()F"
            )
    )
    private float onJumpGetYaw(float original) {
        if ((Object) this == mc.player) {
            RotationManager rotationManager = RotationManager.getInstance();

            if (rotationManager.isNetwork() && rotationManager.getWorkingRotation().getVelocityCorrection().need()) {
                // float g = rotationManager.getWorkingRotation().getYaw() * ((float)Math.PI / 180F);

                // new Vec3d((double)(-MathHelper.sin(g)) * 0.2, 0.0F, (double)MathHelper.cos(g) * 0.2)
                return rotationManager.getWorkingRotation().getYaw();
            }
        }

        return original;
    }

    @ModifyExpressionValue(
            method = "calcGlidingVelocity",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/LivingEntity;getRotationVector()Lnet/minecraft/util/math/Vec3d;"
            )
    )
    private Vec3d onCalcGlidingVelocityRotationVector(Vec3d original) {
        if ((Object) this == mc.player) {
            RotationManager rotationManager = RotationManager.getInstance();

            if (rotationManager.isNetwork() && rotationManager.getWorkingRotation().getVelocityCorrection().need()) {
                return rotationManager.getWorkingRotation().getAngle().getVec();
            }
        }

        return original;
    }

    @ModifyExpressionValue(
            method = "calcGlidingVelocity",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/LivingEntity;getPitch()F"
            )
    )
    private float onCalcGlidingVelocityGetPitch(float original) {
        if ((Object) this == mc.player) {
            RotationManager rotationManager = RotationManager.getInstance();

            if (rotationManager.isNetwork() && rotationManager.getWorkingRotation().getVelocityCorrection().need()) {
                return rotationManager.getWorkingRotation().getPitch();
            }
        }

        return original;
    }
}
