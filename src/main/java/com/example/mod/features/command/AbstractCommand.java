package com.example.mod.features.command;

import com.example.entity.NamedEntity;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.CommandSource;
import net.minecraft.registry.BuiltinRegistries;
import net.minecraft.server.command.CommandManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractCommand extends NamedEntity {
    protected final Logger logger;
    protected CommandRegistryAccess registryAccess = CommandManager.createRegistryAccess(BuiltinRegistries.createWrapperLookup());

    public int SINGLE_SUCCESS = 1;

    public AbstractCommand(String name) {
        this.logger = LoggerFactory.getLogger(name);
        this.id = name;
    }

    protected <T> RequiredArgumentBuilder<CommandSource, T> argument(final String name, final ArgumentType<T> type) {
        return RequiredArgumentBuilder.argument(name, type);
    }

    protected LiteralArgumentBuilder<CommandSource> literal(final String name) {
        return LiteralArgumentBuilder.literal(name);
    }

    public abstract void build(LiteralArgumentBuilder<CommandSource> builder);
}
