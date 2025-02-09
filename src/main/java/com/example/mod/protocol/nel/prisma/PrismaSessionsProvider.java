package com.example.mod.protocol.nel.prisma;

import com.example.mod.features.module.miscellaneous.ModuleProtocol;
import com.example.mod.protocol.Http;
import com.example.mod.protocol.nel.GameSessionProvider;
import com.google.gson.annotations.SerializedName;
import com.mojang.authlib.GameProfile;

import java.util.ArrayList;
import java.util.List;

public class PrismaSessionsProvider implements GameSessionProvider {
    private static PrismaSessionsProvider sInstance;
    public final List<PrismaSessionInfo> sessions = new ArrayList<>();

    public static PrismaSessionsProvider get() {
        if (sInstance == null) {
            sInstance = new PrismaSessionsProvider();
        }
        return sInstance;
    }

    @Override
    public void refresh() {
        try {
            PrismaResponse response = Http.get("http://127.0.0.1:" + ModuleProtocol.getInstance().getPrismaPort() + "/manager/proxy/list")
                .sendJson(PrismaResponse.class);

            sessions.clear();
            sessions.addAll(response.data);
        } catch (Exception e) {
//            e.printStackTrace();
        }
    }

    @Override
    public String getPlayerUUID(int port) {
        return sessions.stream().filter(s -> s.port == port).findFirst().orElse(new PrismaSessionInfo()).playerGameUuid;
    }

    @Override
    public String getUserId(int port) {
        return sessions.stream().filter(s -> s.port == port).findFirst().orElse(new PrismaSessionInfo()).entityId;
    }

    @Override
    public String getToken(int port) {
        return sessions.stream().filter(s -> s.port == port).findFirst().orElse(new PrismaSessionInfo()).token;
    }

    @Override
    public String getPlayerUUID(GameProfile profile) {
        return sessions.stream().filter(s -> s.playerGameUuid.equals(profile.getId().toString())).findFirst().orElse(new PrismaSessionInfo()).playerGameUuid;
    }

    @Override
    public String getUserId(GameProfile profile) {
        return sessions.stream().filter(s -> s.playerGameUuid.equals(profile.getId().toString())).findFirst().orElse(new PrismaSessionInfo()).entityId;
    }

    @Override
    public String getToken(GameProfile profile) {
        return sessions.stream().filter(s -> s.playerGameUuid.equals(profile.getId().toString())).findFirst().orElse(new PrismaSessionInfo()).token;
    }

    @Override
    public boolean has(int port) {
        return sessions.stream().anyMatch(s -> s.port == port);
    }

    @Override
    public boolean has(GameProfile profile) {
        return sessions.stream().anyMatch(s -> s.playerGameUuid.equals(profile.getId().toString()));
    }

    public static class PrismaResponse {
        @SerializedName("code")
        public int code;
        @SerializedName("msg")
        public String msg;
        @SerializedName("data")
        private List<PrismaSessionInfo> data;
    }
}
