package com.hurbie48.util;

import com.hurbie48.config.AutoStashConfig;
import net.minecraft.server.network.ServerPlayerEntity;

public class AutoStashPermission {

    /** Returns true if commands are allowed to run; sends a warning if disabled. */
    public static boolean checkEnabled(ServerPlayerEntity player) {
        if (!AutoStashConfig.isEnabled()) {
            if (player != null) {
                ChatUtil.sendModMessage(player, "AutoStash is currently disabled. Use /autostash toggle to enable it.");
            }
            return false;
        }
        return true;
    }
}
