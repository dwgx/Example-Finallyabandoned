package com.example.mod.features.module.legit;

import com.example.mod.enums.ModuleCategory;
import com.example.mod.features.module.AbstractModule;
import com.example.utils.pattern.Singleton;

public class ModuleLegitbot extends AbstractModule {
    public ModuleLegitbot() {
        super("Legitbot", "ez", ModuleCategory.LEGIT);
    }

    public static ModuleLegitbot getInstance() {
        return Singleton.getInstance(ModuleLegitbot.class);
    }
}
