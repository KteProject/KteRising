package com.kteproject.kterising;

import com.kteproject.kterising.database.DatabaseManager;
import com.kteproject.kterising.game.AutoStart;
import com.kteproject.kterising.game.Game;
import com.kteproject.kterising.listeners.AutoPickUp;
import com.kteproject.kterising.listeners.GameListeners;
import com.kteproject.kterising.managers.CommandManager;
import com.kteproject.kterising.managers.LobbyItems;
import com.kteproject.kterising.managers.SafeBiomeManager;
import com.kteproject.kterising.managers.gamemodes.ModeManager;
import com.kteproject.kterising.managers.vote.VoteGui;
import com.kteproject.kterising.managers.vote.VoteManager;
import com.kteproject.kterising.stats.StatsCache;
import com.kteproject.kterising.stats.StatsManager;
import com.kteproject.kterising.utils.ChatUtil;
import com.kteproject.kterising.utils.MessagesConfig;
import com.kteproject.kterising.utils.Metrics;
import com.kteproject.kterising.utils.UpdateCheck;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public final class KteRising extends JavaPlugin {

    public static KteRising instance;
    private VoteManager voteManager;
    private VoteGui voteGui;
    private static Location cachedSpawn;

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();
        MessagesConfig.setup(this);
        ChatUtil.init(this);
        CommandManager.init(this);

        Bukkit.getPluginManager().registerEvents(new GameListeners(), this);
        Bukkit.getPluginManager().registerEvents(new AutoStart(), this);
        Bukkit.getPluginManager().registerEvents(new AutoPickUp(this), this);
        Bukkit.getPluginManager().registerEvents(new LobbyItems(), this);

        voteManager = new VoteManager(getConfig().getConfigurationSection("modes-configuration"));
        voteGui = new VoteGui(this, voteManager);

        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new Placeholder().register();
        }

        printBanner("Loading...");

        Bukkit.getAsyncScheduler().runNow(this, task -> {
            StatsCache.init();
            DatabaseManager.init();
            StatsManager.init();
            ModeManager.loadModes();
            voteGui.init();
        });

        SafeBiomeManager.findSafeLocation();
        cachedSpawn = SafeBiomeManager.getSafeLocation();

        Game.init();

        if (getConfiguration().getBoolean("plugin-configurations.bstats-metrics")) {
            new Metrics(this, 21969);
        }

        printBanner("Enabled");

        if(getConfiguration().getBoolean("plugin-configurations.update-check")) {
            UpdateCheck updateCheck = new UpdateCheck(this, 112155);
            getLogger().info("Checking for updates...");
            updateCheck.isUpdateAvailable(isAvailable -> {
                if(isAvailable) {
                    getLogger().info("");
                    getLogger().info("   WARNING!");
                    getLogger().info(" A new update for KteRising is available!");
                    getLogger().info(" Please update the plugin as soon as possible.");
                    getLogger().info("");
                } else {
                    getLogger().info("The plugin is up to date.");
                }
            });
        }
    }

    @Override
    public void onDisable() {
        StatsManager.shutdown();
        DatabaseManager.shutdown();
        printBanner("Disabled");
    }

    public void cacheSpawn() {
        World w = Bukkit.getWorld(getConfig().getString("world-configurations.world-name", "world"));

        if (w == null) {
            getLogger().warning("[KteRising] Config world not found. Using default world.");
            w = Bukkit.getWorlds().get(0);
        }

        int y = w.getHighestBlockYAt(0, 0) + 1;
        cachedSpawn = new Location(w, 0.5, y, 0.5);
    }

    public static Location getSpawnLocation() {
        return cachedSpawn;
    }

    public VoteManager getVoteManager() {
        return voteManager;
    }

    public VoteGui getVoteGui() {
        return voteGui;
    }

    public static KteRising getInstance() {
        return instance;
    }

    public static FileConfiguration getConfiguration() {
        return instance.getConfig();
    }

    public static boolean isVotingMenuEnabled() {
        return getConfiguration().getBoolean("voting-menu-configuration.enabled", true);
    }

    private void printBanner(String status) {
        getLogger().info("");
        getLogger().info(" _  __   _____   _____");
        getLogger().info(" / |/ /  /__ __/ Y __/   KteRising");
        getLogger().info(" | / /     \\ |    \\      Version: 0.0.1-DEV");
        getLogger().info(" | \\ |     | |   /_      Status: " + status);
        getLogger().info(" \\_|\\_\\    \\_/ \\____\\");
        getLogger().info("");
    }
}
