package com.example.mod.protocol;

import com.example.mod.events.client.world.AddEntityEvent;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.Packet;
import net.minecraft.text.Text;

import java.io.File;

import static com.example.mod.client.GameAccessor.mc;


//@StringEncryption
//@ControlFlowObfuscation
public interface IProtocol {
    File FOLDER = new File(MinecraftClient.getInstance().runDirectory,"Example").toPath().resolve("protocol").toFile();

    void init();

    void reload();

    String name();

    boolean onPacket(Packet packet);

    void onMessage(Text text);

    void onAddEntity(AddEntityEvent e);

    void tick();


    default GameProfile getPlayerProfile() {
        if (mc.player == null || mc.player.getPlayerListEntry() == null) {
            if (mc.player != null && mc.player.getGameProfile() != null) return mc.player.getGameProfile();
        }
        return mc.player.getPlayerListEntry().getProfile();
    }
}
