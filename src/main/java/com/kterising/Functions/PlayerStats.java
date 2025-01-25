package com.kterising.Functions;

import java.io.File;
import java.io.IOException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;

public class PlayerStats implements Listener {
    private static File statsFile;

    private static YamlConfiguration statsConfig;

    public static void setup(Plugin plugin) {
        statsFile = new File(plugin.getDataFolder(), "playerstats.yml");
        if (!statsFile.exists())
            try {
                statsFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
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
        String playerPath = p.getName();
        if (!getConfig().contains(playerPath)) {
            getConfig().createSection(playerPath);
            getConfig().set(playerPath + ".win", 0);
            getConfig().set(playerPath + ".kill", 0);
            getConfig().set(playerPath + ".death", 0);
            save();
        }
    }

    public static int getWin(Player p) {
        return getConfig().getInt(p.getName() + ".win");
    }

    public static int getDeath(Player p) {
        return getConfig().getInt(p.getName() + ".death");
    }

    public static int getKill(Player p) {
        return getConfig().getInt(p.getName() + ".kill");
    }

    public static void addWin(Player p) {
        getConfig().set(p.getName() + ".win", getWin(p) + 1);
        save();
    }

    public static void addDeath(Player p) {
        getConfig().set(p.getName() + ".death", getDeath(p) + 1);
        save();
    }

    public static void addKill(Player p) {
        getConfig().set(p.getName() + ".kill", getKill(p) + 1);
        save();
    }
}