package com.example.mod.protocol.heypixel.check;

import com.example.mod.protocol.NetPayload;
import com.example.mod.protocol.heypixel.HeypixelHandler;
import com.example.mod.protocol.heypixel.HeypixelProtocol;
import com.example.mod.protocol.heypixel.check.c2s.*;
import com.example.mod.protocol.heypixel.check.s2c.*;
import com.example.mod.protocol.heypixel.utils.BufferHelper;
import com.example.mod.protocol.heypixel.utils.HeypixelVarUtils;
import io.netty.buffer.Unpooled;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import org.msgpack.core.MessageBufferPacker;
import org.msgpack.core.MessagePack;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class HeypixelPacket {
    protected final BufferHelper helper = new BufferHelper();

    public static Map<Integer, Function<PacketByteBuf, HeypixelPacket>> s2cMap = new HashMap<>();
    public static Map<Class<? extends HeypixelPacket>, Integer> c2sMap = new HashMap<>();

    public HeypixelHandler manager;

//    @NativeObfuscation.Inline
    public static void init() {
        registerC2S(0, EmptyC2SPacket.class);
        registerC2S(1, GameDataC2SPacket.class);
        registerC2S(2, HitResultC2SPacket.class);
        registerC2S(3, ReflectDataC2SPacket.class);
        registerC2S(4, EncryptionCheckC2SPacket.class);
        registerC2S(5, BlockStateC2SPacket.class);
        registerC2S(6, ClassesC2SPacket.class);

        registerS2C(100, UnknownS2CPacket::new);
        registerS2C(101, HeypixelKeysS2CPacket::new);
        registerS2C(102, TokenDataS2CPacket::new);
        registerS2C(103, SyncKeysS2CPacket::new);
        registerS2C(104, ReflectDataC2SPacket::new);
        registerS2C(105, PlayerListDataS2CPacket::new);
        registerS2C(106, BlockPosS2CPacket::new);
        registerS2C(107, NeteaseCheckS2CPacket::new);
    }

    private static void registerC2S(int id, Class<? extends HeypixelPacket> cls) {
        c2sMap.put(cls, id);
    }

    private static void registerS2C(int id, Function<PacketByteBuf, HeypixelPacket> function) {
        s2cMap.put(id, function);
    }

    public static Function<PacketByteBuf, HeypixelPacket> newS2CInstance(int i) {
        return s2cMap.get(i);
    }

    public void encode(PacketByteBuf buf, BufferHelper helper) {
    }

    public void encode(MessageBufferPacker packer) throws IOException {
    }

    public void handle(ClientPlayerEntity player) {
//        throw new UnsupportedOperationException("This packet ( " + getPacketId() + ") does not implement a client side handler.");
    }

    public PacketByteBuf buffer() {
        var buf = new PacketByteBuf(Unpooled.buffer());
        HeypixelVarUtils.writeUnsignedInt(buf, getPacketId());
        return buf;
    }

    public void sendMsgpack() {
        
        try (var buffer = MessagePack.newDefaultBufferPacker()) {
            buffer.packString(HeypixelProtocol.get().clientId.toString());
            buffer.packString(HeypixelProtocol.get().getPlayerUUID());
            encode(buffer);

            var buf = new PacketByteBuf(Unpooled.buffer());
            HeypixelVarUtils.writeUnsignedInt(buf, getPacketId());
            helper.writeByteArray(buf, buffer.toByteArray());
            System.out.println("dwgx heypixel: send check packet " + getClass().getName());
            NetPayload.send(HeypixelProtocol.CHECK_CHANNEL, buf);
            System.out.println("heypixel: send check packet " + getClass().getName());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendVanilla() {
        
        try {
            var decoder = manager.msgDecoder;

            Identifier channel;
            if (manager.players == null || manager.players.isEmpty()) {
                channel = HeypixelProtocol.CHECK_CHANNEL;
            } else {
                channel = Identifier.of(HeypixelProtocol.MOD_ID,
                        decoder.decode((byte[]) manager.players.get(manager.playerIndex))
                );
                NetPayload.register(channel);
            }

            var buf = buffer();
            encode(buf, helper);
            System.out.println("dwgx heypixel: send check packet vanilla");
            NetPayload.send(channel, buf);
            System.out.println("heypixel: send check packet vanilla");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getPacketId() {
        return c2sMap.getOrDefault(getClass(), -1);
    }

    public <T extends HeypixelPacket> T set(HeypixelHandler manager) {
        this.manager = manager;
        return (T) this;
    }
}
