package com.example.mod.injection.mixin.minecraft.client.gui.hud;

import com.example.mod.utils.interfaces.IChatHudLineVisible;
import net.minecraft.client.gui.hud.ChatHudLine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ChatHudLine.Visible.class)
public class ChatHudLineVisibleMixin implements IChatHudLineVisible {
    @Unique private int id;

    @Override
    public int example$getId() {
        return this.id;
    }

    @Override
    public void example$setId(int id) {
        this.id = id;
    }
}
