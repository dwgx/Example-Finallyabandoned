package com.example.mod.protocol.heypixel.check.s2c;


import com.example.mod.protocol.heypixel.check.HeypixelPacket;
import com.example.mod.protocol.heypixel.check.c2s.EncryptionCheckC2SPacket;
import net.minecraft.network.PacketByteBuf;
import org.msgpack.core.MessagePack;

import java.io.IOException;
import java.util.UUID;

@Deprecated
public class UnknownS2CPacket extends HeypixelPacket {

    public UUID uuid;
    public Long field_9461;

    public UnknownS2CPacket(PacketByteBuf buf) {
        try (var unpacker = MessagePack.newDefaultUnpacker(helper.readByteArray(buf))) {
            var obj = unpacker.unpackValue();
            var obj1 = unpacker.unpackValue();
            this.uuid = UUID.fromString(obj.asRawValue().asString());
            this.field_9461 = obj1.asIntegerValue().asLong();
            new EncryptionCheckC2SPacket(getPacketId()).set(manager).sendMsgpack();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
