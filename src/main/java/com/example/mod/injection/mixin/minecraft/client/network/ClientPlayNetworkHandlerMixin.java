package com.example.mod.injection.mixin.minecraft.client.network;

import com.example.Global;
import com.example.mod.events.client.network.SendMessageEvent;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {
    @Inject(
            method = "sendChatMessage",
            at = @At(
                    value = "HEAD"
            ),
            cancellable = true
    )
    private void sendChatMessage(String content, CallbackInfo ci) {
        SendMessageEvent event = new SendMessageEvent(content);
        Global.getEventBus().post(event).now();

        if (event.isCanceled()) {
            ci.cancel();
        }
    }
}
