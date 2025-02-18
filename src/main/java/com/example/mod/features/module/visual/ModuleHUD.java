package com.example.mod.features.module.visual;

import com.example.mod.enums.ModuleCategory;
import com.example.mod.features.module.AbstractModule;
import com.example.mod.events.client.render.Render2DEvent;

import net.engio.mbassy.listener.Handler;

public class ModuleHUD extends AbstractModule {
    public ModuleHUD() {
        super("HUD", "HUD Module.", ModuleCategory.VISUAL);
    }

    @Override
    public void onEnable() {
        // TODO Auto-generated method stub
        super.onEnable();
    }

    @Override
    public void onDisable() {
        // TODO Auto-generated method stub
        super.onDisable();
    }

    @Handler
    public void onRender(Render2DEvent event) {
        // 在这里实现HUD渲染逻辑
    }

}
