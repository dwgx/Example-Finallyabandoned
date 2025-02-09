package com.example.mod.features.command.impl;

import com.example.mod.features.command.AbstractCommand;
import com.example.mod.features.command.argument.KeyArgumentType;
import com.example.mod.features.command.argument.ModuleArgumentType;
import com.example.mod.features.module.AbstractModule;
import com.example.mod.utils.player.ChatUtils;
import com.example.utils.input.KeyMapper;
import com.example.utils.input.ShortcutKey;
import com.example.utils.pattern.Singleton;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;

public class CommandBind extends AbstractCommand {
    public CommandBind() {
        super("bind");
    }

    private boolean waitInput = false;

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(argument("module", ModuleArgumentType.module())
                .executes(context -> {
                    AbstractModule module = context.getArgument("module", AbstractModule.class);
                    ChatUtils.display(Text.literal("Module '" + module.getName() + "' is waiting for key press."), getInstance().hashCode() + module.hashCode());
                    this.setWaitInput(true);
                    return SINGLE_SUCCESS;
                })
                .then(argument("key", KeyArgumentType.key())
                        .executes(context -> {
                            AbstractModule module = context.getArgument("module", AbstractModule.class);
                            String key = context.getArgument("key", String.class);
                            int keyCode = KeyMapper.getKeyCode(key);
                            module.setShortcutKey(new ShortcutKey(keyCode).getPrimaryKey());
                            ChatUtils.display(Text.literal("Module '" + module.getName() + "' is now bound to key '" + key + "'"), getInstance().hashCode() + module.hashCode());
                            return SINGLE_SUCCESS;
                        })
                )
        );
    }

    public boolean isWaitInput() {
        return waitInput;
    }

    public void setWaitInput(boolean waitInput) {
        this.waitInput = waitInput;
    }

    public static CommandBind getInstance() {
        return Singleton.getInstance(CommandBind.class);
    }
}
