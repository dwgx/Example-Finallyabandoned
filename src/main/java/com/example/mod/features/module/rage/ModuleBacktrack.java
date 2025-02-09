package com.example.mod.features.module.rage;

import com.example.mod.enums.ModuleCategory;
import com.example.mod.features.module.AbstractModule;
import com.example.utils.pattern.Singleton;

public class ModuleBacktrack extends AbstractModule {
    public ModuleBacktrack() {
        super("Backtrack", "idk", ModuleCategory.RAGE);
    }

    public static ModuleBacktrack getInstance() {
        return Singleton.getInstance(ModuleBacktrack.class);
    }
}
