package com.kteproject.kterising;

import com.kteproject.kterising.database.DatabaseManager;
import com.kteproject.kterising.game.MatchSession;
import com.kteproject.kterising.listeners.AutoPickUp;
import com.kteproject.kterising.listeners.GameListeners;
import com.kteproject.kterising.managers.CommandManager;
import com.kteproject.kterising.managers.LobbyItems;
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
import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class KteRising extends JavaPlugin {

    private static KteRising instance;
    private World arenaWorld;
    private Location cachedSpawn;
    private MatchSession match;
    private VoteManager voteManager;
    private VoteGui voteGui;
    private volatile boolean ready;

    @Override
    public void onEnable() {
        instance = this;
        ready = false;

        saveDefaultConfig();
        MessagesConfig.setup(this);
        ChatUtil.init(this);
        CommandManager.init(this);

        Bukkit.getPluginManager().registerEvents(new GameListeners(), this);
        Bukkit.getPluginManager().registerEvents(new AutoPickUp(this), this);
        Bukkit.getPluginManager().registerEvents(new LobbyItems(), this);

        voteManager = new VoteManager(getConfig().getConfigurationSection("modes-configuration"));
        voteGui = new VoteGui(this, voteManager);

        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new Placeholder().register();
        }

        resolveArenaWorld();
        applyWorldRules(arenaWorld);

        printBanner("Loading...");

        getServer().getAsyncScheduler().runNow(this, task -> {
            try {
                StatsCache.init();
                DatabaseManager.init(this);
                StatsManager.init(this);

                getServer().getGlobalRegionScheduler().run(this, mainTask -> {
                    ModeManager.loadModes();
                    voteGui.reload();
                    match = new MatchSession(this);
                    match.init(false);
                    ready = true;

                    for (Player player : Bukkit.getOnlinePlayers()) {
                        StatsManager.load(player);
                    }

                    printBanner("Enabled");
                });
            } catch (Exception ex) {
                getLogger().severe("Failed to initialize plugin: " + ex.getMessage());
                ex.printStackTrace();
                getServer().getGlobalRegionScheduler().run(this, t -> getServer().getPluginManager().disablePlugin(this));
            }
        });

        if (getConfig().getBoolean("plugin-configurations.bstats-metrics")) {
            new Metrics(this, 21969);
        }

        if (getConfig().getBoolean("plugin-configurations.update-check")) {
            UpdateCheck updateCheck = new UpdateCheck(this, 112155);
            getLogger().info("Checking for updates...");
            updateCheck.isUpdateAvailable(isAvailable -> {
                if (isAvailable) {
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
        ready = false;
        if (match != null) {
            match.shutdown();
        }
        StatsManager.shutdown();
        DatabaseManager.shutdown();
        ChatUtil.shutdown();
        printBanner("Disabled");
    }

    public void resolveArenaWorld() {
        String worldName = getConfig().getString("world-configurations.world-name", "world");
        arenaWorld = Bukkit.getWorld(worldName);
        if (arenaWorld == null) {
            getLogger().warning("[KteRising] World '" + worldName + "' not found. Using default world.");
            arenaWorld = Bukkit.getWorlds().get(0);
        }
    }

    public void applyWorldRules(World world) {
        if (world == null) return;
        world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
        world.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
        world.setGameRule(GameRule.DO_MOB_SPAWNING, false);
        world.setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, true);
    }

    public World getArenaWorld() {
        if (arenaWorld == null) {
            resolveArenaWorld();
        }
        return arenaWorld;
    }

    public Location getSpawnLocation() {
        return cachedSpawn;
    }

    public void setSpawnLocation(Location location) {
        cachedSpawn = location;
    }

    public VoteManager getVoteManager() {
        return voteManager;
    }

    public VoteGui getVoteGui() {
        return voteGui;
    }

    public MatchSession getMatchSession() {
        return match;
    }

    public boolean isReady() {
        return ready;
    }

    public static KteRising getInstance() {
        return instance;
    }

    public static MatchSession getMatch() {
        return instance == null ? null : instance.match;
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
        getLogger().info(" | / /     \\ |    \\      Version: " + getDescription().getVersion());
        getLogger().info(" | \\ |     | |   /_      Status: " + status);
        getLogger().info(" \\_|\\_\\    \\_/ \\____\\");
        getLogger().info("");
    }
}
