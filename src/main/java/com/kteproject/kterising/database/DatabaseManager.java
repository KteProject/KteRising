package com.kteproject.kterising.database;

import com.kteproject.kterising.KteRising;

import java.sql.Connection;
import java.sql.Statement;

public final class DatabaseManager {

    private static SQLProvider provider;

    private DatabaseManager() {}

    public static void init(KteRising plugin) throws Exception {
        String type = plugin.getConfig().getString("database.type", "SQLITE").toUpperCase();
        StorageType dbType = StorageType.valueOf(type);

        if (dbType == StorageType.SQLITE) {
            provider = new SQLiteProvider(plugin);
        } else {
            provider = new MySQLProvider(plugin);
        }

        provider.init();
        createTables();
        plugin.getLogger().info("Database initialized successfully!");
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
        if (provider == null) {
            throw new IllegalStateException("Database is not initialized");
        }
        return provider.getConnection();
    }

    public static boolean isReady() {
        return provider != null;
    }

    public static void shutdown() {
        if (provider != null) {
            provider.shutdown();
            provider = null;
        }
    }
}
