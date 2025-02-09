package com.example.mod.injection.mixin.minecraft.client.render;

import com.example.Global;
import com.example.mod.events.client.render.WorldRenderEvent;
import com.example.mod.features.module.visual.ModuleBlockOutline;
import com.example.mod.storage.event.WorldRenderStorage;
import net.minecraft.client.render.*;
import net.minecraft.client.util.ObjectAllocator;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.awt.*;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {
    @Final @Shadow private BufferBuilderStorage bufferBuilders;

    @Inject(
            method = "render",
            at = @At(value = "HEAD"),
            cancellable = true
    )
    private void onPreRender(
            ObjectAllocator allocator,
            RenderTickCounter tickCounter,
            boolean renderBlockOutline,
            Camera camera,
            GameRenderer gameRenderer,
            Matrix4f positionMatrix,
            Matrix4f projectionMatrix,
            CallbackInfo ci
    ) {
        WorldRenderEvent event = new WorldRenderEvent(
                allocator,
                tickCounter,
                renderBlockOutline,
                camera,
                gameRenderer,
                positionMatrix,
                projectionMatrix,
                this.bufferBuilders
        );

        WorldRenderStorage.setEvent(event);
        WorldRenderStorage.setStage(WorldRenderStorage.Stage.START);
        Global.getEventBus().post(event).now();

        if (event.isCanceled()) {
            ci.cancel();
        }
    }

    @Inject(
            method = "render",
            at = @At(value = "TAIL")
    )
    private void onPostRender(
            ObjectAllocator allocator,
            RenderTickCounter tickCounter,
            boolean renderBlockOutline,
            Camera camera,
            GameRenderer gameRenderer,
            Matrix4f positionMatrix,
            Matrix4f projectionMatrix,
            CallbackInfo ci
    ) {
        WorldRenderEvent event = WorldRenderStorage.getEvent();

        if (event != null) {
            WorldRenderStorage.setStage(WorldRenderStorage.Stage.END);
            Global.getEventBus().post(event).now();
        }
    }

    @Inject(
            method = "method_62214",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/WorldRenderer;renderLayer(Lnet/minecraft/client/render/RenderLayer;DDDLorg/joml/Matrix4f;Lorg/joml/Matrix4f;)V",
                    ordinal = 2,
                    shift = At.Shift.AFTER
            ),
            cancellable = true
    )
    private void onBeforeRenderEntities(CallbackInfo ci) {
        WorldRenderEvent event = WorldRenderStorage.getEvent();

        if (event != null) {
            WorldRenderStorage.setStage(WorldRenderStorage.Stage.BEFORE_ENTITIES);
            Global.getEventBus().post(event).now();

            if (event.isCanceled()) {
                ci.cancel();
            }
        }
    }

    @Inject(
            method = "method_62214",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/WorldRenderer;checkEmpty(Lnet/minecraft/client/util/math/MatrixStack;)V",
                    ordinal = 0,
                    shift = At.Shift.AFTER
            ),
            cancellable = true
    )
    private void onAfterRenderEntities(CallbackInfo ci) {
        WorldRenderEvent event = WorldRenderStorage.getEvent();

        if (event != null) {
            WorldRenderStorage.setStage(WorldRenderStorage.Stage.AFTER_ENTITIES);
            Global.getEventBus().post(event).now();

            if (event.isCanceled()) {
                ci.cancel();
            }
        }
    }

    @ModifyArgs(
            method = "drawBlockOutline",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/VertexRendering;drawOutline(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;Lnet/minecraft/util/shape/VoxelShape;DDDI)V"
            )
    )
    private void onDrawBlockOutlineArgs(Args args) {
        // MatrixStack matrices, VertexConsumer vertexConsumer, Entity entity, double cameraX, double cameraY, double cameraZ, BlockPos pos, BlockState state, int color

        if (!ModuleBlockOutline.getInstance().isEnabled()) {
            return;
        }

        Color color = ModuleBlockOutline.getInstance().getColor();

        args.set(6, (color.getAlpha() << 24) | (color.getRed() << 16) | (color.getGreen() << 8) | color.getBlue());
    }
}
