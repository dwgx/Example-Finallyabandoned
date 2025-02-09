package com.example.mod.protocol.forge;


import net.minecraft.network.packet.Packet;

public class ForgeHandshakeV1 extends ForgeHandshake {
    public ForgeHandshakeV1(ForgeProtocol protocol) {
        super(protocol);
    }

    @Override
    public void handle(Packet packet) {

    }

    @Override
    public String name() {
        return "handshake_v1";
    }

    @Override
    public String version() {
        return "FML";
    }
}
