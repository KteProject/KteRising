package com.kteproject.kterising.placeholders;

import com.kteproject.kterising.stats.PlayerStats;
import com.kteproject.kterising.stats.StatsCache;
import com.kteproject.kterising.game.Game;
import com.kteproject.kterising.utils.ChatUtil;
import org.bukkit.OfflinePlayer;

public class PlaceholderUtil {

    public static String processPlaceholder(OfflinePlayer player, String placeholder) {
        if (player == null) return null;

        String ph = placeholder.toLowerCase();

        PlayerStats stats = StatsCache.get(player.getUniqueId());

        switch (ph) {

            case "win":
                return stats != null ? String.valueOf(stats.wins) : "0";

            case "game":
                return stats != null ? String.valueOf(stats.gamesPlayed) : "0";

            case "time":
                int total = Game.seconds;
                return (total / 60) + ":" + String.format("%02d", total % 60);

            case "lava":
                return Integer.toString(Game.lava);

            case "live":
                return Integer.toString(Game.lives);

            case "lavarising":
                return String.valueOf(Game.lavarising);

            case "pvp":
                return Game.pvp
                        ? ChatUtil.getText("placeholderapi.pvp-enabled")
                        : ChatUtil.getText("placeholderapi.pvp-disabled");

            case "mode":
                return Game.placeholderlabel;

            case "match":
                return String.valueOf(Game.match);

            default:
                return null;
        }
    }
}
