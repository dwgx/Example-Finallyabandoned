package com.example.mod.injection.mixin.minecraft.client;

import com.example.Global;
import com.example.mod.events.input.FilesDroppedEvent;
import com.example.mod.events.input.MouseButtonEvent;
import com.example.mod.events.input.MouseScrollEvent;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.file.Path;
import java.util.List;

@Mixin(Mouse.class)
public class MouseMixin {
    @Inject(
            method = "onMouseButton",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/util/Window;getHandle()J"),
            cancellable = true
    )
    private void onMouseButton(long window, int button, int action, int mods, CallbackInfo ci) {
        MouseButtonEvent event = new MouseButtonEvent(window, button, action, mods);
        Global.getEventBus().post(event).now();

        if (event.isCanceled()) {
            ci.cancel();
        }
    }

    @Inject(
            method = "onMouseScroll",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/util/Window;getHandle()J"),
            cancellable = true
    )
    private void onMouseScroll(long window, double horizontal, double vertical, CallbackInfo ci) {
        MouseScrollEvent event = new MouseScrollEvent(window, horizontal, vertical);
        Global.getEventBus().post(event).now();

        if (event.isCanceled()) {
            ci.cancel();
        }
    }

    @Inject(
            method = "onFilesDropped",
            at = @At(
                    value = "HEAD"
            ),
            cancellable = true
    )
    private void onFilesDropped(long window, List<Path> paths, int invalidFilesCount, CallbackInfo ci) {
        FilesDroppedEvent event = new FilesDroppedEvent(window, paths, invalidFilesCount);
        Global.getEventBus().post(event).now();

        if (event.isCanceled()) {
            ci.cancel();
        }
    }
}
