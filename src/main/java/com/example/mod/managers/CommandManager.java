package com.example.mod.managers;

import com.example.mod.features.command.AbstractCommand;
import com.example.mod.features.command.impl.*;
import com.example.utils.interfaces.Initializable;
import com.example.utils.interfaces.Manageable;
import com.example.utils.pattern.Singleton;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.example.mod.client.GameAccessor.mc;

public class CommandManager implements Initializable, Manageable<AbstractCommand> {
    private final Set<AbstractCommand> commands = new HashSet<>();

    private final CommandDispatcher<CommandSource> dispatcher = new CommandDispatcher<>();

    @Override
    public boolean init() {
        items().forEach(this::add);

        return true;
    }

    @Override
    public boolean destroy() {
        items().forEach(this::remove);

        return true;
    }

    @Override
    public boolean add(AbstractCommand element) {
        if (element != null && !commands.contains(element)) {
            for (String identifier : element.getIdentifiers()) {
                LiteralArgumentBuilder<CommandSource> builder = LiteralArgumentBuilder.literal(identifier);
                element.build(builder);
                dispatcher.register(builder);
            }
            return commands.add(element);
        }

        return false;
    }

    @Override
    public boolean remove(AbstractCommand element) {
        return commands.remove(element);
    }

    @Override
    public List<AbstractCommand> items() {
        return List.of(
                CommandToggle.getInstance(),
                CommandBind.getInstance(),
                CommandBinds.getInstance(),
                CommandDisconnect.getInstance(),
                CommandIRC.getInstance(), // 添加 CommandIRC 实例
                CommandSetMode.getInstance()
        );
    }

    public void execute(String input) throws CommandSyntaxException {
        dispatcher.execute(input, mc.getNetworkHandler().getCommandSource());
    }

    public Set<AbstractCommand> getCommands() {
        return commands;
    }

    public CommandDispatcher<CommandSource> getDispatcher() {
        return dispatcher;
    }

    public static CommandManager getInstance() {
        return Singleton.getInstance(CommandManager.class);
    }
}
