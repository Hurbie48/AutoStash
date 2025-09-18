package com.hurbie48;

import com.hurbie48.command.*;
import com.hurbie48.config.*;
import com.hurbie48.util.ChatUtil;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class AutoStash implements ModInitializer {
    public static final String MOD_ID = "autostash";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private final Random random = new Random();

    @Override
    public void onInitialize() {
        LOGGER.info("AutoStash Mod initialized!");

        // Register commands
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            PingCommand.register(dispatcher);
            JumpCommand.register(dispatcher);
            SetChestCommand.register(dispatcher);
            RemoveChestCommand.register(dispatcher);
            ChestStatusCommand.register(dispatcher);
            CategoryCommand.register(dispatcher);
            GlobalCommand.register(dispatcher);
            StashCommand.register(dispatcher);
        });

        // Load player/world data and persistent storage when server starts
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            PlayerDataStorage.load(server);
            CategoryStorage.load(server);
            ChestStorage.load(server);

            LOGGER.info("Loaded AutoStash player data for world: " + server.getSaveProperties().getLevelName());
            LOGGER.info("Loaded AutoStash categories and chests for world: " + server.getSaveProperties().getLevelName());
        });

        // Save player/world data and persistent storage when server stops
        ServerLifecycleEvents.SERVER_STOPPED.register(server -> {
            PlayerDataStorage.save(server);
            CategoryStorage.save(server);
            ChestStorage.save(server);

            LOGGER.info("Saved AutoStash player data for world: " + server.getSaveProperties().getLevelName());
            LOGGER.info("Saved AutoStash categories and chests for world: " + server.getSaveProperties().getLevelName());
        });

        // Player join welcome message
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayerEntity player = handler.getPlayer();

            String version = FabricLoader.getInstance()
                    .getModContainer("autostash")
                    .map(container -> container.getMetadata().getVersion().getFriendlyString())
                    .orElse("unknown");

            ChatUtil.sendModMessage(player, "Welcome, " + player.getGameProfile().getName() + "!");
            ChatUtil.sendModMessage(player, "Running AutoStash version " + version);
            boolean enabled = AutoStashConfig.isEnabled();
            String state = enabled ? "enabled" : "disabled";
            ChatUtil.sendModMessage(player, "AutoStash is currently " + state + ".");
        });
    }

    private List<ItemStack> copyInventory(ServerPlayerEntity player) {
        List<ItemStack> copy = new ArrayList<>();
        for (ItemStack stack : player.getInventory().getMainStacks()) copy.add(stack.copy());
        return copy;
    }
}
