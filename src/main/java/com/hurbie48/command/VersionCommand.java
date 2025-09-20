package com.hurbie48.command;

import com.hurbie48.config.AutoStashConfig;
import com.hurbie48.util.ChatUtil;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.*;

import static net.minecraft.server.command.CommandManager.literal;

import com.mojang.brigadier.CommandDispatcher;

public class VersionCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("autostash")
                .then(literal("version")
                    .executes(context -> {
                        String version = FabricLoader.getInstance()
                                .getModContainer("autostash")
                                .map(container -> container.getMetadata().getVersion().getFriendlyString())
                                .orElse("unknown");
                        ServerPlayerEntity player = context.getSource().getPlayer();
                        ChatUtil.sendModMessage(player, "Running AutoStash version " + version);
                        boolean enabled = AutoStashConfig.isEnabled();
                        String state = enabled ? "enabled" : "disabled";
                        ChatUtil.sendModMessage(player, "AutoStash is currently " + state + ".");
                        return 1;
                })
                ));
        }
    }
