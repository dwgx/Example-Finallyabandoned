package com.example.mod.protocol.nel;

import com.example.mod.features.module.miscellaneous.ModuleProtocol;
import com.example.mod.protocol.nel.all.AllNELSessionsProvider;
import com.example.mod.protocol.nel.echoo.EchooSessionsProvider;
import com.example.mod.protocol.nel.myth.MythSessionsProvider;
import com.example.mod.protocol.nel.prisma.PrismaSessionsProvider;
import com.example.mod.protocol.nel.wnf.WNFSessionsProvider;
import com.example.mod.protocol.nel.zone.ZoneSessionsProvider;
import com.mojang.authlib.GameProfile;




public interface GameSessionProvider {
    static GameSessionProvider get() {
        return switch (ModuleProtocol.getInstance().getNEL()) {
            case Zone -> ZoneSessionsProvider.get();
            case Myth -> MythSessionsProvider.get();
            case WNF -> WNFSessionsProvider.get();
            case Prisma -> PrismaSessionsProvider.get();
            case Echoo -> EchooSessionsProvider.get();
            case Auto -> AllNELSessionsProvider.get();
        };
    }

    void refresh();

    String getPlayerUUID(int port);

    String getUserId(int port);

    String getToken(int port);

    String getPlayerUUID(GameProfile profile);

    String getUserId(GameProfile profile);

    String getToken(GameProfile profile);

    boolean has(int port);

    boolean has(GameProfile profile);

    default boolean has(int port, GameProfile profile) {
        return switch (ModuleProtocol.getInstance().getRequestMode()) {
            case Port -> has(port);
            case UUID -> has(profile);
            case Merge -> {
                if (profile != null) {
                    yield has(profile);
                } else yield has(port);
            }
        };
    }

    default String getPlayerUUID(int port, GameProfile profile) {
        return switch (ModuleProtocol.getInstance().getRequestMode()) {
            case Port -> getUserId(port);
            case UUID -> getUserId(profile);
            case Merge -> {
                if (profile != null) {
                    var ret = getUserId(profile);

                    if (ret != null && !ret.isEmpty()) yield getUserId(profile);
                    else yield getUserId(port);
                } else yield getUserId(port);
            }
        };
    }

    default String getUserId(int port, GameProfile profile) {
        return switch (ModuleProtocol.getInstance().getRequestMode()) {
            case Port -> getUserId(port);
            case UUID -> getUserId(profile);
            case Merge -> {
                if (profile != null) {
                    var ret = getUserId(profile);

                    if (ret != null && !ret.isEmpty()) yield getUserId(profile);
                    else yield getUserId(port);
                } else yield getUserId(port);
            }
        };
    }

    default String getToken(int port, GameProfile profile) {
        return switch (ModuleProtocol.getInstance().getRequestMode()) {
            case Port -> getUserId(port);
            case UUID -> getUserId(profile);
            case Merge -> {
                if (profile != null) {
                    var ret = getUserId(profile);

                    if (ret != null && !ret.isEmpty()) yield getUserId(profile);
                    else yield getUserId(port);
                } else yield getUserId(port);
            }
        };
    }
}
