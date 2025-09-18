package com.hurbie48.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DefaultCategories {
    public static Map<String, Set<String>> getDefaults() {
        Map<String, Set<String>> defaults = new HashMap<>();

        // Ores and raw resources
        defaults.put("Ores", Set.of(
                "coal", "charcoal",
                "iron_ore", "raw_iron",
                "gold_ore", "raw_gold",
                "copper_ore", "raw_copper",
                "diamond", "emerald", "lapis_lazuli", "redstone", "nether_quartz"
        ));

        // Wood and planks
        defaults.put("Wood", Set.of(
                "oak_log", "spruce_log", "birch_log", "jungle_log", "acacia_log", "dark_oak_log",
                "oak_planks", "spruce_planks", "birch_planks", "jungle_planks", "acacia_planks", "dark_oak_planks",
                "stick"
        ));

        // Farming and food items
        defaults.put("Food", Set.of(
                "wheat", "carrot", "potato", "beetroot",
                "apple", "bread", "cooked_beef", "cooked_chicken", "cooked_porkchop", "cooked_mutton", "cooked_cod", "cooked_salmon"
        ));

        // Stone and minerals
        defaults.put("Stone", Set.of(
                "stone", "cobblestone", "andesite", "diorite", "granite",
                "smooth_stone", "smooth_sandstone", "sandstone"
        ));

        // Mob drops
        defaults.put("Drops", Set.of(
                "string", "feather", "bone", "gunpowder", "rotten_flesh", "ender_pearl", "slime_ball"
        ));

        // Miscellaneous
        defaults.put("Misc", Set.of(
                "torch", "bucket", "shears", "compass", "map", "flint", "arrow", "book"
        ));

        return defaults;
    }
}
