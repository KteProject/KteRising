package com.kteproject.kterising.stats;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class StatsCache {

    private static final Map<UUID, PlayerStats> CACHE = new ConcurrentHashMap<>();

    private StatsCache() {}

    public static void init() {
        CACHE.clear();
    }

    public static PlayerStats get(UUID uuid) {
        return CACHE.get(uuid);
    }

    public static void put(UUID uuid, PlayerStats stats) {
        CACHE.put(uuid, stats);
    }

    public static void remove(UUID uuid) {
        CACHE.remove(uuid);
    }

    public static Map<UUID, PlayerStats> getAll() {
        return CACHE;
    }

    public static void recordKill(UUID uuid) {
        PlayerStats stats = CACHE.get(uuid);
        if (stats != null) {
            stats.kills++;
        }
    }

    public static void recordDeath(UUID uuid) {
        PlayerStats stats = CACHE.get(uuid);
        if (stats != null) {
            stats.deaths++;
        }
    }

    public static void recordWin(UUID uuid) {
        PlayerStats stats = CACHE.get(uuid);
        if (stats != null) {
            stats.wins++;
        }
    }

    public static void recordGamePlayed(UUID uuid) {
        PlayerStats stats = CACHE.get(uuid);
        if (stats != null) {
            stats.gamesPlayed++;
        }
    }
}
