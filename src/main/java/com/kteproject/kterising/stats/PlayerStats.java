package com.kteproject.kterising.stats;

import java.util.UUID;

public class PlayerStats {
    private final UUID uuid;
    private String name;

    public int gamesPlayed;
    public int wins;

    public PlayerStats(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
    }

    public UUID getUuid() { return uuid; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
