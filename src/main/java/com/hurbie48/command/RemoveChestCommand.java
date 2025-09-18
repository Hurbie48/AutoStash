package com.hurbie48.command;

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

                                    // Find chest by position in ChestStorage
                                    String chestKeyToRemove = null;
                                    for (var entry : ChestStorage.getChests().entrySet()) {
                                        if (entry.getValue().getPos().equals(pos)) {
                                            chestKeyToRemove = entry.getKey();
                                            break;
                                        }
                                    }

                                    if (chestKeyToRemove == null) {
                                        ChatUtil.sendModMessage(player, "This chest is not stored.");
                                        return 1;
                                    }


                                    ChestStorage.removeChest(chestKeyToRemove);
                                    ChestStorage.save(player.getServer());

                                    ChatUtil.sendModMessage(
                                            player,
                                            "Chest '" + chestKeyToRemove + "' removed at " +
                                                    formatPos(pos) +
                                                    ". Total stored: " + ChestStorage.getChests().size()
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
