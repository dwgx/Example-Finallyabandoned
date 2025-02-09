package com.example.mod.features.module.visual;

import com.example.mod.enums.ModuleCategory;
import com.example.mod.features.module.AbstractModule;
import com.example.utils.pattern.Singleton;
import com.example.value.BasicValue;

import java.awt.*;
import java.util.Set;

public class ModuleBlockOutline extends AbstractModule {
    public ModuleBlockOutline() {
        super("BlockOutline", "Mod outline color.", ModuleCategory.VISUAL);
    }

    private final BasicValue<Color> colorValue = new BasicValue<>("Color", new Color(255, 255, 255, 255));

    public Color getColor() {
        return colorValue.getValue();
    }

    @Override
    public Set<BasicValue<?>> getValues() {
        return Set.of(
                this.colorValue
        );
    }

    public static ModuleBlockOutline getInstance() {
        return Singleton.getInstance(ModuleBlockOutline.class);
    }
}
