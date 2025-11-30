package com.kteproject.kterising.managers.vote;
import com.kteproject.kterising.KteRising;
import org.bukkit.configuration.ConfigurationSection;
import java.util.*;

public class VoteManager {

    private static Map<String, Integer> modeVotes;
    private static Map<UUID, String> playerVotes;
    private static final Random RNG = new Random();

    public VoteManager(ConfigurationSection modesSection) {
        modeVotes = new HashMap<>();
        playerVotes = new HashMap<>();

        if (modesSection != null) {
            for (String mode : modesSection.getKeys(false)) {
                modeVotes.put(mode.toLowerCase(Locale.ROOT), 0);
            }
        }
    }

    public static void reloadVoteManager() {
        modeVotes = new HashMap<>();
        playerVotes = new HashMap<>();

        ConfigurationSection modesSection = KteRising.getInstance().getConfig().getConfigurationSection("modes-configuration");

        if (modesSection != null) {
            for (String mode : modesSection.getKeys(false)) {
                modeVotes.put(mode.toLowerCase(Locale.ROOT), 0);
            }
        }
    }

    public static void resetVotes() {
        modeVotes.clear();
        playerVotes.clear();

        for (String mode : KteRising.getConfiguration().getConfigurationSection("modes-configuration").getKeys(false)) {
            modeVotes.put(mode.toLowerCase(Locale.ROOT), 0);
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

            int prevCount = modeVotes.get(previous);
            if (prevCount > 0) {
                modeVotes.put(previous, prevCount - 1);
            }

            modeVotes.put(cleanMode, currentVotes + 1);

            playerVotes.put(playerUUID, cleanMode);
            return true;
        }

        modeVotes.put(cleanMode, currentVotes + 1);
        playerVotes.put(playerUUID, cleanMode);
        return true;
    }

    public int getVotesForMode(String modeName) {
        Integer v = modeVotes.get(modeName.toLowerCase(Locale.ROOT));
        return v != null ? v : 0;
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
            return keys.get(RNG.nextInt(keys.size()));
        }

        String bestMode = null;
        int bestVotes = -1;

        for (Map.Entry<String, Integer> entry : modeVotes.entrySet()) {
            if (entry.getValue() > bestVotes) {
                bestVotes = entry.getValue();
                bestMode = entry.getKey();
            }
        }

        if (bestMode == null) {
            return modeVotes.keySet().iterator().next();
        }

        return bestMode;
    }


    public String getPlayerVote(UUID playerUUID) {
        return playerVotes.get(playerUUID);
    }
}
