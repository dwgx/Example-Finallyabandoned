package com.example.mod.features.module.visual;

import com.example.event.Event;
import com.example.mod.enums.ModuleCategory;
import com.example.mod.events.client.world.WorldTickEvent;
import com.example.mod.events.network.PacketEvent;
import com.example.mod.features.module.AbstractModule;
import com.example.utils.pattern.Singleton;
import com.example.value.BasicValue;
import net.engio.mbassy.listener.Handler;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;

import java.util.Set;

public class ModuleWorldTime extends AbstractModule {
    public ModuleWorldTime() {
        super("WorldTime", "Change u world time.", ModuleCategory.VISUAL);
    }

    private final BasicValue<Integer> timeValue = new BasicValue<>("Time", 0);

    private long originalTime;

    @Override
    public void onEnable() {
        if (mc.world != null) {
            this.originalTime = mc.world.getLevelProperties().getTimeOfDay();
        } else {
            this.originalTime = 0;
        }
    }

    @Override
    public void onDisable() {
        if (mc.world != null) {
            mc.world.getLevelProperties().setTimeOfDay(this.originalTime);
        }
    }

    @Handler
    private void onPacket(PacketEvent event) {
        Packet<?> packet = event.getPacket();

        if (packet instanceof WorldTimeUpdateS2CPacket wrapper) {
            this.originalTime = wrapper.timeOfDay();
            event.cancel();
        }
    }

    @Handler
    public void onWorldTick(WorldTickEvent event) {
        if (event.getState().equals(Event.State.POST)) {
            mc.world.getLevelProperties().setTimeOfDay(timeValue.getValue());
        }
    }

    @Override
    public Set<BasicValue<?>> getValues() {
        return Set.of(
                this.timeValue
        );
    }

    public static ModuleWorldTime getInstance() {
        return Singleton.getInstance(ModuleWorldTime.class);
    }
}
