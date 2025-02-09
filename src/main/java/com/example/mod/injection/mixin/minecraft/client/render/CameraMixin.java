package com.example.mod.injection.mixin.minecraft.client.render;

import com.example.mod.datatypes.Rotation;
import com.example.mod.enums.cmd.CmdType;
import com.example.mod.managers.RotationManager;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public abstract class CameraMixin {
    @Shadow
    protected abstract void setRotation(float yaw, float pitch);

    @Inject(
            method = "update",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/Camera;setPos(DDD)V",
                    shift = At.Shift.AFTER
            )
    )
    private void onUpdate(BlockView area, Entity focusedEntity, boolean thirdPerson, boolean inverseView, float tickDelta, CallbackInfo ci) {
        RotationManager rotationManager = RotationManager.getInstance();

        if (rotationManager.getCmdType().has(CmdType.LOCAL)) {
            Rotation current = rotationManager.getWorkingRotation();
            Rotation previous = rotationManager.getPreviousRotation();

            if (previous == null) {
                previous = current;
            }

            setRotation(
                    MathHelper.lerp(tickDelta, previous.getYaw(), current.getYaw()),
                    MathHelper.lerp(tickDelta, previous.getPitch(), current.getPitch())
            );
        }
    }
}
