package com.example.mod.protocol.heypixel.check.s2c;

import com.example.mod.protocol.heypixel.check.HeypixelPacket;
import com.example.mod.protocol.heypixel.check.c2s.EncryptionCheckC2SPacket;
import com.example.mod.protocol.heypixel.check.c2s.ReflectDataC2SPacket;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.PacketByteBuf;
import org.msgpack.core.MessagePack;

import java.util.UUID;


public class ReflectCheckS2CPacket extends HeypixelPacket {
    public UUID uuid;
    public String data;

    public ReflectCheckS2CPacket(PacketByteBuf buf) {
        try (var unpacker = MessagePack.newDefaultUnpacker(helper.readByteArray(buf))) {
            var uuid = unpacker.unpackValue();
            var data = unpacker.unpackValue();
            if (uuid == null) {
                throw new NullPointerException("Rid is null");
            }
            this.uuid = UUID.fromString(uuid.asStringValue().asString());
            this.data = data.asStringValue().asString();
        } catch (Throwable th) {
            if (this.uuid == null) {
                this.uuid = UUID.randomUUID();
            }
            if (this.data == null) {
                this.data = UUID.randomUUID().toString();
            }
            throw new RuntimeException(th);
        }
    }

    @Override
    public void handle(ClientPlayerEntity player) {
        if (this.data.equals("SCI")) { // Send Client Info
            manager.sendClientInfo();
        } else {
            ReflectDataC2SPacket.sendCheckPacket(manager, this.data);
            new EncryptionCheckC2SPacket(getPacketId()).set(manager).sendMsgpack();
        }
    }
}
