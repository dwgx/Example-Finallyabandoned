package com.example.mod.protocol.forge;


import com.example.mod.events.client.world.AddEntityEvent;
import com.example.mod.protocol.IProtocol;
import com.example.mod.protocol.NetPayload;
import net.minecraft.network.packet.Packet;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ForgeProtocol implements IProtocol {
    private final ForgeHandshake handshake;

    public static final Identifier REGISTER_CHANNEL = Identifier.of("minecraft", "register");

    public ForgeProtocol(boolean fml2) {
        this.handshake = fml2 ? new ForgeHandshakeV2(this) : new ForgeHandshakeV1(this);
    }

    @Override
    public void init() {
        NetPayload.register(REGISTER_CHANNEL);

        handshake.init();
    }

    @Override
    public void reload() {
        handshake.reload();
    }

    @Override
    public String name() {
        return "Forge";
    }

    @Override
    public boolean onPacket(Packet packet) {


        return false;
    }

    @Override
    public void tick() {
        handshake.tick();
    }

    @Override
    public void onMessage(Text text) {
    }

    @Override
    public void onAddEntity(AddEntityEvent e) {
    }


}
