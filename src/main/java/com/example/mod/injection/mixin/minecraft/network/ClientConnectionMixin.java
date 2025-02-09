package com.example.mod.injection.mixin.minecraft.network;

import com.example.Global;
import com.example.mod.events.network.HigherPacketEvent;
import com.example.mod.events.network.PacketEvent;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.PacketCallbacks;
import net.minecraft.network.packet.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientConnection.class)
public class ClientConnectionMixin {
    /*
    @Inject(
            method = "exceptionCaught",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/text/Text;translatable(Ljava/lang/String;[Ljava/lang/Object;)Lnet/minecraft/text/MutableText;"
            )
    )
    private void onExceptionCaught(ChannelHandlerContext context, Throwable ex, CallbackInfo ci) {
        ci.cancel();
    }
     */

    @Inject(
            method = "channelRead0(Lio/netty/channel/ChannelHandlerContext;Lnet/minecraft/network/packet/Packet;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/network/ClientConnection;handlePacket(Lnet/minecraft/network/packet/Packet;Lnet/minecraft/network/listener/PacketListener;)V"
            ),
            cancellable = true
    )
    private void onChannelRead0(ChannelHandlerContext channelHandlerContext, Packet<?> packet, CallbackInfo ci) {
        HigherPacketEvent higherEvent = new HigherPacketEvent(HigherPacketEvent.Direction.SEND, packet, (ClientConnection) (Object) this);
        Global.getEventBus().post(higherEvent).now();

        PacketEvent event = new PacketEvent(PacketEvent.Direction.RECEIVE, packet, (ClientConnection) (Object) this);
        Global.getEventBus().post(event).now();

        if (higherEvent.isCanceled() || event.isCanceled()) {
            ci.cancel();
        }
    }

    @Inject(
            method = "send(Lnet/minecraft/network/packet/Packet;)V",
            at = @At(
                    value = "HEAD"
            ),
            cancellable = true
    )
    private void onSend(Packet<?> packet, CallbackInfo ci) {
        PacketEvent event = new PacketEvent(PacketEvent.Direction.SEND, packet, (ClientConnection) (Object) this);
        Global.getEventBus().post(event).now();

        if (event.isCanceled()) {
            ci.cancel();
        }
    }

    @Inject(
            method = "send(Lnet/minecraft/network/packet/Packet;Lnet/minecraft/network/PacketCallbacks;Z)V",
            at = @At(
                    value = "HEAD"
            ),
            cancellable = true
    )
    private void onHigherSend(Packet<?> packet, PacketCallbacks callbacks, boolean flush, CallbackInfo ci) {
        HigherPacketEvent event = new HigherPacketEvent(HigherPacketEvent.Direction.SEND, packet, (ClientConnection) (Object) this);
        Global.getEventBus().post(event).now();

        if (event.isCanceled()) {
            ci.cancel();
        }
    }
}
