package com.kterising.Placeholders;

import com.kterising.Functions.StartGame;
import org.bukkit.OfflinePlayer;

public class PlaceholderUtil {

    public static String processPlaceholder(OfflinePlayer player, String placeholder) {
        switch (placeholder.toLowerCase()) {
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
                return StartGame.PvP ? "Enabled" : "Disabled";
            default:
                return null;
        }
    }
}