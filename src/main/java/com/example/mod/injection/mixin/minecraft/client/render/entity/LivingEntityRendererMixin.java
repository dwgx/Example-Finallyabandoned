package com.example.mod.injection.mixin.minecraft.client.render.entity;

import com.example.Global;
import com.example.event.Event;
import com.example.mod.events.client.render.entity.RenderStateUpdateEvent;
import com.example.mod.managers.RenderStateManager;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.example.mod.client.GameAccessor.mc;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<T extends LivingEntity, S extends LivingEntityRenderState, M extends EntityModel<? super S>> {
    @Inject(
            method = "updateRenderState(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/client/render/entity/state/LivingEntityRenderState;F)V",
            at = @At(
                    value = "HEAD"
            )
    )
    private void onUpdateRenderStateHead(T livingEntity, S livingEntityRenderState, float f, CallbackInfo ci) {
        RenderStateUpdateEvent event = new RenderStateUpdateEvent(Event.State.PRE, livingEntity, livingEntityRenderState, f, livingEntity.equals(mc.player) ? RenderStateUpdateEvent.RenderStateType.LOCAL_PLAYER : RenderStateUpdateEvent.RenderStateType.LIVING_ENTITY);
        Global.getEventBus().post(event).now();
    }

    @Inject(
            method = "updateRenderState(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/client/render/entity/state/LivingEntityRenderState;F)V",
            at = @At(
                    value = "TAIL"
            )
    )
    private void onUpdateRenderStateTail(T livingEntity, S livingEntityRenderState, float f, CallbackInfo ci) {
        RenderStateUpdateEvent event = new RenderStateUpdateEvent(Event.State.POST, livingEntity, livingEntityRenderState, f, livingEntity.equals(mc.player) ? RenderStateUpdateEvent.RenderStateType.LOCAL_PLAYER : RenderStateUpdateEvent.RenderStateType.LIVING_ENTITY);
        Global.getEventBus().post(event).now();

        RenderStateManager.getInstance().setLocalPlayerRenderState(livingEntityRenderState);
    }
}
