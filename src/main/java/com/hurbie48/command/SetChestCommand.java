package com.hurbie48.command;

import com.hurbie48.util.ChatUtil;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class SetChestCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
                literal("autostash")
                        .then(literal("setchest")
                                .then(argument("name", StringArgumentType.word())
                                        .then(argument("category", StringArgumentType.word())
                                                .executes(ctx -> {
                                                    ServerPlayerEntity player = ctx.getSource().getPlayer();
                                                    if (player == null) return 0;

                                                    String name = StringArgumentType.getString(ctx, "name");
                                                    String category = StringArgumentType.getString(ctx, "category");

                                                    // Check if category exists
                                                    if (!CategoryStorage.getCategories().containsKey(category)) {
                                                        ChatUtil.sendModMessage(player,
                                                                "Invalid category! Allowed: " + CategoryStorage.getCategories().keySet());
                                                        return 0;
                                                    }

                                                    return addChest(player, name, category);
                                                })
                                        )
                                )
                        )
        );
    }

    private static int addChest(ServerPlayerEntity player, String name, String category) {
        BlockPos pos = getLookedAtChest(player);
        if (pos == null) return 1;

        // Check if chest is already stored
        boolean exists = ChestStorage.getChests().values().stream()
                .anyMatch(c -> c.getPos().equals(pos));
        if (exists) {
            ChatUtil.sendModMessage(player, "This chest is already stored!");
            return 1;
        }

        // Store chest in world JSON
            ChestStorage.ChestInfo chestInfo = new ChestStorage.ChestInfo(name, category, pos);
            ChestStorage.addChest(name, chestInfo);
            ChestStorage.save(player.getServer());


            ChatUtil.sendModMessage(player,
                    "Chest '" + name + "' stored in category '" + category + "' at " +
                            formatPos(pos) + ". Total stored: " + ChestStorage.getChests().size());
        return 1;
        }


    public static BlockPos getLookedAtChest(ServerPlayerEntity player) {
        Vec3d start = player.getCameraPosVec(1.0f);
        Vec3d direction = player.getRotationVec(1.0f);
        Vec3d end = start.add(direction.multiply(5));

        BlockHitResult hitResult = player.getWorld().raycast(
                new net.minecraft.world.RaycastContext(
                        start,
                        end,
                        net.minecraft.world.RaycastContext.ShapeType.OUTLINE,
                        net.minecraft.world.RaycastContext.FluidHandling.ANY,
                        player
                )
        );

        if (hitResult.getType() == BlockHitResult.Type.MISS) {
            ChatUtil.sendModMessage(player, "Please look at a chest within 5 blocks.");
            return null;
        }

        BlockPos pos = hitResult.getBlockPos();
        BlockState block = player.getWorld().getBlockState(pos);
        if (!(block.getBlock() instanceof ChestBlock)) {
            ChatUtil.sendModMessage(player, "That block is not a chest.");
            return null;
        }

        return pos;
    }

    private static String formatPos(BlockPos pos) {
        return "(" + pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + ")";
    }
}
