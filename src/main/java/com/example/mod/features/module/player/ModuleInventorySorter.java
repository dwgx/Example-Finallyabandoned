package com.example.mod.features.module.player;

import com.example.event.Event;
import com.example.mod.enums.ModuleCategory;
import com.example.mod.events.entity.LivingEntityTickEvent;
import com.example.mod.features.module.AbstractModule;
import com.example.utils.pattern.Singleton;
import com.example.value.BasicValue;
import com.example.value.ChoiceValue;
import com.example.value.RangeNumberValue;
import net.engio.mbassy.listener.Handler;

import java.util.Arrays;
import java.util.Set;

public class ModuleInventorySorter extends AbstractModule {
    public ModuleInventorySorter() {
        super("InventorySorter", "super hot", ModuleCategory.PLAYER);
    }

    private final RangeNumberValue<Integer> delayValue = new RangeNumberValue<>("Delay", 0, 1, 0, 20, 1);
    private final ChoiceValue<Event.State> eventStateValue = new ChoiceValue<>("Event State", Arrays.stream(Event.State.values()).toList(), Event.State.PRE);
    private final BasicValue<Boolean> onlyInventoryOpen = new BasicValue<>("Only Inventory Open", false);

    @Handler
    public void onLivingEntityTick(LivingEntityTickEvent event) {
        // TODO：Both event state
        if (!event.isLocalPlayer() || !event.getState().equals(this.eventStateValue.getValue())) {
            return;
        }

    }

    @Override
    public Set<BasicValue<?>> getValues() {
        return Set.of(
                this.delayValue,
                this.eventStateValue,
                this.onlyInventoryOpen
        );
    }

    public static ModuleInventorySorter getInstance() {
        return Singleton.getInstance(ModuleInventorySorter.class);
    }
}
