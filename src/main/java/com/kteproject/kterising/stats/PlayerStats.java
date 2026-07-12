package com.kteproject.kterising.stats;

import java.util.UUID;

public class PlayerStats {
    private final UUID uuid;
    private String name;

    public int gamesPlayed;
    public int wins;
    public int kills;
    public int deaths;

    public PlayerStats(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PlayerStats snapshot() {
        PlayerStats copy = new PlayerStats(uuid, name);
        copy.gamesPlayed = gamesPlayed;
        copy.wins = wins;
        copy.kills = kills;
        copy.deaths = deaths;
        return copy;
    }
}
