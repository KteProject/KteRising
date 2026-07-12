package com.kteproject.kterising.stats;

import com.kteproject.kterising.KteRising;
import com.kteproject.kterising.database.DatabaseManager;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public final class StatsManager {

    private static final String UPSERT = """
            INSERT INTO kterising_stats (uuid, name, gamesPlayed, wins, kills, deaths)
            VALUES (?, ?, ?, ?, ?, ?)
            ON CONFLICT(uuid) DO UPDATE SET
                name=excluded.name,
                gamesPlayed=excluded.gamesPlayed,
                wins=excluded.wins,
                kills=excluded.kills,
                deaths=excluded.deaths
            """;

    private static final String UPSERT_MYSQL = """
            INSERT INTO kterising_stats (uuid, name, gamesPlayed, wins, kills, deaths)
            VALUES (?, ?, ?, ?, ?, ?)
            ON DUPLICATE KEY UPDATE
                name=VALUES(name),
                gamesPlayed=VALUES(gamesPlayed),
                wins=VALUES(wins),
                kills=VALUES(kills),
                deaths=VALUES(deaths)
            """;

    private static KteRising plugin;
    private static boolean mysql;

    private StatsManager() {}

    public static void init(KteRising pluginInstance) {
        plugin = pluginInstance;
        String type = plugin.getConfig().getString("database.type", "SQLITE");
        mysql = type != null && type.equalsIgnoreCase("MYSQL");

        plugin.getServer().getAsyncScheduler().runAtFixedRate(
                plugin,
                task -> saveAll(),
                120,
                120,
                TimeUnit.SECONDS
        );
    }

    public static void load(Player player) {
        if (plugin == null || !DatabaseManager.isReady()) return;

        UUID uuid = player.getUniqueId();
        String name = player.getName();

        plugin.getServer().getAsyncScheduler().runNow(plugin, task -> {
            try (Connection c = DatabaseManager.getConnection()) {
                PlayerStats stats = null;

                try (PreparedStatement ps = c.prepareStatement(
                        "SELECT gamesPlayed, wins, kills, deaths FROM kterising_stats WHERE uuid=?")) {
                    ps.setString(1, uuid.toString());
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            stats = new PlayerStats(uuid, name);
                            stats.gamesPlayed = rs.getInt("gamesPlayed");
                            stats.wins = rs.getInt("wins");
                            stats.kills = rs.getInt("kills");
                            stats.deaths = rs.getInt("deaths");
                        }
                    }
                }

                if (stats == null) {
                    stats = new PlayerStats(uuid, name);
                    try (PreparedStatement ps = c.prepareStatement(
                            "INSERT INTO kterising_stats (uuid, name, gamesPlayed, wins, kills, deaths) VALUES (?, ?, 0, 0, 0, 0)")) {
                        ps.setString(1, uuid.toString());
                        ps.setString(2, name);
                        ps.executeUpdate();
                    }
                }

                StatsCache.put(uuid, stats);
            } catch (Exception ex) {
                plugin.getLogger().warning("Failed to load stats for " + name + ": " + ex.getMessage());
            }
        });
    }

    public static void saveSnapshot(PlayerStats stats) {
        if (plugin == null || stats == null || !DatabaseManager.isReady()) return;

        PlayerStats snapshot = stats.snapshot();
        plugin.getServer().getAsyncScheduler().runNow(plugin, task -> write(snapshot));
    }

    public static void syncSave(PlayerStats stats) {
        if (stats == null || !DatabaseManager.isReady()) return;
        write(stats.snapshot());
    }

    private static void write(PlayerStats stats) {
        String sql = mysql ? UPSERT_MYSQL : UPSERT;
        try (Connection c = DatabaseManager.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, stats.getUuid().toString());
            ps.setString(2, stats.getName());
            ps.setInt(3, stats.gamesPlayed);
            ps.setInt(4, stats.wins);
            ps.setInt(5, stats.kills);
            ps.setInt(6, stats.deaths);
            ps.executeUpdate();
        } catch (Exception ex) {
            if (plugin != null) {
                plugin.getLogger().warning("Failed to save stats for " + stats.getName() + ": " + ex.getMessage());
            }
        }
    }

    public static void saveAll() {
        List<PlayerStats> all = new ArrayList<>(StatsCache.getAll().values());
        if (all.isEmpty() || plugin == null || !DatabaseManager.isReady()) return;

        List<PlayerStats> snapshots = all.stream().map(PlayerStats::snapshot).toList();
        plugin.getServer().getAsyncScheduler().runNow(plugin, task -> {
            for (PlayerStats stats : snapshots) {
                write(stats);
            }
        });
    }

    public static void unload(Player player) {
        PlayerStats stats = StatsCache.get(player.getUniqueId());
        if (stats != null) {
            saveSnapshot(stats);
        }
        StatsCache.remove(player.getUniqueId());
    }

    public static void shutdown() {
        for (PlayerStats stats : StatsCache.getAll().values()) {
            syncSave(stats);
        }
        StatsCache.init();
    }
}
