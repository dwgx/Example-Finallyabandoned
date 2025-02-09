package com.example.mod.injection.mixin.minecraft.client.gui.hud;

import com.example.mod.utils.interfaces.IChatHudLine;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ChatHudLine.class)
public class ChatHudLineMixin implements IChatHudLine {
    @Shadow @Final private int creationTick;
    @Shadow @Final private Text content;
    @Shadow @Final private MessageIndicator indicator;

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
