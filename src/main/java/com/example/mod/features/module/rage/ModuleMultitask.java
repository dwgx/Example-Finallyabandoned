package com.example.mod.features.module.rage;

import com.example.mod.enums.ModuleCategory;
import com.example.mod.features.module.AbstractModule;
import com.example.utils.pattern.Singleton;
import com.example.value.BasicValue;

public class ModuleMultitask extends AbstractModule {
    public ModuleMultitask() {
        super("Multitask", "dual action fuck u.", ModuleCategory.RAGE);
    }

    private final BasicValue<Boolean> attackValue = new BasicValue<>("Attack", true);

    public boolean canAttack() {
        return attackValue.getValue();
    }

    public static ModuleMultitask getInstance() {
        return Singleton.getInstance(ModuleMultitask.class);
    }
}
