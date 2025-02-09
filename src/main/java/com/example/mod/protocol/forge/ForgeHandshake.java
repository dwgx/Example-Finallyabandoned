package com.example.mod.protocol.forge;

import net.minecraft.network.packet.Packet;

public abstract class ForgeHandshake {
    protected final ForgeProtocol protocol;

    protected ForgeHandshake(ForgeProtocol protocol) {
        this.protocol = protocol;
    }

    public void init() {}
    public void reload() {}
    public void tick() {}

    public abstract void handle(Packet packet);
    public abstract String name();
    public abstract String version();
}
