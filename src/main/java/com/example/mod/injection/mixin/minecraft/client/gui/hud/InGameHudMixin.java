package com.example.mod.injection.mixin.minecraft.client.gui.hud;

import com.example.Global;
import com.example.mod.events.client.render.LayerRenderEvent;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin {
    @Inject(
            method = "render",
            at = @At(
                    value = "TAIL"
            )
    )
    private void onRender(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        LayerRenderEvent event = new LayerRenderEvent(context, tickCounter, tickCounter.getTickDelta(true));
        Global.getEventBus().post(event).now();
    }
}
