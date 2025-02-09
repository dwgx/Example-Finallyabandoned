package com.example.mod.features.command.impl;

import com.example.mod.features.command.AbstractCommand;
import com.example.mod.features.command.argument.ModuleArgumentType;
import com.example.mod.features.module.AbstractModule;
import com.example.mod.utils.player.ChatUtils;
import com.example.utils.pattern.Singleton;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;

public class CommandToggle extends AbstractCommand {
    public CommandToggle() {
        super("toggle");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(argument("module", ModuleArgumentType.module()).executes(context -> {
            AbstractModule module = context.getArgument("module", AbstractModule.class);

            module.toggle();

            boolean enabled = module.isEnabled();

            MutableText statusText = Text.literal(enabled ? "ON" : "OFF")
                    .styled(style -> style.withColor(enabled ? TextColor.fromRgb(0x00FF00) : TextColor.fromRgb(0xFF0000)));

            MutableText displayText = Text.literal("Module '" + module.getName() + "' is now ")
                    .append(statusText);

            ChatUtils.display(displayText, module.hashCode());

            return SINGLE_SUCCESS;
        }));
    }

    public static CommandToggle getInstance() {
        return Singleton.getInstance(CommandToggle.class);
    }
}
