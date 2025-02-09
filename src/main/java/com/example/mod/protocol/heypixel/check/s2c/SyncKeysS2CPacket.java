package com.example.mod.protocol.heypixel.check.s2c;

import com.example.mod.protocol.heypixel.check.HeypixelPacket;
import com.example.mod.protocol.heypixel.check.c2s.EncryptionCheckC2SPacket;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.PacketByteBuf;

import java.util.Arrays;


//@StringEncryption
//@ControlFlowObfuscation
public class SyncKeysS2CPacket extends HeypixelPacket {


    public byte[] keyListB;


    public byte[] keyListC;


    public byte[] keyB;


    public byte[] keyA;


    public byte[] keyC;


    public byte[] keyListA;


    public SyncKeysS2CPacket(PacketByteBuf buf) {
        this.keyA = helper.readByteArray(buf);
        this.keyListA = helper.readByteArray(buf);
        this.keyB = helper.readByteArray(buf);
        this.keyListB = helper.readByteArray(buf);
        this.keyC = helper.readByteArray(buf);
        this.keyListC = helper.readByteArray(buf);
    }

    @Override
    public void handle(ClientPlayerEntity player) {
        manager.processSyncKeys(this);
        new EncryptionCheckC2SPacket(getPacketId()).set(manager).sendVanilla();
    }

    public String toString() {
        return "SyncKeysPacket(keyA=" +
            Arrays.toString(this.keyA) +
            ", keyListA=" + Arrays.toString(this.keyListA) + ", " +
            "keyB=" + Arrays.toString(this.keyB) +
            ", keyListB=" + Arrays.toString(this.keyListB) +
            ", keyC=" + Arrays.toString(this.keyC) +
            ", keyListC=" + Arrays.toString(this.keyListC) + ")";
    }
}
