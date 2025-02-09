package com.example.mod.features.module.movement;

import com.example.mod.enums.ModuleCategory;
import com.example.mod.events.client.input.MovementInputEvent;
import com.example.mod.features.module.AbstractModule;
import com.example.utils.pattern.Singleton;
import com.example.value.BasicValue;
import net.engio.mbassy.listener.Handler;

import java.util.Set;

public class ModuleEagle extends AbstractModule {
    public ModuleEagle() {
        super("Eagle", "laoying", ModuleCategory.MOVEMENT);
    }

    private final BasicValue<Boolean> jumpValue = new BasicValue<>("Jump", false);

    @Handler
    public void onMovementInput(MovementInputEvent event) {
        if (mc.player.isOnGround() && (!event.getInput().isJump() || jumpValue.getValue()) && mc.world.getBlockState(mc.player.getBlockPos().down()).isAir()) {
            event.getInput().setSneak(true);
        }
    }

    @Override
    public Set<BasicValue<?>> getValues() {
        return Set.of(
                this.jumpValue
        );
    }

    public static ModuleEagle getInstance() {
        return Singleton.getInstance(ModuleEagle.class);
    }
}
