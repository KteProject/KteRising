package com.kterising.Placeholders;

import com.kterising.Functions.MessagesConfig;
import com.kterising.Functions.PlayerStats;
import com.kterising.Functions.StartGame;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;

import java.util.Objects;

public class PlaceholderUtil {

    public static String processPlaceholder(OfflinePlayer player, String placeholder) {
        switch (placeholder.toLowerCase()) {
            case "match":
                return String.valueOf(StartGame.match);
            case "survivor":
                return Integer.toString(StartGame.survivor);
            case "player":
                return player.getName();
            case "time":
                int totalSeconds = StartGame.seconds;
                int minutes = totalSeconds / 60;
                int seconds = totalSeconds % 60;
                return minutes + ":" + String.format("%02d", seconds);
            case "mode":
                return StartGame.mode;
            case "lava":
                return Integer.toString(StartGame.lava);
            case "pvp":
                return StartGame.PvP ? ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("pvp-enabled"))) : ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("pvp-disabled")));
            case "win":
                return Integer.toString(PlayerStats.getWin(Objects.requireNonNull(player.getPlayer())));
            case "death":
                return Integer.toString(PlayerStats.getDeath(Objects.requireNonNull(player.getPlayer())));
            case "kill":
                return Integer.toString(PlayerStats.getKill(Objects.requireNonNull(player.getPlayer())));
            default:
                return null;
        }
    }
}
