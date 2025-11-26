package com.kteproject.kterising.database;

import com.kteproject.kterising.KteRising;
import org.bukkit.Bukkit;

import java.sql.Connection;
import java.sql.Statement;

public class DatabaseManager {

    private static SQLProvider provider;

    public static void init() {
        KteRising plugin = KteRising.getInstance();

        String type = plugin.getConfig().getString("database.type", "SQLITE").toUpperCase();
        StorageType dbType = StorageType.valueOf(type);

        Bukkit.getAsyncScheduler().runNow(plugin, task -> {

            try {
                if (dbType == StorageType.SQLITE) {
                    provider = new SQLiteProvider(plugin);
                } else {
                    provider = new MySQLProvider(plugin);
                }

                provider.init();

                createTables();

                plugin.getLogger().info("Database initialized successfully!");

            } catch (Exception e) {
                e.printStackTrace();
            }

        });
    }

    private static void createTables() throws Exception {
        try (Connection c = provider.getConnection();
             Statement st = c.createStatement()) {

            st.executeUpdate("""
                CREATE TABLE IF NOT EXISTS kterising_stats (
                    uuid TEXT PRIMARY KEY,
                    name TEXT,
                    gamesPlayed INTEGER DEFAULT 0,
                    wins INTEGER DEFAULT 0,
                    kills INTEGER DEFAULT 0,
                    deaths INTEGER DEFAULT 0
                );
            """);
        }
    }

    public static Connection getConnection() throws Exception {
        return provider.getConnection();
    }

    public static void shutdown() {
        if (provider != null) provider.shutdown();
    }
}
