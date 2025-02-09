package com.example.mod.protocol.nel.zone;

import com.example.mod.protocol.Http;
import com.example.mod.protocol.nel.GameSessionProvider;
import com.google.gson.annotations.SerializedName;
import com.mojang.authlib.GameProfile;

import java.util.ArrayList;
import java.util.List;

public class ZoneSessionsProvider implements GameSessionProvider {
    private static ZoneSessionsProvider sInstance;
    public final List<ZoneSessionInfo> sessions = new ArrayList<>();

    public static ZoneSessionsProvider get() {
        if (sInstance == null) {
            sInstance = new ZoneSessionsProvider();
        }
        return sInstance;
    }

    @Override
    public void refresh() {
        try {
            ZoneResponse response = Http.get("http://127.0.0.1:54188/")
                .sendJson(ZoneResponse.class);

            sessions.clear();
            sessions.addAll(response.data);
        } catch (Exception e) {
//            e.printStackTrace();
        }
    }

    @Override
    public String getPlayerUUID(int port) {
        return sessions.stream().filter(s -> s.port == port).findFirst().orElse(new ZoneSessionInfo()).playerGameUuid;
    }

    @Override
    public String getUserId(int port) {
        return sessions.stream().filter(s -> s.port == port).findFirst().orElse(new ZoneSessionInfo()).entityId;
    }

    @Override
    public String getToken(int port) {
        return sessions.stream().filter(s -> s.port == port).findFirst().orElse(new ZoneSessionInfo()).token;
    }

    @Override
    public String getPlayerUUID(GameProfile profile) {
        return sessions.stream().filter(s -> s.playerGameUuid.equals(profile.getId().toString())).findFirst().orElse(new ZoneSessionInfo()).playerGameUuid;
    }

    @Override
    public String getUserId(GameProfile profile) {
        return sessions.stream().filter(s -> s.playerGameUuid.equals(profile.getId().toString())).findFirst().orElse(new ZoneSessionInfo()).entityId;
    }

    @Override
    public String getToken(GameProfile profile) {
        return sessions.stream().filter(s -> s.playerGameUuid.equals(profile.getId().toString())).findFirst().orElse(new ZoneSessionInfo()).token;
    }

    @Override
    public boolean has(int port) {
        return sessions.stream().anyMatch(s -> s.port == port);
    }

    @Override
    public boolean has(GameProfile profile) {
        return sessions.stream().anyMatch(s -> s.playerGameUuid.equals(profile.getId().toString()));
    }

    public static class ZoneResponse {
        @SerializedName("code")
        public int code;
        @SerializedName("msg")
        public String msg;
        @SerializedName("count")
        public int count;
        @SerializedName("total")
        public int total;
        @SerializedName("data")
        public List<ZoneSessionInfo> data;
    }
}
