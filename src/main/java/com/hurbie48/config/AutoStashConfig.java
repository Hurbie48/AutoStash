package com.hurbie48.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class AutoStashConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File CONFIG_FILE = new File("config/AutoStash.json");

    private static ConfigData data = new ConfigData();

    public static class ConfigData {
        public boolean enabled = true; // default: enabled
    }

    public static void load() {
        if (!CONFIG_FILE.exists()) {
            save(); // create default file
            return;
        }
        try (FileReader reader = new FileReader(CONFIG_FILE)) {
            data = GSON.fromJson(reader, ConfigData.class);
            if (data == null) data = new ConfigData();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void save() {
        CONFIG_FILE.getParentFile().mkdirs();
        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            GSON.toJson(data, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean isEnabled() {
        return data.enabled;
    }

    public static void toggle() {
        data.enabled = !data.enabled;
        save();
    }
}
