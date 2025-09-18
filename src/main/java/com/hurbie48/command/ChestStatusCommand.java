package com.hurbie48.command;

import com.hurbie48.util.ChatUtil;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import com.mojang.brigadier.CommandDispatcher;

import static net.minecraft.server.command.CommandManager.literal;

import java.util.Map;

public class ChestStatusCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
                literal("autostash")
                        .then(literal("cheststatus")
                                .executes(ctx -> {
                                    ServerPlayerEntity player = ctx.getSource().getPlayer();
                                    if (player == null) return 0;

                                    // Use world storage instead of player-local
                                    Map<String, ChestStorage.ChestInfo> chests = ChestStorage.getChests();

                                    if (chests.isEmpty()) {
                                        ChatUtil.sendModMessage(player, "No chests are currently stored in this world.");
                                        return 1;
                                    }

                                    ChatUtil.sendModMessage(player, "Chest status:");
                                    int count = 1;
                                    for (ChestStorage.ChestInfo chest : chests.values()) {
                                        ChatUtil.sendModMessage(player,
                                                count + ". Name: '" + chest.getName() + "', Category: '" +
                                                        chest.getCategory() + "' at (" +
                                                        chest.getPos().getX() + ", " +
                                                        chest.getPos().getY() + ", " +
                                                        chest.getPos().getZ() + ")"
                                        );
                                        count++;
                                    }

                                    return 1;
                                })
                        )
        );
    }
}
