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
    private static Map<String, ChestInfo> chests = new HashMap<>(); // only current world

    public static class ChestInfo {
        private String name;
        private String category;
        private int x, y, z;

        public ChestInfo(String name, String category, BlockPos pos) {
            this.name = name;
            this.category = category;
            this.x = pos.getX();
            this.y = pos.getY();
            this.z = pos.getZ();
        }

        public BlockPos getPos() { return new BlockPos(x, y, z); }
        public String getName() { return name; }
        public String getCategory() { return category; }
    }

    private static File getFile(MinecraftServer server) {
        File worldDir = server.getSavePath(WorldSavePath.ROOT).toFile();
        if (!worldDir.exists()) worldDir.mkdirs();
        return new File(worldDir, "autostash_chests.json");
    }

    // Load only the current world's chests
    public static void load(MinecraftServer server) {
        chests.clear();
        File file = getFile(server);
        if (!file.exists()) return;

        try (Reader reader = new FileReader(file)) {
            Type type = new TypeToken<Map<String, ChestInfo>>() {}.getType();
            Map<String, ChestInfo> loaded = GSON.fromJson(reader, type);
            if (loaded != null) chests.putAll(loaded);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void save(MinecraftServer server) {
        try (Writer writer = new FileWriter(getFile(server))) {
            GSON.toJson(chests, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Map<String, ChestInfo> getChests() {
        return chests;
    }

    public static void addChest(String name, ChestInfo chestInfo) {
        chests.put(name, chestInfo);
    }

    public static void removeChest(String name) {
        chests.remove(name);
    }
}
