package com.example.mod.protocol.heypixel.check.s2c;

import com.example.mod.protocol.heypixel.HeypixelProtocol;
import com.example.mod.protocol.heypixel.check.HeypixelPacket;
import com.example.mod.protocol.heypixel.check.c2s.BlockStateC2SPacket;
import com.example.mod.protocol.heypixel.check.c2s.EncryptionCheckC2SPacket;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import org.msgpack.core.MessagePack;

import java.io.IOException;


//@StringEncryption
//@ControlFlowObfuscation
public class BlockPosS2CPacket extends HeypixelPacket {


    public BlockPos blockPos;

    public BlockPosS2CPacket(PacketByteBuf buf) {
        try (var unpacker = MessagePack.newDefaultUnpacker(helper.readByteArray(buf))) {
            var uuid = unpacker.unpackValue();
            var posX = unpacker.unpackValue();
            var posY = unpacker.unpackValue();
            var posZ = unpacker.unpackValue();
            if (uuid.asStringValue().asString().equals(HeypixelProtocol.get().getPlayerUUID())) {
                this.blockPos = new BlockPos(
                    posX.asIntegerValue().toInt(),
                    posY.asIntegerValue().toInt(),
                    posZ.asIntegerValue().toInt()
                );
            } else {
                this.blockPos = new BlockPos(
                    unpacker.unpackValue().asIntegerValue().toInt(),
                    unpacker.unpackValue().asIntegerValue().toInt(),
                    unpacker.unpackValue().asIntegerValue().toInt()
                );
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void handle(ClientPlayerEntity player) {
        BlockStateC2SPacket.send(manager, this.blockPos);
        new EncryptionCheckC2SPacket(getPacketId()).set(manager).sendMsgpack();
    }
}
