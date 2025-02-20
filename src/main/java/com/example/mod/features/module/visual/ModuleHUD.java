package com.example.mod.features.module.visual;

import com.example.mod.enums.ModuleCategory;
import com.example.mod.events.client.render.LayerRenderEvent;
import com.example.mod.features.module.AbstractModule;

import com.example.utils.pattern.Singleton;
import net.engio.mbassy.listener.Handler;

public class ModuleHUD extends AbstractModule {
    public ModuleHUD() {
        super("HUD", "HUD Module.", ModuleCategory.VISUAL);
    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }

    @Handler
    public void onLayerRender(LayerRenderEvent event) {

    }

    public static ModuleHUD getInstance() {
        return Singleton.getInstance(ModuleHUD.class);
    }
}