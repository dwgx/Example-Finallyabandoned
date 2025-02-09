package com.example.mod.protocol.heypixel.check.s2c;

import com.example.mod.protocol.heypixel.check.HeypixelPacket;
import net.minecraft.network.PacketByteBuf;

import java.lang.reflect.Method;

@Deprecated
public class NeteaseCheckS2CPacket extends HeypixelPacket {
    public NeteaseCheckS2CPacket(PacketByteBuf friendlyByteBuf) {
        try {
            Method declaredMethod = Class.forName("com.netease.mc.mod.fullscreenpopup.ToggleFullscreenTransformer").getDeclaredMethod("showGameStorePopup");
            declaredMethod.setAccessible(true);
            declaredMethod.invoke(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
