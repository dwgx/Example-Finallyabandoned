package com.example.mod.features.command.impl;

import com.example.mod.features.command.AbstractCommand;
import com.example.mod.features.module.AbstractModule;
import com.example.mod.utils.player.ChatUtils;
import com.example.value.ChoiceValue;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;

public class CommandSetMode extends AbstractCommand {
    public CommandSetMode() {
        super("setmode");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(argument("module", com.example.mod.features.command.argument.ModuleArgumentType.module())
                .then(argument("param", StringArgumentType.word())
                        .then(argument("mode", StringArgumentType.word())
                                .executes(context -> {
                                    AbstractModule module = context.getArgument("module", AbstractModule.class);
                                    String paramName = context.getArgument("param", String.class);
                                    String modeStr = context.getArgument("mode", String.class);
                                    module.getValues().stream()
                                            .filter(val -> val.getName().equalsIgnoreCase(paramName))
                                            .findFirst()
                                            .ifPresent(val -> {
                                                if (val instanceof ChoiceValue) {
                                                    ChoiceValue choiceVal = (ChoiceValue) val;
                                                    Object matched = choiceVal.getOptions().stream()
                                                            .filter(option -> option.toString().equalsIgnoreCase(modeStr))
                                                            .findFirst()
                                                            .orElse(null);
                                                    if (matched != null) {
                                                        choiceVal.setValue(matched);
                                                        ChatUtils.display(Text.literal("Module '" + module.getName() + "' parameter '" + paramName + "' set to '" + modeStr + "'"), getInstance().hashCode() + module.hashCode());
                                                    } else {
                                                        ChatUtils.display(Text.literal("Invalid mode value: " + modeStr + " for parameter " + paramName), getInstance().hashCode());
                                                    }
                                                } else {
                                                    ChatUtils.display(Text.literal("Parameter " + paramName + " is not a mode (choice) parameter."), getInstance().hashCode());
                                                }
                                            });
                                    return SINGLE_SUCCESS;
                                })
                        )
                )
        );
    }

    public static CommandSetMode getInstance() {
        return com.example.utils.pattern.Singleton.getInstance(CommandSetMode.class);
    }
}
