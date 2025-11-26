package com.kteproject.kterising.stats;

import com.kteproject.kterising.KteRising;
import com.kteproject.kterising.database.DatabaseManager;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.UUID;

public class StatsManager {

    private static KteRising plugin;

    public static void init() {
        plugin = KteRising.getInstance();

        plugin.getServer().getAsyncScheduler().runAtFixedRate(
                plugin,
                (ScheduledTask task) -> saveAll(),
                120,
                120,
                java.util.concurrent.TimeUnit.SECONDS
        );
    }


    public static void load(Player player) {
        UUID uuid = player.getUniqueId();
        String name = player.getName();

        plugin.getServer().getAsyncScheduler().runNow(plugin, (task) -> {
            try (Connection c = DatabaseManager.getConnection()) {

                PlayerStats stats = null;

                try (PreparedStatement ps = c.prepareStatement(
                        "SELECT * FROM kterising_stats WHERE uuid=?")) {
                    ps.setString(1, uuid.toString());
                    ResultSet rs = ps.executeQuery();

                    if (rs.next()) {
                        stats = new PlayerStats(uuid, name);
                        stats.gamesPlayed = rs.getInt("gamesPlayed");
                        stats.wins = rs.getInt("wins");
                    }
                }

                if (stats == null) {
                    stats = new PlayerStats(uuid, name);

                    try (PreparedStatement ps = c.prepareStatement(
                            "INSERT INTO kterising_stats (uuid, name, gamesPlayed, wins) " +
                                    "VALUES (?, ?, ?, ?, ?, ?)")) {

                        ps.setString(1, uuid.toString());
                        ps.setString(2, name);
                        ps.setInt(3, 0);
                        ps.setInt(4, 0);
                        ps.executeUpdate();
                    }
                }

                StatsCache.put(uuid, stats);

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    // ASYNC SAVE
    public static void save(PlayerStats stats) {
        plugin.getServer().getAsyncScheduler().runNow(plugin, (task) -> {
            try (Connection c = DatabaseManager.getConnection();
                 PreparedStatement ps = c.prepareStatement(
                         "UPDATE kterising_stats SET name=?, gamesPlayed=?, wins=? WHERE uuid=?"
                 )) {

                ps.setString(1, stats.getName());
                ps.setInt(2, stats.gamesPlayed);
                ps.setInt(3, stats.wins);
                ps.setString(4, stats.getUuid().toString());
                ps.executeUpdate();

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    // SYNCHRONOUS SAVE — sadece shutdown için
    public static void syncSave(PlayerStats stats) {
        try (Connection c = DatabaseManager.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "UPDATE kterising_stats SET name=?, gamesPlayed=?, wins=? WHERE uuid=?"
             )) {

            ps.setString(1, stats.getName());
            ps.setInt(2, stats.gamesPlayed);
            ps.setInt(3, stats.wins);
            ps.setString(4, stats.getUuid().toString());
            ps.executeUpdate();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void saveAll() {
        for (PlayerStats stats : StatsCache.getAll().values()) {
            save(stats);
        }
    }

    public static void unload(Player player) {
        PlayerStats stats = StatsCache.get(player.getUniqueId());
        if (stats != null) save(stats);
        StatsCache.remove(player.getUniqueId());
    }

    public static void shutdown() {
        for (PlayerStats stats : StatsCache.getAll().values()) {
            syncSave(stats);
        }
    }
}
