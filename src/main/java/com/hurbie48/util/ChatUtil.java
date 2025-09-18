package com.hurbie48.util;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class ChatUtil {

    public static void sendModMessage(ServerPlayerEntity player, String message) {
        if (player == null) return;

        MutableText prefix = Text.literal("[AutoStash] ").setStyle(Style.EMPTY.withColor(Formatting.BLUE));
        MutableText msg = Text.literal(message).setStyle(Style.EMPTY.withColor(Formatting.WHITE));
        player.sendMessage(prefix.append(msg), false);
    }

    public static void sendModToggleMessage(ServerPlayerEntity player, boolean enabled) {
        if (player == null) return;

        MutableText prefix = Text.literal("[AutoStash] ").setStyle(Style.EMPTY.withColor(Formatting.BLUE));
        String statusText = enabled ? "on" : "off";
        Formatting color = enabled ? Formatting.GREEN : Formatting.RED;

        MutableText msg = Text.literal("AutoStash is now turned " + statusText + ".")
                .setStyle(Style.EMPTY.withColor(color));

        player.sendMessage(prefix.append(msg), false);
    }
}
