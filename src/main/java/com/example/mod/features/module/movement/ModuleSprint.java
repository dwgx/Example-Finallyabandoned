package com.example.mod.features.module.movement;

import com.example.mod.enums.ModuleCategory;
import com.example.mod.events.entity.TravelEvent;
import com.example.mod.features.module.AbstractModule;
import com.example.utils.pattern.Singleton;
import com.example.value.BasicValue;
import com.example.value.ChoiceValue;
import net.engio.mbassy.listener.Handler;

import java.util.Arrays;
import java.util.Set;

public class ModuleSprint extends AbstractModule {
    public ModuleSprint() {
        super("Sprint", "Automatically sprints.", ModuleCategory.MOVEMENT);
    }

    private final ChoiceValue<Mode> modeValue = new ChoiceValue<>("Mode", Arrays.stream(Mode.values()).toList(), Mode.Options);

    @Handler
    public void onTravel(TravelEvent event) {
        if (!event.isLocalPlayer()) {
            return;
        }

        switch (modeValue.getValue()) {
            case Always:
                mc.player.setSprinting(true);
                break;
            case Options:
                mc.options.sprintKey.setPressed(this.shouldSprint());
                break;
        }
    }

    private boolean shouldSprint() {
        return mc.options.forwardKey.isPressed();
    }

    @Override
    public Set<BasicValue<?>> getValues() {
        return Set.of(
                this.modeValue
        );
    }

    enum Mode { Always, Options }

    public static ModuleSprint getInstance() {
        return Singleton.getInstance(ModuleSprint.class);
    }
}
