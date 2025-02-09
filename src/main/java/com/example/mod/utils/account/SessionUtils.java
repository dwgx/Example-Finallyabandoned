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

        // --- SkinProvider initialization ---
        PlayerSkinProvider currentProvider = mc.getSkinProvider();
        PlayerSkinProvider newProvider = null;
        Executor executor = Util.getMainWorkerExecutor();  // Use Minecraft's main worker executor

        if (currentProvider != null) {
            // If there is already a skin provider, try to re-create one using its cache directory.
            PlayerSkinProvider.FileCache skinCache = ((PlayerSkinProviderAccessor) currentProvider).getSkinCache();
            if (skinCache != null) {
                Path cacheDirectory = ((PlayerSkinProviderFileCacheAccessor) skinCache).getDirectory();
                newProvider = new PlayerSkinProvider(cacheDirectory, sessionService, executor);
            } else {
                System.out.println("【警告】皮肤缓存为空，无法从现有缓存重新初始化皮肤提供程序。");
            }
        }
        if (newProvider == null) {
            Path defaultDir = mc.runDirectory.toPath().resolve("cache/skins");
            newProvider = new PlayerSkinProvider(defaultDir, sessionService, executor);
        }
        if (newProvider != null) {
            accessor.setSkinProvider(newProvider);

            // 安全地更新静态字段（如果存在的话）
            try {
                Field instanceField = null;
                try {
                    instanceField = PlayerSkinProvider.class.getDeclaredField("instance");
                    instanceField.setAccessible(true);
                    instanceField.set(null, newProvider);
                } catch (NoSuchFieldException e) {
                    // 如果找不到静态字段，则跳过更新
                    System.out.println("【警告】没有找到静态字段 'instance'，无法更新 PlayerSkinProvider 实例");
                }

                if (instanceField == null) {
                    // 处理没有找到字段的情况，可以选择记录日志或做其他操作
                    System.out.println("【警告】PlayerSkinProvider 没有静态实例字段，跳过更新。");
                }
            } catch (Exception e) {
                System.out.println("【警告】无法更新玩家皮肤提供程序静态实例：");
                e.printStackTrace();
            }
        } else {
            System.out.println("【错误】无法创建玩家皮肤提供程序实例");
        }
        // --- End SkinProvider initialization ---

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
