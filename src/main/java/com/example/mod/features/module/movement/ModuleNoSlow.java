package com.example.mod.features.module.movement;

import com.example.mod.enums.ModuleCategory;
import com.example.mod.events.client.network.MovementMultiplierEvent;
import com.example.mod.features.module.AbstractModule;
import com.example.utils.pattern.Singleton;
import net.engio.mbassy.listener.Handler;

public class ModuleNoSlow extends AbstractModule {
    public ModuleNoSlow() {
        super("NoSlow", "using move fast. like ccb (shit english)", ModuleCategory.MOVEMENT);
    }

    @Handler
    public void onMovementMultiplier(MovementMultiplierEvent event) {
        event.cancel();
    }

    public static ModuleNoSlow getInstance() {
        return Singleton.getInstance(ModuleNoSlow.class);
    }
}
