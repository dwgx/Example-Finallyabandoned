package com.example.mod.utils.player;

import com.example.mod.features.ClientSettings;
import com.example.mod.utils.interfaces.IChatHud;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.Optional;

import static com.example.mod.client.GameAccessor.mc;

public class ChatUtils {
    private ChatUtils() {}

    public static void send(String content) {
        Optional.ofNullable(mc.player)
                .ifPresent(player -> player.networkHandler.sendChatMessage(content));
    }

    public static void display(Text prefix, Text message, boolean overlay) {
        MutableText formatted = Text.empty().append(prefix).append(message);

        Optional.ofNullable(mc.player)
                .ifPresent(player -> player.sendMessage(formatted, overlay));
    }

    public static void display(Text prefix, Text message) {
        display(prefix, message, false);
    }

    public static void display(Text message) {
        display(ClientSettings.CHAT_PREFIX.getValue(), message, false);
    }

    public static void display(Text prefix, Text message, int id) {
        MutableText formatted = Text.empty().append(prefix).append(message);

        Optional.ofNullable(mc.inGameHud)
                .ifPresent(inGameHud -> ((IChatHud) inGameHud.getChatHud()).example$addMessage(formatted, id));
    }

    public static void display(Text message, int id) {
        display(ClientSettings.CHAT_PREFIX.getValue(), message, id);
    }
}
