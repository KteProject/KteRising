package com.kteproject.kterising.managers.vote;

import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class VoteManager {

    private final Map<String, Integer> modeVotes = new HashMap<>();
    private final Map<UUID, String> playerVotes = new HashMap<>();
    private final Random rng = new Random();

    public VoteManager(ConfigurationSection modesSection) {
        reload(modesSection);
    }

    public void reload(ConfigurationSection modesSection) {
        modeVotes.clear();
        playerVotes.clear();
        if (modesSection == null) return;
        for (String mode : modesSection.getKeys(false)) {
            modeVotes.put(mode.toLowerCase(Locale.ROOT), 0);
        }
    }

    public void resetVotes() {
        playerVotes.clear();
        for (String mode : modeVotes.keySet()) {
            modeVotes.put(mode, 0);
        }
    }

    public boolean vote(UUID playerUUID, String modeName) {
        String cleanMode = modeName.toLowerCase(Locale.ROOT);

        Integer currentVotes = modeVotes.get(cleanMode);
        if (currentVotes == null) {
            return false;
        }

        String previous = playerVotes.get(playerUUID);
        if (previous != null) {
            if (previous.equals(cleanMode)) {
                return false;
            }

            int prevCount = modeVotes.getOrDefault(previous, 0);
            if (prevCount > 0) {
                modeVotes.put(previous, prevCount - 1);
            }
        }

        modeVotes.put(cleanMode, currentVotes + 1);
        playerVotes.put(playerUUID, cleanMode);
        return true;
    }

    public int getVotesForMode(String modeName) {
        return modeVotes.getOrDefault(modeName.toLowerCase(Locale.ROOT), 0);
    }

    public Map<String, Integer> getModeVotes() {
        return Collections.unmodifiableMap(modeVotes);
    }

    public String getWinningMode() {
        if (modeVotes.isEmpty()) return null;

        int total = 0;
        for (int v : modeVotes.values()) total += v;

        if (total == 0) {
            List<String> keys = new ArrayList<>(modeVotes.keySet());
            return keys.get(rng.nextInt(keys.size()));
        }

        String bestMode = null;
        int bestVotes = -1;
        for (Map.Entry<String, Integer> entry : modeVotes.entrySet()) {
            if (entry.getValue() > bestVotes) {
                bestVotes = entry.getValue();
                bestMode = entry.getKey();
            }
        }
        return bestMode;
    }

    public String getPlayerVote(UUID playerUUID) {
        return playerVotes.get(playerUUID);
    }
}
