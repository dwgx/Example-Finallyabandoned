package com.example.mod.features.module.movement;

import com.example.mod.enums.ModuleCategory;
import com.example.mod.events.network.LocalTickMovementEvent;
import com.example.mod.features.module.AbstractModule;
import com.example.mod.injection.mixin.minecraft.client.gui.screen.ingame.CreativeInventoryScreenAccessor;
import com.example.mod.utils.input.InputUtils;
import com.example.utils.pattern.Singleton;
import com.example.value.BasicValue;
import net.engio.mbassy.listener.Handler;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.item.ItemGroups;

import java.util.Set;

public class ModuleGuiMove extends AbstractModule {
    public ModuleGuiMove() {
        super("GuiMove", "Allows you to perform moves in the GUI.", ModuleCategory.MOVEMENT);
    }

    private final BasicValue<Boolean> chatScreenValue = new BasicValue<>("Chat Screen", false);
    private final BasicValue<Boolean> sneakValue = new BasicValue<>("Sneak", false);

    @Handler
    public void onLocalTickMovement(LocalTickMovementEvent event) {
        if (!should()) {
            return;
        }

        mc.options.forwardKey.setPressed(InputUtils.isKeyPressed(mc.options.forwardKey));
        mc.options.backKey.setPressed(InputUtils.isKeyPressed(mc.options.backKey));
        mc.options.leftKey.setPressed(InputUtils.isKeyPressed(mc.options.leftKey));
        mc.options.rightKey.setPressed(InputUtils.isKeyPressed(mc.options.rightKey));
        mc.options.jumpKey.setPressed(InputUtils.isKeyPressed(mc.options.jumpKey));
        mc.options.sneakKey.setPressed(InputUtils.isKeyPressed(mc.options.sneakKey) && sneakValue.getValue());
    }

    public boolean should() {
        return mc.currentScreen != null && (!(mc.currentScreen instanceof ChatScreen) || chatScreenValue.getValue()) && (!(mc.currentScreen instanceof CreativeInventoryScreen) || CreativeInventoryScreenAccessor.getSelectedTab() != ItemGroups.getSearchGroup());
    }

    @Override
    public Set<BasicValue<?>> getValues() {
        return Set.of(
                this.chatScreenValue,
                this.sneakValue
        );
    }

    public static ModuleGuiMove getInstance() {
        return Singleton.getInstance(ModuleGuiMove.class);
    }
}
