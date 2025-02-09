package com.example.mod.protocol.heypixel.check.s2c;

import com.example.mod.protocol.heypixel.check.HeypixelPacket;
import com.example.mod.protocol.heypixel.check.c2s.EncryptionCheckC2SPacket;
import com.example.mod.protocol.heypixel.utils.HeypixelVarUtils;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.PacketByteBuf;

import java.util.ArrayList;
import java.util.List;

@Deprecated
public class PlayerListDataS2CPacket extends HeypixelPacket {
    public List<byte[]> players = new ArrayList<>();
    public int index;

    public PlayerListDataS2CPacket(PacketByteBuf buf) {
        int len = HeypixelVarUtils.readVarInt(buf);
        for (int i = 0; i < len; i++) {
            this.players.add(helper.readByteArray(buf));
        }
        this.index = HeypixelVarUtils.readVarInt(buf);
    }


    @Override
    public void handle(ClientPlayerEntity player) {
        manager.players = this.players;
        manager.playerIndex = this.index;

        new EncryptionCheckC2SPacket(getPacketId()).set(manager).sendVanilla();
    }
}
