package com.example.mod.features.command.argument;

import com.example.utils.input.KeyMapper;
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

public class KeyArgumentType implements ArgumentType<String> {
    private static final Collection<String> EXAMPLES = KeyMapper
            .getKeyNames()
            .stream()
            .limit(4)
            .toList();

    private static final DynamicCommandExceptionType UNKNOWN_KEY_EXCEPTION =
            new DynamicCommandExceptionType(key -> Text.literal("Key '" + key + "' is not recognized."));

    public static KeyArgumentType key() {
        return new KeyArgumentType();
    }

    @Override
    public String parse(StringReader reader) throws CommandSyntaxException {
        final String argument = reader.readString();

        Integer keyCode = KeyMapper.getKeyCode(argument);

        if (keyCode == null) {
            throw UNKNOWN_KEY_EXCEPTION.create(argument);
        }

        return argument;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return CommandSource.suggestMatching(KeyMapper.getKeyNames(), builder);
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}
