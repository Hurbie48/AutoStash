package com.hurbie48.config;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.WorldSavePath;

import java.io.*;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerDataStorage {

    private static final Gson GSON = new Gson();
    private static Map<UUID, PlayerData> PLAYER_DATA = new HashMap<>();

    private static File getFile(MinecraftServer server) {
        File worldDir = new File(server.getRunDirectory().toFile(), server.getSaveProperties().getLevelName());
        if (!worldDir.exists()) worldDir.mkdirs();
        return new File(worldDir, "autostash_playerdata.json");
    }

    public static void load(MinecraftServer server) {
        File file = getFile(server);
        if (!file.exists()) return;

        try (Reader reader = new FileReader(file)) {
            Type type = new TypeToken<Map<String, PlayerData>>(){}.getType();
            Map<String, PlayerData> raw = GSON.fromJson(reader, type);
            PLAYER_DATA.clear();
            if (raw != null) {
                for (Map.Entry<String, PlayerData> e : raw.entrySet()) {
                    PLAYER_DATA.put(UUID.fromString(e.getKey()), e.getValue());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void save(MinecraftServer server) {

        File worldDir = server.getSavePath(WorldSavePath.ROOT).toFile();
        File file = new File(worldDir, "autostash_playerdata.json");
        try (Writer writer = new FileWriter(file)) {
            Map<String, PlayerData> stringKeyed = new HashMap<>();
            for (Map.Entry<UUID, PlayerData> e : PLAYER_DATA.entrySet()) {
                stringKeyed.put(e.getKey().toString(), e.getValue());
            }
            GSON.toJson(stringKeyed, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static PlayerData get(UUID uuid) {
        return PLAYER_DATA.computeIfAbsent(uuid, k -> new PlayerData());
    }
}
