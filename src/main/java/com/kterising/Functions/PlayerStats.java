package com.kterising.Functions;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;

public class PlayerStats implements Listener {
    private static File statsFile;
    private static YamlConfiguration statsConfig;

    public static void setup(Plugin plugin) {
        statsFile = new File(plugin.getDataFolder(), "playerstats.yml");

        if (!statsFile.exists()) {
            plugin.getLogger().info("playerstats.yml file not found, creating");
            plugin.saveResource("playerstats.yml", false);
        }

        statsConfig = YamlConfiguration.loadConfiguration(statsFile);
    }

    public static void reload() {
        statsConfig = YamlConfiguration.loadConfiguration(statsFile);
    }

    public static YamlConfiguration getConfig() {
        return statsConfig;
    }

    public static void save() {
        try {
            statsConfig.save(statsFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        reload();
        if (getConfig().getConfigurationSection(p.getName()) == null) {
            getConfig().set(p.getName() + ".win", 0);
            getConfig().set(p.getName() + ".death", 0);
            getConfig().set(p.getName() + ".kill", 0);
            save();
        }
    }

    public static int getWin(Player p) {
        reload();
        return getConfig().getInt(p.getName() + ".win");
    }

    public static int getDeath(Player p) {
        reload();
        return getConfig().getInt(p.getName() + ".death");
    }

    public static int getKill(Player p) {
        reload();
        return getConfig().getInt(p.getName() + ".kill");
    }

    public static void addWin(Player p) {
        getConfig().set(p.getName() + ".win", getConfig().getInt(p.getName() + ".win") + 1);
        save();
    }

    public static void addDeath(Player p) {
        getConfig().set(p.getName() + ".death", getConfig().getInt(p.getName() + ".death") + 1);
        save();
    }

    public static void addKill(Player p) {
        getConfig().set(p.getName() + ".kill", getConfig().getInt(p.getName() + ".kill") + 1);
        save();
    }
}
