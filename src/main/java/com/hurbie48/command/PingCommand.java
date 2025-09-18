package com.hurbie48.command;

import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import com.mojang.brigadier.CommandDispatcher;

import static net.minecraft.server.command.CommandManager.literal;

public class PingCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
                literal("ping")
                        .executes(ctx -> {
                            ctx.getSource().sendFeedback(() -> Text.literal("Pong!"), false);
                            return 1;
                        })
        );
    }
}
