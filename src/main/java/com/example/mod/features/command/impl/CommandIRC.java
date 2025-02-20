package com.example.mod.features.command.impl;

import com.example.mod.features.command.AbstractCommand;
import com.example.utils.pattern.Singleton;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class CommandIRC extends AbstractCommand {
    private static CommandIRC instance;

    public CommandIRC() {
        super("irc");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(argument("action", StringArgumentType.word())
                .suggests((context, builder1) -> {
                    // 使用 SuggestionsBuilder 的 suggest 方法添加建议项
                    builder1.suggest("send");
                    builder1.suggest("broadcast");
                    return builder1.buildFuture(); // 返回 Suggestions 对象
                })
                .then(argument("message", StringArgumentType.greedyString())
                        .executes(context -> {
                            String action = StringArgumentType.getString(context, "action");
                            String message = StringArgumentType.getString(context, "message");
                            handleCommand(action, message, context.getSource());
                            return SINGLE_SUCCESS;
                        })
                )
        );
    }

    private void handleCommand(String action, String message, CommandSource source) {
        // 将 CommandSource 转换为 ServerCommandSource
        ServerCommandSource serverSource = (ServerCommandSource) source;

        if ("send".equalsIgnoreCase(action)) {
            sendIrcMessage(message);
            serverSource.sendFeedback(() -> Text.literal("已发送 IRC 消息: " + message).formatted(Formatting.GREEN), false);
        } else if ("broadcast".equalsIgnoreCase(action)) {
            broadcastIrcMessage(message);
            serverSource.sendFeedback(() -> Text.literal("已广播 IRC 消息: " + message).formatted(Formatting.GREEN), false);
        } else {
            serverSource.sendFeedback(() -> Text.literal("未知的操作: " + action).formatted(Formatting.RED), false);
        }
    }

    private void sendIrcMessage(String message) {
        // 这里你可以实现具体的发送消息到 IRC 频道的逻辑
        System.out.println("发送到 IRC: " + message); // TODO: 实际发送逻辑
    }

    private void broadcastIrcMessage(String message) {
        // 这里你可以实现具体的广播消息到 IRC 频道的逻辑
        System.out.println("广播到 IRC: " + message); // TODO: 实际广播逻辑
    }
    
    public static CommandIRC getInstance() {
        return Singleton.getInstance(CommandIRC.class);
    }
}
