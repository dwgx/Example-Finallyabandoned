package com.example.mod.protocol.heypixel.check.c2s;

import com.example.mod.protocol.heypixel.check.HeypixelPacket;
import com.example.mod.protocol.heypixel.utils.BufferHelper;
import com.example.mod.protocol.heypixel.utils.EncryptionUtils;
import com.example.mod.protocol.heypixel.utils.HeypixelVarUtils;
import net.minecraft.network.PacketByteBuf;
import org.msgpack.core.MessageBufferPacker;
import org.msgpack.value.Variable;
//import tech.skidonion.obfuscator.annotations.ControlFlowObfuscation;
//import tech.skidonion.obfuscator.annotations.StringEncryption;


//@StringEncryption
//@ControlFlowObfuscation
public class EncryptionCheckC2SPacket extends HeypixelPacket {
    public int packetId;

    public EncryptionCheckC2SPacket(int i) {
        this.packetId = i;
    }

    @Override
    public void encode(MessageBufferPacker packer) {
        try {
            packer.packValue(new Variable().setStringValue(EncryptionUtils.encryptString(manager, String.valueOf(this.packetId))));
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }

    @Override
    public void encode(PacketByteBuf friendlyByteBuf, BufferHelper bufferHelper) {
        try {
            bufferHelper.writeString(friendlyByteBuf, EncryptionUtils.encryptString(manager, String.valueOf(this.packetId)));
        } catch (Exception e) {
            HeypixelVarUtils.writeVarLong(friendlyByteBuf, this.packetId);
        }
    }
}
