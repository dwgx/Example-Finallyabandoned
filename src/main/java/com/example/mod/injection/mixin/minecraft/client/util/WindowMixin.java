package com.example.mod.injection.mixin.minecraft.client.util;

import com.example.Global;
import com.example.mod.events.client.window.WindowResizeEvent;
import net.minecraft.client.util.Window;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Window.class)
public class WindowMixin {
    @Shadow @Final private long handle;

    /*
    @ModifyArg(
            method = "<init>",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/lwjgl/glfw/GLFW;glfwWindowHint(II)V",
                    ordinal = 2
            ),
            index = 1
    )
    private int onInitWindowHintMajor(int hint) {
        return 4;
    }

    @ModifyArg(
            method = "<init>",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/lwjgl/glfw/GLFW;glfwWindowHint(II)V",
                    ordinal = 3
            ),
            index = 1
    )
    private int onInitWindowHintMinor(int hint) {
        return 6;
    }
     */

    @Inject(
            method = "onWindowSizeChanged",
            at = @At(
                    value = "HEAD"
            ),
            cancellable = true
    )
    private void onWindowSizeChanged(long window, int width, int height, CallbackInfo ci) {
        if (handle == window) {
            WindowResizeEvent event = new WindowResizeEvent(window, width, height);
            Global.getEventBus().post(event).now();

            if (event.isCanceled()) {
                ci.cancel();
            }
        }
    }
}
