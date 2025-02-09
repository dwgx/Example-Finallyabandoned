package com.example.mod.client;

import net.minecraft.client.MinecraftClient;

public interface GameAccessor {
    MinecraftClient mc = MinecraftClient.getInstance();
}
