package com.hurbie48.command;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hurbie48.config.DefaultCategories;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.WorldSavePath;

import java.io.*;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CategoryStorage {

    private static final Gson GSON = new Gson();
    private static Map<String, Set<String>> CATEGORIES = new HashMap<>();

    private static File getFile(MinecraftServer server) {
        File worldDir = server.getSavePath(WorldSavePath.ROOT).toFile(); // This is the current world folder
        if (!worldDir.exists()) worldDir.mkdirs();
        return new File(worldDir, "autostash_categories.json");
    }

    public static void load(MinecraftServer server) {
        File file = getFile(server);
        if (!file.exists()) {
            CATEGORIES = new HashMap<>(DefaultCategories.getDefaults());
            save(server);
            return;
        }

        try (Reader reader = new FileReader(file)) {
            Type type = new TypeToken<Map<String, Set<String>>>() {}.getType();
            Map<String, Set<String>> loaded = GSON.fromJson(reader, type);
            if (loaded != null && !loaded.isEmpty()) {
                CATEGORIES = loaded;
            } else {
                CATEGORIES = new HashMap<>(DefaultCategories.getDefaults());
                save(server);
            }
        } catch (IOException e) {
            e.printStackTrace();
            CATEGORIES = new HashMap<>(DefaultCategories.getDefaults());
        }
    }

    public static void save(MinecraftServer server) {
        File file = getFile(server);
        try (Writer writer = new FileWriter(file)) {
            GSON.toJson(CATEGORIES, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void addCategory(String name) {
        CATEGORIES.putIfAbsent(name, new HashSet<>());
    }

    public static Set<String> getItems(String category) {
        return CATEGORIES.getOrDefault(category, new HashSet<>());
    }

    public static Map<String, Set<String>> getCategories() {
        return CATEGORIES;
    }

    public static void addItemToCategory(String category, String item) {
        CATEGORIES.computeIfAbsent(category, k -> new HashSet<>()).add(item);
    }
}
