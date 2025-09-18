package com.hurbie48.config;

import net.minecraft.util.math.BlockPos;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PlayerData {

    private boolean autoStashEnabled = true;

    private final Map<String, Set<String>> categories = new HashMap<>();
    private final Map<String, ChestInfo> chests = new HashMap<>();

    // Constructor
    public PlayerData() {
        // Load default categories if none exist
        if (categories.isEmpty()) {
            DefaultCategories.getDefaults().forEach((key, value) -> categories.put(key, new HashSet<>(value)));
        }
    }

    public boolean isAutoStashEnabled() { return autoStashEnabled; }
    public void setAutoStashEnabled(boolean enabled) { this.autoStashEnabled = enabled; }

    public Map<String, Set<String>> getCategories() { return categories; }
    public Map<String, ChestInfo> getChests() { return chests; }

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
}
