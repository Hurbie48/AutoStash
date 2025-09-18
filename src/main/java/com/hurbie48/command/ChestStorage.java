package com.hurbie48.command;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.math.BlockPos;

import java.io.*;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class ChestStorage {

    private static final Gson GSON = new Gson();
    private static Map<String, ChestInfo> CHESTS = new HashMap<>();

    public static class ChestInfo {
        private String name;
        private String category;
        private BlockPos pos;

        public ChestInfo(String name, String category, BlockPos pos) {
            this.name = name;
            this.category = category;
            this.pos = pos;
        }

        public String getName() { return name; }
        public String getCategory() { return category; }
        public BlockPos getPos() { return pos; }
    }

    private static File getFile(MinecraftServer server) {
        File worldDir = server.getSavePath(WorldSavePath.ROOT).toFile();
        if (!worldDir.exists()) worldDir.mkdirs();
        return new File(worldDir, "autostash_chests.json");
    }

    public static void load(MinecraftServer server) {
        File file = getFile(server);
        if (!file.exists()) {
            CHESTS = new HashMap<>();
            save(server);
            return;
        }

        try (Reader reader = new FileReader(file)) {
            Type type = new TypeToken<Map<String, ChestInfo>>() {}.getType();
            Map<String, ChestInfo> loaded = GSON.fromJson(reader, type);
            if (loaded != null && !loaded.isEmpty()) {
                CHESTS = loaded;
            } else {
                CHESTS = new HashMap<>();
                save(server);
            }
        } catch (IOException e) {
            e.printStackTrace();
            CHESTS = new HashMap<>();
        }
    }

    public static void save(MinecraftServer server) {
        File file = getFile(server);
        try (Writer writer = new FileWriter(file)) {
            GSON.toJson(CHESTS, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void addChest(String name, ChestInfo chestInfo) {
        CHESTS.put(name, chestInfo);
    }

    public static Map<String, ChestInfo> getChests() {
        return CHESTS;
    }
}
