package com.example.mod.features.module.movement;

import com.example.mod.enums.ModuleCategory;
import com.example.mod.features.module.AbstractModule;
import com.example.utils.pattern.Singleton;

public class ModuleKeepSprint extends AbstractModule {
    public ModuleKeepSprint() {
        super("KeepSprint", "nb", ModuleCategory.MOVEMENT);
    }

    public static ModuleKeepSprint getInstance() {
        return Singleton.getInstance(ModuleKeepSprint.class);
    }
}
