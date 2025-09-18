package com.hurbie48.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;

import static net.minecraft.server.command.CommandManager.literal;
import static net.minecraft.server.command.CommandManager.argument;

public class GreetCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
                literal("autostash")
                        .then(literal("greet")
                                .then(argument("name", StringArgumentType.word())
                                        .executes(ctx -> {
                                            String name = StringArgumentType.getString(ctx, "name");

                                            // Fetch version from Fabric mod metadata
                                            String version = FabricLoader.getInstance()
                                                    .getModContainer("autostash")
                                                    .map(ModContainer::getMetadata)
                                                    .map(metadata -> metadata.getVersion().getFriendlyString())
                                                    .orElse("unknown");

                                            ctx.getSource().sendFeedback(() -> Text.literal("Hello, " + name + "!"), false);
                                            ctx.getSource().sendFeedback(() -> Text.literal("This server is running AutoStash version: " + version), false);

                                            return 1;
                                        })
                                )
                        )
        );
    }
}
