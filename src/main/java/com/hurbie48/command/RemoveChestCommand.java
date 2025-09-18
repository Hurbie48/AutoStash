package com.hurbie48.command;

import com.hurbie48.config.PlayerData;
import com.hurbie48.config.PlayerDataStorage;
import com.hurbie48.util.AutoStashPermission;
import com.hurbie48.util.ChatUtil;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

import static net.minecraft.server.command.CommandManager.literal;

public class RemoveChestCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
                literal("autostash")
                        .then(literal("removechest")
                                .executes(ctx -> {
                                    ServerPlayerEntity player = ctx.getSource().getPlayer();
                                    if (player == null) return 0;
                                    if (!AutoStashPermission.checkEnabled(player)) return 0;

                                    BlockPos pos = SetChestCommand.getLookedAtChest(player);
                                    if (pos == null) return 1;

                                    PlayerData data = PlayerDataStorage.get(player.getUuid());

                                    // Find chest by position
                                    PlayerData.ChestInfo toRemove = null;
                                    for (PlayerData.ChestInfo chest : data.getChests().values()) {
                                        if (chest.getPos().equals(pos)) {
                                            toRemove = chest;
                                            break;
                                        }
                                    }

                                    if (toRemove == null) {
                                        ChatUtil.sendModMessage(player, "This chest is not stored.");
                                        return 1;
                                    }

                                    // Remove chest
                                    data.getChests().values().remove(toRemove);
                                    PlayerDataStorage.save(player.getServer());

                                    String posFormatted = formatPos(pos);
                                    ChatUtil.sendModMessage(
                                            player,
                                            "Chest '" + toRemove.getName() + "' removed at " + posFormatted +
                                                    ". Total stored: " + data.getChests().size()
                                    );
                                    return 1;
                                })
                        )
        );
    }

    private static String formatPos(BlockPos pos) {
        return "(" + pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + ")";
    }
}
