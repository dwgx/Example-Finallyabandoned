package com.example.mod.protocol;


import com.example.event.Event;
import com.example.mod.events.client.GameTickEvent;
import io.netty.buffer.Unpooled;
import net.engio.mbassy.listener.Handler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.packet.c2s.common.CustomPayloadC2SPacket;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

import static com.example.mod.client.GameAccessor.mc;

public class NetPayload  {
    public static final List<Runnable> preTasks = new ArrayList<>();

    public static void pre(Runnable task) {
        preTasks.add(task);
    }

    @Handler
    private static void onTick(GameTickEvent event) {
        if (event.getState() == Event.State.PRE) {
            if (canUpdate() && mc.getNetworkHandler() != null) {
                for (Runnable task : preTasks) {
                    task.run();
                }
                preTasks.clear();
            }
        }
    }

    public static final List<Identifier> REGISTERED_CHANNELS = new ArrayList<>();

    public static void register(Identifier channel) {
        if (REGISTERED_CHANNELS.contains(channel)) return;
        REGISTERED_CHANNELS.add(channel);
    }

    public static void send(Identifier channel, PacketByteBuf buf) {
        try {
            if (mc.getNetworkHandler() != null) {
                PayloadPacket packet = new PayloadPacket(channel, buf);
                CustomPayloadC2SPacket c2sPacket = new CustomPayloadC2SPacket(packet);
                mc.getNetworkHandler().sendPacket(c2sPacket);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static class PayloadPacket implements CustomPayload {
        private final Identifier channel;
        public final PacketByteBuf buf;

        // 构造函数中初始化 buf
        public PayloadPacket(Identifier channel, PacketByteBuf buf) {
            this.channel = channel;
            this.buf = buf;
        }

        @Override
        public Id<? extends CustomPayload> getId() {
            return new Id<>(this.channel); // 返回正确的 channel
        }


        public void write(PacketByteBuf byteBuf) {
            // 确保这里的 buf 被正确传递
            byteBuf.writeBytes(this.buf); // 使用 PacketByteBuf 的 writeBytes 方法
        }

    }



    public static void write(PacketByteBuf byteBuf, PacketByteBuf data) {
        byteBuf.writeBytes(data.copy());
    }

    public static PacketByteBuf read(PacketByteBuf byteBuf, int maxSize) {
        assertSize(byteBuf, maxSize);
        PacketByteBuf newBuf = new PacketByteBuf(Unpooled.buffer());
        newBuf.writeBytes(byteBuf.copy());
        byteBuf.skipBytes(byteBuf.readableBytes());
        return newBuf;
    }

    private static void assertSize(PacketByteBuf buf, int maxSize) {
        int size = buf.readableBytes();
        if (size < 0 || size > maxSize) {
            throw new IllegalArgumentException("Payload may not be larger than " + maxSize + " bytes");
        }
    }
    public static boolean canUpdate() {
        return mc != null && mc.player != null && mc.world != null;
    }

}
