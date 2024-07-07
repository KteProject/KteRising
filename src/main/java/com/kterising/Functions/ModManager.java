package com.kterising.Functions;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ModManager {
    private final Map<String, Integer> modVotes = new HashMap<>();
    private final Map<UUID, String> playerVotes = new HashMap<>();

    public ModManager() {
        for (String mod : getModNames()) {
            modVotes.put(mod, 0);
        }
    }

    public String[] getModNames() {
        return new String[] {"Classic", "Elytra", "Trident", "OP", "UltraOP", "ElytraOP", "TridentOP"};
    }

    public void voteForMod(Player player, String mod) {
        UUID playerId = player.getUniqueId();
        String previousVote = playerVotes.get(playerId);

        if (previousVote != null) {
            modVotes.put(previousVote, modVotes.get(previousVote) - 1);
        }

        modVotes.put(mod, modVotes.getOrDefault(mod, 0) + 1);
        playerVotes.put(playerId, mod);
    }

    public Integer getVotes(String mod) {
        return modVotes.getOrDefault(mod, 0);
    }

    public String getVoteForPlayer(UUID playerId) {
        return playerVotes.get(playerId);
    }

    public Map<String, Integer> getModVotes() {
        return new HashMap<>(modVotes);
    }
}