package com.example.mod.features.module.miscellaneous;

import com.example.mod.enums.ModuleCategory;
import com.example.mod.features.module.AbstractModule;
import com.example.utils.pattern.Singleton;
import com.example.value.BasicValue;
import com.example.value.NumberValue;

import java.util.Set;

public class ModuleTimer extends AbstractModule {
    public ModuleTimer() {
        super("Timer", "timer++++", ModuleCategory.MISCELLANEOUS);
    }

    private final NumberValue<Float> multiplierValue = new NumberValue<>("multiplier", 1.0f, 0.0f, 10.0f, 0.01f);

    public float getMultiplier() {
        return this.multiplierValue.getValue();
    }

    @Override
    public Set<BasicValue<?>> getValues() {
        return Set.of(
                this.multiplierValue
        );
    }

    public static ModuleTimer getInstance() {
        return Singleton.getInstance(ModuleTimer.class);
    }
}
