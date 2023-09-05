package com.kterising;

import com.kterising.Command.Command;
import com.kterising.Listeners.*;
import com.kterising.Functions.MessagesConfig;
import com.kterising.Functions.StartGame;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.plugin.java.JavaPlugin;

public final class KteRising extends JavaPlugin {

    @Override
    public void onEnable() {
        new UpdateChecker(this, 112155).getVersion(version -> {
            if (this.getDescription().getVersion().equals(version)) {
                getLogger().info("There is not a new update available.");
            } else {
                getLogger().info("There is a new update available.");
            }
        });
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new Placeholder().register();
        } else {
            getLogger().warning(ChatColor.RED + "PlaceholderAPI not found. Some features do not work!");
        }

        getCommand("kterising").setExecutor(new Command(this));


        Bukkit.getPluginManager().registerEvents(new AutoStart(this), this);
        Bukkit.getPluginManager().registerEvents(new AutoMelt(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerDamage(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerDeath(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerJoin(this), this);
        Bukkit.getPluginManager().registerEvents(new NightVision(), this);
        Bukkit.getPluginManager().registerEvents(new AutoPickUp(this), this);

        StartGame.match = false;
        StartGame.PvP = false;
        StartGame.survivor = 0;
        StartGame.mode = "Classic";
        StartGame.winner = null;
        AutoStart.players = 0;

        getConfig().options().copyDefaults();
        saveDefaultConfig();

        System.out.println(ChatColor.GOLD + "[KteRising]" + ChatColor.GREEN + " Plugin Enabled!");
        System.out.println("Thanks for using our plugin!!");

        World world = Bukkit.getWorld("world");
        WorldBorder worldBorder = world.getWorldBorder();
        worldBorder.setSize(getConfig().getInt("world-size"));
        worldBorder.setCenter(0, 0);
        worldBorder.setDamageAmount(5);
        worldBorder.setDamageBuffer(2);

        //Messages.yml
        MessagesConfig.setup();

        MessagesConfig.get().addDefault("prefix","&6[KteRising] &r");
        MessagesConfig.get().addDefault("title.start-game.title","&6GAME STARTED");
        MessagesConfig.get().addDefault("title.start-game.sub","&aGood Luck");
        MessagesConfig.get().addDefault("title.lava-starting.title","&6Lava Rising");
        MessagesConfig.get().addDefault("title.lava-starting.sub","&cBeware of Lava");
        MessagesConfig.get().addDefault("title.finish-game.title","&6GAME OVER");
        MessagesConfig.get().addDefault("title.finish-game.sub","&aWinner: &f%winner%");
        MessagesConfig.get().addDefault("title.pvp-allow.title","&6PVP Allow!");
        MessagesConfig.get().addDefault("title.pvp-allow.sub","&cbe careful!");
        MessagesConfig.get().addDefault("title.player-died.title","&cYou Died");
        MessagesConfig.get().addDefault("title.player-died.sub","&aNext time!");
        MessagesConfig.get().addDefault("title.mode-refresh.title","&6Mode Changed!");
        MessagesConfig.get().addDefault("title.mode-refresh.sub","&aMode: &f%mode%");
        MessagesConfig.get().addDefault("pvp-enabled","&aEnabled");
        MessagesConfig.get().addDefault("pvp-disabled","&cDisabled");
        MessagesConfig.get().addDefault("start-time","&aStarting in: &f");
        MessagesConfig.get().addDefault("start-second"," &aseconds!");
        MessagesConfig.get().addDefault("title.worldborder-shrink.title","&cAttention!");
        MessagesConfig.get().addDefault("title.worldborder-shrink.sub","&6Space is shrinking!");
        MessagesConfig.get().addDefault("title.eliminated.title","&cYou Are eliminated");
        MessagesConfig.get().addDefault("title.eliminated.sub","&aNext time!");
        MessagesConfig.get().addDefault("message.command-title","&8&m-----------&6KteRising-----------");
        MessagesConfig.get().addDefault("message.dont-permission","&4You do''nt have permission.");
        MessagesConfig.get().addDefault("message.already-started","&4Game already started.");
        MessagesConfig.get().addDefault("message.plugin-reload","&aPlugin Reloaded");
        MessagesConfig.get().addDefault("message.command-game-started","&4Game already started.");

        MessagesConfig.get().options().copyDefaults(true);
        MessagesConfig.save();

    }

        @Override
        public void onDisable() {
        System.out.println(ChatColor.GOLD + "[KteRising]" + ChatColor.RED + "Plugin Disabled!");
    }
}
