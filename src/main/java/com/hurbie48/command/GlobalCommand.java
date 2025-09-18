package com.hurbie48.command;

import com.hurbie48.config.AutoStashConfig;
import com.hurbie48.util.ChatUtil;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import static net.minecraft.server.command.CommandManager.literal;

public class GlobalCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
                literal("autostash")
                        .then(literal("toggle")
                                .executes(ctx -> {
                                    ServerPlayerEntity player = ctx.getSource().getPlayer();
                                    if (player == null) return 0;

                                    AutoStashConfig.toggle();
                                    boolean enabled = AutoStashConfig.isEnabled();
                                    ChatUtil.sendModToggleMessage(player, AutoStashConfig.isEnabled());
                                    return 1;
                                })
                        )
        );
    }
}
