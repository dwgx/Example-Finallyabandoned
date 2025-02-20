package com.example.mod.utils.account;

import com.example.mod.injection.mixin.minecraft.client.MinecraftClientAccessor;
import com.example.mod.injection.mixin.minecraft.client.texture.PlayerSkinProviderAccessor;
import com.example.mod.injection.mixin.minecraft.client.texture.PlayerSkinProviderFileCacheAccessor;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.ServicesKeyType;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.util.UndashedUuid;
import net.minecraft.client.network.SocialInteractionsManager;
import net.minecraft.client.realms.RealmsClient;
import net.minecraft.client.realms.RealmsPeriodicCheckers;
import net.minecraft.client.session.ProfileKeys;
import net.minecraft.client.session.Session;
import net.minecraft.client.session.report.AbuseReportContext;
import net.minecraft.client.session.report.ReporterEnvironment;
import net.minecraft.client.texture.PlayerSkinProvider;
import net.minecraft.network.encryption.SignatureVerifier;
import net.minecraft.util.Util;
import net.minecraft.util.Uuids;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.Executor;

import static com.example.mod.client.GameAccessor.mc;

public class SessionUtils {

    public static Session offline(String username) {
        return new Session(username, Uuids.getOfflinePlayerUuid(username), "", Optional.empty(), Optional.empty(), Session.AccountType.MOJANG);
    }

    public static Session microsoft(String username, String uuid, String accessToken) {
        return new Session(username, UndashedUuid.fromStringLenient(uuid), accessToken, Optional.empty(), Optional.empty(), Session.AccountType.MSA);
    }

    public static void update(Session session) {
        MinecraftClientAccessor accessor = (MinecraftClientAccessor) mc;
        accessor.setSession(session);

        YggdrasilAuthenticationService authenticationService = new YggdrasilAuthenticationService(mc.getNetworkProxy());

        accessor.setAuthenticationService(authenticationService);
        SignatureVerifier.create(authenticationService.getServicesKeySet(), ServicesKeyType.PROFILE_KEY);
        MinecraftSessionService sessionService = authenticationService.createMinecraftSessionService();
        accessor.setSessionService(sessionService);

        PlayerSkinProvider.FileCache skinCache = ((PlayerSkinProviderAccessor) mc.getSkinProvider()).getSkinCache();
        accessor.setSkinProvider(new PlayerSkinProvider(
                ((PlayerSkinProviderFileCacheAccessor) skinCache).getDirectory(),
                sessionService,
                mc
        ));

        accessor.setUserApiService(accessor.getAuthenticationService().createUserApiService(session.getAccessToken()));
        accessor.setSocialInteractionsManager(new SocialInteractionsManager(mc, accessor.getUserApiService()));
        accessor.setProfileKeys(ProfileKeys.create(accessor.getUserApiService(), session, mc.runDirectory.toPath()));
        accessor.setAbuseReportContext(AbuseReportContext.create(ReporterEnvironment.ofIntegratedServer(), accessor.getUserApiService()));
        accessor.setRealmsPeriodicCheckers(new RealmsPeriodicCheckers(RealmsClient.create()));
    }

    public static void logout() {
        MinecraftClientAccessor accessor = (MinecraftClientAccessor) mc;
        accessor.setSession(null);
        accessor.setAuthenticationService(null);
        accessor.setSessionService(null);
        accessor.setSkinProvider(null);
        accessor.setUserApiService(null);
        accessor.setSocialInteractionsManager(null);
        accessor.setProfileKeys(null);
        accessor.setAbuseReportContext(null);
        accessor.setRealmsPeriodicCheckers(null);

        // 清理缓存（如果有缓存）
        PlayerSkinProvider currentProvider = mc.getSkinProvider();
        if (currentProvider != null) {
            PlayerSkinProvider.FileCache skinCache = ((PlayerSkinProviderAccessor) currentProvider).getSkinCache();
            if (skinCache != null) {
                // 手动删除缓存目录中的文件
                Path cacheDirectory = ((PlayerSkinProviderFileCacheAccessor) skinCache).getDirectory();
                try {
                    // 删除缓存目录中的所有文件
                    Files.walk(cacheDirectory)
                            .map(Path::toFile)
                            .forEach(file -> {
                                if (!file.isDirectory()) {
                                    file.delete();
                                }
                            });
                    System.out.println("【信息】玩家皮肤缓存已清理");
                } catch (IOException e) {
                    System.out.println("【警告】无法清理皮肤缓存:");
                    e.printStackTrace();
                }
            }
        }
    }
}
