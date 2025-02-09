package com.example.mod.features.module.rage;

import com.example.mod.enums.ModuleCategory;
import com.example.mod.events.network.PacketEvent;
import com.example.mod.features.module.AbstractModule;
import com.example.utils.pattern.Singleton;
import com.example.value.BasicValue;
import com.example.value.ChoiceValue;
import com.example.value.NumberValue;
import net.engio.mbassy.listener.Handler;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;

import java.util.Arrays;
import java.util.Set;

public class ModuleAntiKnockback extends AbstractModule {
    public ModuleAntiKnockback() {
        super("AntiKnockback", "idk", ModuleCategory.RAGE);
    }

    private final ChoiceValue<Mode> modeValue = new ChoiceValue<>("Mode", Arrays.stream(Mode.values()).toList(), Mode.Cancel);
    private final NumberValue<Float> multiplierValue = new NumberValue<>("multiplier", 1.0f, 0.0f, 5.0f, 0.01f);

    @Handler
    public void onPacket(PacketEvent event) {
        Packet<?> packet = event.getPacket();

        if (packet instanceof EntityVelocityUpdateS2CPacket wrapper && wrapper.getEntityId() == mc.player.getId()) {
            switch (modeValue.getValue()) {
                case Cancel -> {
                    event.cancel();
                }

                case Modify -> {
                    wrapper.velocityX *= multiplierValue.getValue();
                    wrapper.velocityY *= multiplierValue.getValue();
                    wrapper.velocityZ *= multiplierValue.getValue();
                }
            }
        }
    }

    @Override
    public Set<BasicValue<?>> getValues() {
        return Set.of(
                this.modeValue
        );
    }

    enum Mode { Cancel, Modify }

    public static ModuleAntiKnockback getInstance() {
        return Singleton.getInstance(ModuleAntiKnockback.class);
    }
}
