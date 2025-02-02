package com.kterising.Functions;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class Rewards {
    public static void killPlayer(Player player) {

        FileConfiguration config = Bukkit.getPluginManager().getPlugin("KteRising").getConfig();

        if(!config.getBoolean("rewards.enabled")) return;

        for (String command : config.getStringList("rewards.kill-player")) {
            Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), command.replace("%player%", player.getName()));
        }

    }

    public static void winPlayer(Player player) {
        FileConfiguration config = Bukkit.getPluginManager().getPlugin("KteRising").getConfig();

        if(!config.getBoolean("rewards.enabled")) return;

        for (String command : config.getStringList("rewards.win-player")) {
            Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), command.replace("%player%", player.getName()));
        }

    }

    public static void deathPlayer(Player player) {
        FileConfiguration config = Bukkit.getPluginManager().getPlugin("KteRising").getConfig();

        if(!config.getBoolean("rewards.enabled")) return;

        for (String command : config.getStringList("rewards.death-player")) {
            Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), command.replace("%player%", player.getName()));
        }
    }
}
