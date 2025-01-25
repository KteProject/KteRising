package com.kterising.Team;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Team {
    private final String name;
    private final int maxSize;
    private final List<Player> players;

    public Team(String name, int maxSize) {
        this.name = name;
        this.maxSize = maxSize;
        this.players = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public int getMaxSize() {
        return maxSize;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void addPlayer(Player player) {
        if (players.size() < maxSize) {
            players.add(player);
        }
    }

    public String getDisplayName() {
        return name + " [" + players.size() + "/" + maxSize + "]";
    }

    public String getPlayersNames() {
        return players.stream().map(Player::getName).collect(Collectors.joining(", "));
    }
}