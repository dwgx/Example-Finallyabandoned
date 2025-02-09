package com.example.mod.features.module.rage;

import com.example.mod.enums.ModuleCategory;
import com.example.mod.events.network.PacketEvent;
import com.example.mod.features.module.AbstractModule;
import com.example.utils.pattern.Singleton;
import net.engio.mbassy.listener.Handler;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BundleDelimiterS2CPacket;
import net.minecraft.network.packet.s2c.play.CloseScreenS2CPacket;

/**
 * 该模块会拦截多个 S2C 数据包，防止服务器执行特定操作。
 * 模块开启后，服务器发送的相关数据包会直接被屏蔽，不作出任何回应。
 */
public class ModuleDISCSS2Cpacket extends AbstractModule {

    public ModuleDISCSS2Cpacket() {
        super("DISCSS2Cpacket", "Intercepts specific S2C packets without response.", ModuleCategory.RAGE);
    }

    @Handler
    public void onPacket(PacketEvent event) {
        Packet<?> packet = event.getPacket();

        // 如果数据包匹配拦截列表，取消它
        if (shouldCancelPacket(packet)) {
            event.cancel();
        }
    }

    /**
     * 判断是否要拦截该数据包
     */
    private boolean shouldCancelPacket(Packet<?> packet) {
        return packet instanceof CloseScreenS2CPacket || packet instanceof BundleDelimiterS2CPacket;
    }

    public static ModuleDISCSS2Cpacket getInstance() {
        return Singleton.getInstance(ModuleDISCSS2Cpacket.class);
    }
}
