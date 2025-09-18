package com.hurbie48.command;

import com.hurbie48.util.AutoStashPermission;
import com.hurbie48.util.ChatUtil;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Map;
import java.util.Set;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class CategoryCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
                literal("autostash")
                        // CREATE CATEGORY
                        .then(literal("new")
                                .then(literal("category")
                                        .then(argument("name", StringArgumentType.word())
                                                .executes(ctx -> {
                                                    ServerPlayerEntity player = ctx.getSource().getPlayer();
                                                    if (player == null) return 0;
                                                    if (!AutoStashPermission.checkEnabled(player)) return 0;

                                                    String name = StringArgumentType.getString(ctx, "name");

                                                    if (CategoryStorage.getCategories().containsKey(name)) {
                                                        ChatUtil.sendModMessage(player, "Category '" + name + "' already exists.");
                                                        return 1;
                                                    }

                                                    CategoryStorage.addCategory(name);
                                                    CategoryStorage.save(player.getServer());

                                                    ChatUtil.sendModMessage(player, "Category '" + name + "' created!");
                                                    return 1;
                                                })
                                        )
                                )
                        )

                        // LIST CATEGORIES OR ITEMS
                        .then(literal("list")
                                .then(literal("category")
                                        .executes(ctx -> {
                                            ServerPlayerEntity player = ctx.getSource().getPlayer();
                                            if (player == null) return 0;

                                            Map<String, Set<String>> categories = CategoryStorage.getCategories();

                                            if (categories.isEmpty()) {
                                                ChatUtil.sendModMessage(player, "No categories exist.");
                                            } else {
                                                ChatUtil.sendModMessage(player, "Categories:");
                                                int i = 1;
                                                for (String cat : categories.keySet()) {
                                                    ChatUtil.sendModMessage(player, i + ". " + cat);
                                                    i++;
                                                }
                                            }
                                            return 1;
                                        })
                                )
                                .then(literal("item")
                                        .then(argument("category", StringArgumentType.word())
                                                .executes(ctx -> {
                                                    ServerPlayerEntity player = ctx.getSource().getPlayer();
                                                    if (player == null) return 0;

                                                    String category = StringArgumentType.getString(ctx, "category");

                                                    if (!CategoryStorage.getCategories().containsKey(category)) {
                                                        ChatUtil.sendModMessage(player, "Category '" + category + "' does not exist.");
                                                        return 1;
                                                    }

                                                    Set<String> items = CategoryStorage.getItems(category);
                                                    if (items.isEmpty()) {
                                                        ChatUtil.sendModMessage(player, "No items in category '" + category + "'.");
                                                    } else {
                                                        ChatUtil.sendModMessage(player, "Items in category '" + category + "':");
                                                        int i = 1;
                                                        for (String item : items) {
                                                            ChatUtil.sendModMessage(player, i + ". " + item);
                                                            i++;
                                                        }
                                                    }
                                                    return 1;
                                                })
                                        )
                                )
                        )

                        // ADD ITEM
                        .then(literal("add")
                                .then(literal("item")
                                        .then(argument("category", StringArgumentType.word())
                                                .then(argument("item", StringArgumentType.word())
                                                        .executes(ctx -> {
                                                            ServerPlayerEntity player = ctx.getSource().getPlayer();
                                                            if (player == null) return 0;
                                                            if (!AutoStashPermission.checkEnabled(player)) return 0;

                                                            String category = StringArgumentType.getString(ctx, "category");
                                                            String item = StringArgumentType.getString(ctx, "item");

                                                            if (!CategoryStorage.getCategories().containsKey(category)) {
                                                                ChatUtil.sendModMessage(player, "Category '" + category + "' does not exist.");
                                                                return 1;
                                                            }

                                                            Set<String> items = CategoryStorage.getItems(category);
                                                            if (items.contains(item)) {
                                                                ChatUtil.sendModMessage(player, "Item '" + item + "' is already in category '" + category + "'.");
                                                                return 1;
                                                            }

                                                            CategoryStorage.addItemToCategory(category, item);
                                                            CategoryStorage.save(player.getServer());

                                                            ChatUtil.sendModMessage(player, "Item '" + item + "' added to category '" + category + "'.");
                                                            return 1;
                                                        })
                                                )
                                        )
                                )
                        )

                        // REMOVE ITEM OR CATEGORY
                        .then(literal("remove")
                                // Remove item
                                .then(literal("item")
                                        .then(argument("category", StringArgumentType.word())
                                                .then(argument("item", StringArgumentType.word())
                                                        .executes(ctx -> {
                                                            ServerPlayerEntity player = ctx.getSource().getPlayer();
                                                            if (player == null) return 0;
                                                            if (!AutoStashPermission.checkEnabled(player)) return 0;

                                                            String category = StringArgumentType.getString(ctx, "category");
                                                            String item = StringArgumentType.getString(ctx, "item");

                                                            if (!CategoryStorage.getCategories().containsKey(category)) {
                                                                ChatUtil.sendModMessage(player, "Category '" + category + "' does not exist.");
                                                                return 1;
                                                            }

                                                            Set<String> items = CategoryStorage.getItems(category);
                                                            if (!items.contains(item)) {
                                                                ChatUtil.sendModMessage(player, "Item '" + item + "' is not in category '" + category + "'.");
                                                                return 1;
                                                            }

                                                            items.remove(item);
                                                            CategoryStorage.save(player.getServer());

                                                            ChatUtil.sendModMessage(player, "Item '" + item + "' removed from category '" + category + "'.");
                                                            return 1;
                                                        })
                                                )
                                        )
                                )
                                // Remove category
                                .then(literal("category")
                                        .then(argument("name", StringArgumentType.word())
                                                .executes(ctx -> {
                                                    ServerPlayerEntity player = ctx.getSource().getPlayer();
                                                    if (player == null) return 0;
                                                    if (!AutoStashPermission.checkEnabled(player)) return 0;

                                                    String name = StringArgumentType.getString(ctx, "name");

                                                    if (!CategoryStorage.getCategories().containsKey(name)) {
                                                        ChatUtil.sendModMessage(player, "Category '" + name + "' does not exist.");
                                                        return 1;
                                                    }

                                                    CategoryStorage.getCategories().remove(name);
                                                    CategoryStorage.save(player.getServer());

                                                    ChatUtil.sendModMessage(player, "Category '" + name + "' removed.");
                                                    return 1;
                                                })
                                        )
                                )
                        )
        );
    }
}
