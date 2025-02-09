package com.example.mod.features.command.argument;

import com.example.mod.features.module.AbstractModule;
import com.example.mod.managers.ModuleManager;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class ModuleArgumentType implements ArgumentType<AbstractModule> {
    private static final Collection<String> EXAMPLES = ModuleManager.getInstance()
            .getModules()
            .stream()
            .limit(4)
            .map(AbstractModule::getName)
            .toList();

    private static final DynamicCommandExceptionType UNKNOWN_MODULE_EXCEPTION =
            new DynamicCommandExceptionType(name -> Text.literal("Module '" + name + "' not found."));

    public static ModuleArgumentType module() {
        return new ModuleArgumentType();
    }

    @Override
    public AbstractModule parse(StringReader reader) throws CommandSyntaxException {
        String argument = reader.readString();
        AbstractModule module = ModuleManager.getInstance().getModuleByName(argument);

        if (module == null) {
            throw  UNKNOWN_MODULE_EXCEPTION.create(argument);
        }

        return module;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return CommandSource.suggestMatching(ModuleManager.getInstance().getModules().stream().map(AbstractModule::getName), builder);
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}
