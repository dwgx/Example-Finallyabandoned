package com.example.mod.features.command.impl;

import com.example.mod.features.command.AbstractCommand;
import com.example.mod.features.module.AbstractModule;
import com.example.mod.managers.ModuleManager;
import com.example.mod.utils.player.ChatUtils;
import com.example.utils.input.KeyMapper;
import com.example.utils.input.ShortcutKey;
import com.example.utils.pattern.Singleton;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;

public class CommandBinds extends AbstractCommand {
    public CommandBinds() {
        super("binds");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> {
            StringBuilder bindsList = new StringBuilder();

            ModuleManager moduleManager = ModuleManager.getInstance();
            for (AbstractModule module : moduleManager.getModules()) {
                ShortcutKey shortcutKey = module.getShortcutKey();
                if (shortcutKey.getPrimaryKey() != -1) {
                    String keyName = KeyMapper.getKeyName(shortcutKey.getPrimaryKey());
                    bindsList.append("\n").append(keyName.toUpperCase()).append(" - ").append(module.getName());
                }
            }

            if (!bindsList.isEmpty()) {
                ChatUtils.display(Text.literal("Bound Modules:").append(bindsList.toString()));
            } else {
                ChatUtils.display(Text.literal("No modules are currently bound to any key."));
            }

            return SINGLE_SUCCESS;
        });
    }

    public static CommandBinds getInstance() {
        return Singleton.getInstance(CommandBinds.class);
    }
}
