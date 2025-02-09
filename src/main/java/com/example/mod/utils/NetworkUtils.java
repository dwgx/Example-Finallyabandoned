package com.example.mod.utils;

import net.minecraft.network.packet.Packet;

import static com.example.mod.client.GameAccessor.mc;

public class NetworkUtils {
    public static void sendPacket(Packet<?> packet) {
        mc.getNetworkHandler().getConnection().send(packet);
    }

    public static void sendPacketSilently(Packet<?> packet) {
        mc.getNetworkHandler().getConnection().send(packet, null);
    }
}
