package com.hurbie48.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.server.command.ServerCommandSource;

import static net.minecraft.server.command.CommandManager.literal;
import static net.minecraft.server.command.CommandManager.argument;

public class JumpCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
                literal("jump")
                        // default jump if no intensity is given
                        .executes(ctx -> {
                            int intensity = 1;
                            String command = "execute as @e run data modify entity @s Motion set value [0d," + (intensity * 2) + "d,0d]";
                            ctx.getSource().getServer().getCommandManager().executeWithPrefix(ctx.getSource(), command);
                            return 1;
                        })
                        // jump with intensity argument
                        .then(argument("intensity", IntegerArgumentType.integer(-5, 5))
                                .executes(ctx -> {
                                    int intensity = IntegerArgumentType.getInteger(ctx, "intensity");
                                    double motionY = intensity * 2.0;
                                    String command = "execute as @e run data modify entity @s Motion set value [0d," + motionY + "d,0d]";
                                    ctx.getSource().getServer().getCommandManager().executeWithPrefix(ctx.getSource(), command);
                                    return 1;
                                })
                        )
        );
    }
}
