package com.example.mod.injection.mixin.minecraft.client.render;

import com.example.mod.features.module.miscellaneous.ModuleTimer;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RenderTickCounter.Dynamic.class)
public class RenderTickCounterDynamicMixin {
    @Shadow private float lastFrameDuration;

    @Inject(
            method = "beginRenderTick(J)I",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/render/RenderTickCounter$Dynamic;lastFrameDuration:F",
                    shift = At.Shift.AFTER
            )
    )
    private void onBeingRenderTick(long timeMillis, CallbackInfoReturnable<Integer> cir) {
        if (ModuleTimer.getInstance().isEnabled()) {
            this.lastFrameDuration *= ModuleTimer.getInstance().getMultiplier();
        }
    }
}
