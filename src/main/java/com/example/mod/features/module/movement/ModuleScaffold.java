package com.example.mod.features.module.movement;

import com.example.mod.enums.ModuleCategory;
import com.example.mod.events.network.NetworkMovementEvent;
import com.example.mod.features.module.AbstractModule;
import com.example.utils.pattern.Singleton;
import net.engio.mbassy.listener.Handler;

public class ModuleScaffold extends AbstractModule {
    public ModuleScaffold() {
        super("Scaffold", "Auto place block.", ModuleCategory.MOVEMENT);
    }

    @Handler
    public void onNetworkMovement(NetworkMovementEvent event) {

    }

    public static ModuleScaffold getInstance() {
        return Singleton.getInstance(ModuleScaffold.class);
    }
}
