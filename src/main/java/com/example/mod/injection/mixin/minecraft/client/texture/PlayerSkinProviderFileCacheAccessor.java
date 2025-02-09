package com.example.mod.injection.mixin.minecraft.client.texture;

import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.minecraft.client.texture.PlayerSkinProvider;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Mixin(PlayerSkinProvider.FileCache.class)
public interface PlayerSkinProviderFileCacheAccessor {
    @Mutable
    @Accessor("directory")
    Path getDirectory();

    @Mutable
    @Accessor("type")
    MinecraftProfileTexture.Type getType();

    @Mutable
    @Accessor("hashToTexture")
    Map<String, CompletableFuture<Identifier>> getHashToTexture();
}
