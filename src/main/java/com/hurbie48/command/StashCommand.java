package com.hurbie48.command;

import com.hurbie48.config.AutoStashConfig;
import com.hurbie48.util.ChatUtil;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.*;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;

public class StashCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("stash")
                .executes(context -> {
                    ServerPlayerEntity player = context.getSource().getPlayer();
                    if (player == null) return 0;
                    if (!AutoStashConfig.isEnabled()) {
                        ChatUtil.sendModMessage(player, "Stashing is currently disabled.");
                        return 0;
                    }
                    stashPlayerInventory(player, null);
                    return 1;
                })
                .then(argument("category", StringArgumentType.word())
                        .executes(context -> {
                            ServerPlayerEntity player = context.getSource().getPlayer();
                            if (player == null) return 0;
                            if (!AutoStashConfig.isEnabled()) {
                                ChatUtil.sendModMessage(player, "Stashing is currently disabled.");
                                return 0;
                            }
                            String category = StringArgumentType.getString(context, "category");
                            stashPlayerInventory(player, category);
                            return 1;
                        })
                )
        );
    }

    private static void stashPlayerInventory(ServerPlayerEntity player, String categoryFilter) {
        List<ItemStack> inventory = player.getInventory().getMainStacks();

        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.get(i);
            if (stack.isEmpty()) continue;

            String itemId = Registries.ITEM.getId(stack.getItem()).toString().replace("minecraft:", "");

            // Find compatible chests in this world
            List<ChestStorage.ChestInfo> compatibleChests = new ArrayList<>();
            for (ChestStorage.ChestInfo chestInfo : ChestStorage.getChests().values()) {
                if (categoryFilter != null && !chestInfo.getCategory().equalsIgnoreCase(categoryFilter))
                    continue;

                Set<String> allowed = CategoryStorage.getItems(chestInfo.getCategory());
                if (allowed != null && allowed.contains(itemId)) {
                    compatibleChests.add(chestInfo);
                }
            }

            if (compatibleChests.isEmpty()) continue;

            // Pick a random chest
            ChestStorage.ChestInfo targetChest = compatibleChests.get(new Random().nextInt(compatibleChests.size()));

            if (!(player.getWorld().getBlockState(targetChest.getPos()).getBlock() instanceof ChestBlock chestBlock))
                continue;

            Inventory chestInventory = ChestBlock.getInventory(
                    chestBlock,
                    player.getWorld().getBlockState(targetChest.getPos()),
                    player.getWorld(),
                    targetChest.getPos(),
                    true // ignore blocked
            );

            if (chestInventory == null) continue;

            int originalCount = stack.getCount();
            int remaining = originalCount;

            // Merge into existing stacks
            for (int slot = 0; slot < chestInventory.size(); slot++) {
                ItemStack slotStack = chestInventory.getStack(slot);
                if (!slotStack.isEmpty() && ItemStack.areItemsEqual(slotStack, stack)) {
                    int space = slotStack.getMaxCount() - slotStack.getCount();
                    if (space > 0) {
                        int toAdd = Math.min(space, remaining);
                        slotStack.increment(toAdd);
                        remaining -= toAdd;
                    }
                    if (remaining <= 0) break;
                }
            }

            // Fill empty slots
            for (int slot = 0; slot < chestInventory.size() && remaining > 0; slot++) {
                ItemStack slotStack = chestInventory.getStack(slot);
                if (slotStack.isEmpty()) {
                    int toAdd = Math.min(stack.getMaxCount(), remaining);
                    ItemStack copy = stack.copy();
                    copy.setCount(toAdd);
                    chestInventory.setStack(slot, copy);
                    remaining -= toAdd;
                }
            }

            // Mark chest dirty to save
            BlockEntity chestEntity = player.getWorld().getBlockEntity(targetChest.getPos());
            if (chestEntity instanceof ChestBlockEntity chestBE) {
                chestBE.markDirty();
            }

            player.getWorld().updateComparators(targetChest.getPos(), player.getWorld().getBlockState(targetChest.getPos()).getBlock());

            int moved = originalCount - remaining;
            if (moved > 0) {
                if (remaining <= 0) player.getInventory().removeStack(i);
                else stack.setCount(remaining);

                ChatUtil.sendModMessage(player, "Moved " + moved + " x " + stack.getName().getString() +
                        " to chest '" + targetChest.getName() + "'");
            }
        }
    }
}
