package com.kterising.Functions;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;

public class ItemsConfig {

    private static File itemsFile;
    private static YamlConfiguration itemsConfig;

    public static void setup(Plugin plugin) {
        itemsFile = new File(plugin.getDataFolder(), "items.yml");

        if (!itemsFile.exists()) {
            plugin.saveResource("items.yml", false);
        }

        itemsConfig = YamlConfiguration.loadConfiguration(itemsFile);
    }

    public static void reload() {
        itemsConfig = YamlConfiguration.loadConfiguration(itemsFile);
    }

    public static YamlConfiguration getConfig() {
        return itemsConfig;
    }

}