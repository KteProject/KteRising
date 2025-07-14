package com.kterising.Functions;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class ModManager {
    private final Map<String, Integer> modVotes = new HashMap<>();
    private final Map<UUID, String> playerVotes = new HashMap<>();
    private final Map<String, Integer> teamVotes = new HashMap<>();
    private final Map<UUID, String> playerTeamVotes = new HashMap<>();

    public ModManager() {
        for (String mod : getModNames()) {
            modVotes.put(mod, 0);
        }
        teamVotes.put(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("vote.team"))), 0);
        teamVotes.put(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("vote.no-team"))), 0);
    }

    public String[] getModNames() {
        return new String[]{"Classic", "Elytra", "Trident", "OP", "UltraOP", "ElytraOP", "TridentOP"};
    }

    public void voteForMod(Player player, String mod) {
        UUID playerId = player.getUniqueId();
        String previousVote = playerVotes.get(playerId);
        int multiplier = VoteUtils.getVoteMultiplier(player);

        if (previousVote != null) {
            int previousMultiplier = VoteUtils.getVoteMultiplier(player);
            modVotes.put(previousVote, modVotes.get(previousVote) - previousMultiplier);
        }

        modVotes.put(mod, modVotes.getOrDefault(mod, 0) + multiplier);
        playerVotes.put(playerId, mod);
    }

    public void voteForTeam(Player player, String team) {
        UUID playerId = player.getUniqueId();
        String previousVote = playerTeamVotes.get(playerId);
        int multiplier = VoteUtils.getVoteMultiplier(player);

        if (previousVote != null) {
            int previousMultiplier = VoteUtils.getVoteMultiplier(player);
            teamVotes.put(previousVote, teamVotes.get(previousVote) - previousMultiplier);
        }

        teamVotes.put(team, teamVotes.getOrDefault(team, 0) + multiplier);
        playerTeamVotes.put(playerId, team);
    }

    public Integer getVotes(String mod) {
        return modVotes.getOrDefault(mod, 0);
    }

    public Integer getTeamVotes(String team) {
        return teamVotes.getOrDefault(team, 0);
    }

    public String getVoteForPlayer(UUID playerId) {
        return playerVotes.get(playerId);
    }

    public String getTeamVoteForPlayer(UUID playerId) {
        return playerTeamVotes.get(playerId);
    }

    public Map<String, Integer> getModVotes() {
        return new HashMap<>(modVotes);
    }

    public Map<String, Integer> getTeamVotes() {
        return new HashMap<>(teamVotes);
    }

    public void resetVotes() {
        modVotes.replaceAll((m, v) -> 0);

        teamVotes.replaceAll((t, v) -> 0);

        playerVotes.clear();
        playerTeamVotes.clear();
    }

    public void removeModVote(Player player) {
        UUID playerId = player.getUniqueId();
        String votedMod = playerVotes.remove(playerId);
        if (votedMod != null) {
            int multiplier = VoteUtils.getVoteMultiplier(player);
            modVotes.put(votedMod, modVotes.getOrDefault(votedMod, multiplier) - multiplier);
            if (modVotes.get(votedMod) <= 0) {
                modVotes.remove(votedMod);
            }
        }
    }


    public void removeTeamVote(Player player) {
        UUID playerId = player.getUniqueId();
        String votedTeam = playerTeamVotes.remove(playerId);
        if (votedTeam != null) {
            int multiplier = VoteUtils.getVoteMultiplier(player);
            teamVotes.put(votedTeam, teamVotes.getOrDefault(votedTeam, multiplier) - multiplier);
            if (teamVotes.get(votedTeam) <= 0) {
                teamVotes.remove(votedTeam);
            }
        }
    }
}
