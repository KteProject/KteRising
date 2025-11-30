package com.kteproject.kterising.stats;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class StatsCache {

    private static Map<UUID, PlayerStats> cache;

    public static void init() {
        cache = new ConcurrentHashMap<>();
    }

    public static PlayerStats get(UUID uuid) {
        return cache.get(uuid);
    }

    public static void put(UUID uuid, PlayerStats stats) {
        cache.put(uuid, stats);
    }

    public static void remove(UUID uuid) {
        cache.remove(uuid);
    }

    public static Map<UUID, PlayerStats> getAll() {
        return cache;
    }
}
