package com.example.mod.injection.mixin.minecraft.client.texture;

import net.minecraft.client.texture.PlayerSkinProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(PlayerSkinProvider.class)
public interface PlayerSkinProviderAccessor {
    @Mutable
    @Accessor("skinCache")
    PlayerSkinProvider.FileCache getSkinCache();

    @Mutable
    @Accessor("capeCache")
    PlayerSkinProvider.FileCache getCapeCache();

    @Mutable
    @Accessor("elytraCache")
    PlayerSkinProvider.FileCache getElytraCache();
}
