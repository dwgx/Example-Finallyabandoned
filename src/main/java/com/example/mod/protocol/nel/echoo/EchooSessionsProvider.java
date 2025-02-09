package com.example.mod.protocol.nel.echoo;

import com.example.mod.protocol.nel.GameSessionProvider;
import com.mojang.authlib.GameProfile;

public class EchooSessionsProvider implements GameSessionProvider {
    private static EchooSessionsProvider sInstance;

    public static EchooSessionsProvider get() {
        if (sInstance == null) {
            sInstance = new EchooSessionsProvider();
        }
        return sInstance;
    }

    @Override
    public void refresh() {

    }

    @Override
    public String getPlayerUUID(int port) {
        return "";
    }

    @Override
    public String getUserId(int port) {
        return "";
    }

    @Override
    public String getToken(int port) {
        return "";
    }

    @Override
    public String getPlayerUUID(GameProfile profile) {
        return "";
    }

    @Override
    public String getUserId(GameProfile profile) {
        return "";
    }

    @Override
    public String getToken(GameProfile profile) {
        return "";
    }

    @Override
    public boolean has(int port) {
        return false;
    }

    @Override
    public boolean has(GameProfile profile) {
        return false;
    }
}
