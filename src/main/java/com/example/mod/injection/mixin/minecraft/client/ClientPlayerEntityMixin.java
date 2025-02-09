package com.example.mod.injection.mixin.minecraft.client;

import com.example.Global;
import com.example.event.Event;
import com.example.mod.events.client.network.MovementMultiplierEvent;
import com.example.mod.events.network.LocalTickMovementEvent;
import com.example.mod.events.network.NetworkMovementEvent;
import com.example.mod.managers.RotationManager;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.input.Input;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin {
    @Shadow public Input input;

    @Shadow public abstract void tick();

    @Inject(
            method = "sendMovementPackets",
            at = @At(
                    value = "HEAD"
            ),
            cancellable = true
    )
    private void onPreSendMovementPackets(CallbackInfo ci) {
        NetworkMovementEvent event = new NetworkMovementEvent(Event.State.PRE);
        Global.getEventBus().post(event).now();

        if (event.isCanceled()) {
            ci.cancel();
        }
    }

    @Inject(
            method = "sendMovementPackets",
            at = @At(
                    value = "RETURN"
            )
    )
    private void onPostSendMovementPackets(CallbackInfo ci) {
        NetworkMovementEvent event = new NetworkMovementEvent(Event.State.POST);
        Global.getEventBus().post(event).now();
    }

    @ModifyExpressionValue(
            method = {
                    "tick",
                    "sendMovementPackets"
            },
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/network/ClientPlayerEntity;getYaw()F"
            )
    )
    private float onNetworkYaw(float original) {
        RotationManager rotationManager = RotationManager.getInstance();
        if (RotationManager.getInstance().isNetwork()) {
            return rotationManager.getWorkingRotation().getYaw();
        }

        return original;
    }

    @ModifyExpressionValue(
            method = {
                    "tick",
                    "sendMovementPackets"
            },
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/network/ClientPlayerEntity;getPitch()F"
            )
    )
    private float onNetworkPitch(float original) {
        RotationManager rotationManager = RotationManager.getInstance();
        if (RotationManager.getInstance().isNetwork()) {
            return rotationManager.getWorkingRotation().getPitch();
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
    private void onPreTickMovement(CallbackInfo ci) {
        LocalTickMovementEvent event = new LocalTickMovementEvent(Event.State.PRE);
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
        LocalTickMovementEvent event = new LocalTickMovementEvent(Event.State.POST);
        Global.getEventBus().post(event).now();
    }

    @Inject(
            method = "tickMovement",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/network/ClientPlayerEntity;ticksLeftToDoubleTapSprint:I",
                    shift = At.Shift.AFTER,
                    ordinal = 3
            )
    )
    private void onTickMovementUsingItem(CallbackInfo ci) {
        Input input = this.input;

        input.movementForward /= 0.2f;
        input.movementSideways /= 0.2f;

        MovementMultiplierEvent event = new MovementMultiplierEvent(0.2f, 0.2f);
        Global.getEventBus().post(event).now();

        if (!event.isCanceled()) {
            input.movementForward *= event.getForwardMultiplier();
            input.movementSideways *= event.getSidewaysMultiplier();
        }
    }
}
