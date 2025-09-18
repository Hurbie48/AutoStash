package com.hurbie48.command;

import com.hurbie48.util.ChatUtil;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

import java.util.Map;

import static net.minecraft.server.command.CommandManager.literal;

public class ChestStatusCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
                literal("autostash")
                        .then(literal("cheststatus")
                                .executes(ctx -> {
                                    ServerPlayerEntity player = ctx.getSource().getPlayer();
                                    if (player == null) return 0;

                                    MinecraftServer server = player.getServer();
                                    if (server == null) return 0;

                                    // Load current world's chests
                                    ChestStorage.load(player.getServer());
                                    Map<String, ChestStorage.ChestInfo> chests = ChestStorage.getChests();


                                    if (chests.isEmpty()) {
                                        ChatUtil.sendModMessage(player, "No chests are currently stored in this world.");
                                        return 1;
                                    }

                                    ChatUtil.sendModMessage(player, "Chest status in this world:");
                                    int count = 1;
                                    for (ChestStorage.ChestInfo chest : chests.values()) {
                                        BlockPos pos = chest.getPos();
                                        ChatUtil.sendModMessage(player,
                                                count + ". Name: '" + chest.getName() +
                                                        "', Category: '" + chest.getCategory() +
                                                        "' at (" + pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + ")"
                                        );
                                        count++;
                                    }

                                    return 1;
                                })
                        )
        );
    }
}
