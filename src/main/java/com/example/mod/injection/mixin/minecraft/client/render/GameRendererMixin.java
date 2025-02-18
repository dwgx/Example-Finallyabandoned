package com.example.mod.injection.mixin.minecraft.client.render;

import com.example.Global;
import com.example.mod.enums.cmd.CmdType;
import com.example.mod.events.client.render.RenderWorldEvent_Backup;
import com.example.mod.managers.RotationManager;
import com.example.mod.utils.player.TraceUtils;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.profiler.Profiler;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Shadow @Final private MinecraftClient client;

    @Inject(
            method = "renderWorld",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V",
                    ordinal = 1,
                    shift = At.Shift.BEFORE
            ),
            locals = LocalCapture.CAPTURE_FAILEXCEPTION
    )
    private void onRenderWorld(RenderTickCounter tickCounter, CallbackInfo ci, @Local Profiler profiler, @Local Camera camera, @Local(ordinal = 1) float tickDelta, @Local(ordinal = 0) Matrix4f matrix4f, @Local MatrixStack matrixStack, @Local(ordinal = 1) Matrix4f matrix4f2, @Local Quaternionf quaternionf, @Local(ordinal = 2) Matrix4f matrix4f3) {
        RenderWorldEvent_Backup event = new RenderWorldEvent_Backup(profiler, tickCounter, camera, tickDelta, matrixStack, matrix4f, matrix4f2, matrix4f3, quaternionf);
        Global.getEventBus().post(event).now();
    }

    @ModifyExpressionValue(
            method = "findCrosshairTarget",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/Entity;raycast(DFZ)Lnet/minecraft/util/hit/HitResult;"
            )
    )
    private HitResult onFindCrosshairTargetTrace(HitResult original, Entity camera, double blockInteractionRange, double entityInteractionRange, float tickDelta) {
        if (!camera.equals(client.player) || (!RotationManager.getInstance().isNetwork() && !RotationManager.getInstance().getCmdType().has(CmdType.RENDER)) || !RotationManager.getInstance().getWorkingRotation().isTrace()) {
            return original;
        }

        return TraceUtils.traceBlock(
                RotationManager.getInstance().getWorkingRotation().getAngle(),
                MathHelper.absMax(blockInteractionRange, entityInteractionRange),
                tickDelta
        );
    }

    @ModifyExpressionValue(
            method = "findCrosshairTarget",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/Entity;getRotationVec(F)Lnet/minecraft/util/math/Vec3d;"
            )
    )
    private Vec3d onFindCrosshairTargetVec(Vec3d original, Entity camera) {
        if (!camera.equals(client.player) || (!RotationManager.getInstance().isNetwork() && !RotationManager.getInstance().getCmdType().has(CmdType.RENDER)) || !RotationManager.getInstance().getWorkingRotation().isTrace()) {
            return original;
        }

        return RotationManager.getInstance().getWorkingRotation().getAngle().getVec();
    }
}
