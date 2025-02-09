package com.example.mod.features.command.impl;

import com.example.mod.features.command.AbstractCommand;
import com.example.utils.pattern.Singleton;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.network.packet.s2c.common.DisconnectS2CPacket;
import net.minecraft.text.Text;

import static com.example.mod.client.GameAccessor.mc;

public class CommandDisconnect extends AbstractCommand {
    public CommandDisconnect() {
        super("disconnect");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(argument("reason", StringArgumentType.greedyString())
                .executes(context -> {
                            mc.player.networkHandler.onDisconnect(
                                    new DisconnectS2CPacket(
                                            Text.literal(StringArgumentType.getString(context, "reason"))
                                    )
                            );

                            return SINGLE_SUCCESS;
                        }
                ));
    }

    public static CommandDisconnect getInstance() {
        return Singleton.getInstance(CommandDisconnect.class);
    }
}
