package com.example.mod.protocol.forge;

import net.minecraft.network.packet.Packet;

public class ForgeHandshakeV2 extends ForgeHandshake {
    private final int Packet_S2CModList = 1;
    private final int Packet_C2SModListReply = 2;
    private final int Packet_S2CRegistry = 3;
    private final int Packet_S2CConfigData = 4;
    private final int Packet_C2SAcknowledge = 99;

    public ForgeHandshakeV2(ForgeProtocol protocol) {
        super(protocol);
    }

    @Override
    public void handle(Packet packet) {

    }

    @Override
    public String name() {
        return "handshake_v2";
    }

    @Override
    public String version() {
        return "FML2";
    }
}
