package com.example.mod.protocol.heypixel.check.c2s;


import com.example.mod.protocol.heypixel.check.HeypixelPacket;
import net.minecraft.network.PacketByteBuf;

@Deprecated
public class EmptyC2SPacket extends HeypixelPacket {
    public EmptyC2SPacket(PacketByteBuf friendlyByteBuf) {
    }
}
