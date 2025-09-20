package com.hurbie48;

import com.hurbie48.command.*;
import com.hurbie48.config.*;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AutoStash implements ModInitializer {
    public static final String MOD_ID = "autostash";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("AutoStash Mod initialized!");

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            SetChestCommand.register(dispatcher);
            RemoveChestCommand.register(dispatcher);
            ChestStatusCommand.register(dispatcher);
            CategoryCommand.register(dispatcher);
            GlobalCommand.register(dispatcher);
            StashCommand.register(dispatcher);
            VersionCommand.register(dispatcher);
        });

        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            PlayerDataStorage.load(server);
            CategoryStorage.load(server);
            ChestStorage.load(server);

            LOGGER.info("Loaded AutoStash player data for world: {}", server.getSaveProperties().getLevelName());
            LOGGER.info("Loaded AutoStash categories and chests for world: {}", server.getSaveProperties().getLevelName());
        });

        ServerLifecycleEvents.SERVER_STOPPED.register(server -> {
            PlayerDataStorage.save(server);
            CategoryStorage.save(server);
            ChestStorage.save(server);

            LOGGER.info("Saved AutoStash player data for world: {}", server.getSaveProperties().getLevelName());
            LOGGER.info("Saved AutoStash categories and chests for world: {}", server.getSaveProperties().getLevelName());
        });
    }

}
