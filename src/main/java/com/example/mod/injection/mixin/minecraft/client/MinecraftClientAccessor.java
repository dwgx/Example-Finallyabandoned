package com.example.mod.injection.mixin.minecraft.client;

import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.minecraft.UserApiService;
import com.mojang.authlib.yggdrasil.ProfileResult;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.SocialInteractionsManager;
import net.minecraft.client.realms.RealmsPeriodicCheckers;
import net.minecraft.client.session.ProfileKeys;
import net.minecraft.client.session.Session;
import net.minecraft.client.session.report.AbuseReportContext;
import net.minecraft.client.texture.PlayerSkinProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.concurrent.CompletableFuture;

@Mixin(MinecraftClient.class)
public interface MinecraftClientAccessor {
    @Mutable
    @Accessor("gameProfileFuture")
    void setGameProfileFuture(CompletableFuture<ProfileResult> future);

    @Mutable
    @Accessor("session")
    void setSession(Session session);

    @Mutable
    @Accessor("authenticationService")
    YggdrasilAuthenticationService getAuthenticationService();

    @Mutable
    @Accessor("authenticationService")
    void setAuthenticationService(YggdrasilAuthenticationService service);

    @Mutable
    @Accessor("sessionService")
    void setSessionService(MinecraftSessionService service);

    @Mutable
    @Accessor("userApiService")
    UserApiService getUserApiService();

    @Mutable
    @Accessor("userApiService")
    void setUserApiService(UserApiService service);

    @Mutable
    @Accessor("skinProvider")
    void setSkinProvider(PlayerSkinProvider provider);

    @Mutable
    @Accessor("socialInteractionsManager")
    void setSocialInteractionsManager(SocialInteractionsManager socialInteractionsManager);

    @Mutable
    @Accessor("profileKeys")
    void setProfileKeys(ProfileKeys profileKeys);

    @Accessor("abuseReportContext")
    void setAbuseReportContext(AbuseReportContext context);

    @Mutable
    @Accessor("realmsPeriodicCheckers")
    void setRealmsPeriodicCheckers(RealmsPeriodicCheckers checkers);
}
