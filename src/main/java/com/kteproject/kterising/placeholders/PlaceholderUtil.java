package com.kteproject.kterising.placeholders;

import com.kteproject.kterising.KteRising;
import com.kteproject.kterising.game.MatchSession;
import com.kteproject.kterising.stats.PlayerStats;
import com.kteproject.kterising.stats.StatsCache;
import com.kteproject.kterising.utils.ChatUtil;
import org.bukkit.OfflinePlayer;

public final class PlaceholderUtil {

    private PlaceholderUtil() {}

    public static String processPlaceholder(OfflinePlayer player, String placeholder) {
        if (player == null) return null;

        String ph = placeholder.toLowerCase();
        PlayerStats stats = StatsCache.get(player.getUniqueId());
        MatchSession session = KteRising.getMatch();

        return switch (ph) {
            case "kill" -> stats != null ? String.valueOf(stats.kills) : "0";
            case "death" -> stats != null ? String.valueOf(stats.deaths) : "0";
            case "win" -> stats != null ? String.valueOf(stats.wins) : "0";
            case "game" -> stats != null ? String.valueOf(stats.gamesPlayed) : "0";
            case "time" -> {
                int total = session != null ? session.getSeconds() : 0;
                yield (total / 60) + ":" + String.format("%02d", total % 60);
            }
            case "lava" -> session != null ? Integer.toString(session.getLavaY()) : "0";
            case "live" -> session != null ? Integer.toString(session.getLives()) : "0";
            case "lavarising" -> String.valueOf(session != null && session.isLavaRising());
            case "pvp" -> (session != null && session.isPvpAllowed())
                    ? ChatUtil.getText("placeholderapi.pvp-enabled")
                    : ChatUtil.getText("placeholderapi.pvp-disabled");
            case "mode" -> session != null ? session.getPlaceholderLabel() : "";
            case "match" -> String.valueOf(session != null && session.isMatch());
            default -> null;
        };
    }
}
