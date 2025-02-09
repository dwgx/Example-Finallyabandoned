package com.example.mod.injection.mixin.minecraft.client;

import com.example.Global;
import com.example.mod.events.input.KeyboardEvent;
import com.example.utils.input.KeyAction;
import net.minecraft.client.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Keyboard.class)
public class KeyboardMixin {
    @Inject(
            method = "onKey",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/util/Window;getHandle()J",
                    ordinal = 0
            ),
            cancellable = true
    )
    private void onKey(long window, int key, int scancode, int action, int modifiers, CallbackInfo ci) {
        KeyboardEvent event = new KeyboardEvent(window, key, scancode, modifiers, KeyAction.of(action));
        Global.getEventBus().post(event).now();

        if (event.isCanceled()) {
            ci.cancel();
        }
    }
}
