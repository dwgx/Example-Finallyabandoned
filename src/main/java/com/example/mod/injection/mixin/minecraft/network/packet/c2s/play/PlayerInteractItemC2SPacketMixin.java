package com.example.mod.injection.mixin.minecraft.network.packet.c2s.play;

import com.example.mod.datatypes.Rotation;
import com.example.mod.managers.RotationManager;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerInteractItemC2SPacket.class)
public class PlayerInteractItemC2SPacketMixin {
    @Mutable @Shadow @Final private float yaw;
    @Mutable @Shadow @Final private float pitch;

    @Inject(
            method = "<init>(Lnet/minecraft/util/Hand;IFF)V",
            at = @At(
                    value = "RETURN"
            )
    )
    private void onInit(Hand hand, int sequence, float yaw, float pitch, CallbackInfo ci) {
        RotationManager rotationManager = RotationManager.getInstance();
        if (rotationManager.isNetwork()) {
            Rotation current = rotationManager.getWorkingRotation();

            this.yaw = current.getYaw();
            this.pitch = current.getPitch();
        }
    }
}
