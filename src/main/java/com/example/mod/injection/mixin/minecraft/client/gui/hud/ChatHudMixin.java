package com.example.mod.injection.mixin.minecraft.client.gui.hud;

import com.example.mod.utils.interfaces.IChatHud;
import com.example.mod.utils.interfaces.IChatHudLine;
import com.example.mod.utils.interfaces.IChatHudLineVisible;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ChatHud.class)
public abstract class ChatHudMixin implements IChatHud {
    @Shadow @Final private List<ChatHudLine> messages;
    @Shadow @Final private List<ChatHudLine.Visible> visibleMessages;

    @Shadow
    public abstract void addMessage(Text message);

    @Unique
    private int pendingId = -1;

    @Inject(
            method = "addVisibleMessage",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/List;add(ILjava/lang/Object;)V",
                    shift = At.Shift.AFTER
            )
    )
    private void onAddVisibleMessage(ChatHudLine message, CallbackInfo ci) {
        if (this.pendingId != -1) {
            ((IChatHudLineVisible) (Object) visibleMessages.getFirst()).example$setId(this.pendingId);
        }
    }

    @Inject(
            method = "addMessage(Lnet/minecraft/client/gui/hud/ChatHudLine;)V",
            at = @At(
                    value = "HEAD"
            )
    )
    private void onAddMessage(ChatHudLine message, CallbackInfo ci) {
        if (this.pendingId != -1) {
            ((IChatHudLine) (Object) message).example$setId(this.pendingId);
        }
    }

    @Inject(
            method = "addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;Lnet/minecraft/client/gui/hud/MessageIndicator;)V",
            at = @At(
                    value = "HEAD"
            )
    )
    private void onAddMessage(Text message, MessageSignatureData signatureData, MessageIndicator indicator, CallbackInfo ci) {
        if (this.pendingId != -1) {
            messages.removeIf(msg -> ((IChatHudLine) (Object) msg).example$getId() == this.pendingId);
            visibleMessages.removeIf(msg -> ((IChatHudLineVisible) (Object) msg).example$getId() == this.pendingId);
        }
    }

    @Override
    public void example$addMessage(Text message, int id) {
        this.pendingId = id;
        this.addMessage(message);
        this.pendingId = -1;
    }
}
