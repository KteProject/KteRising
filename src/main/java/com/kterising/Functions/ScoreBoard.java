package com.kterising.Functions;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.*;

import java.util.Objects;

public class ScoreBoard {

    public ScoreBoard() {
    }

    public static void scoreboard(Plugin plugin, Player player) {
        if (!plugin.getConfig().getBoolean("scoreboard.scoreboard-enabled")) {
            ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
            assert scoreboardManager != null;
            Scoreboard scoreboard = scoreboardManager.getNewScoreboard();

            player.setScoreboard(scoreboard);
            return;
        }

        String mode = StartGame.mode;
        String survivor = Integer.toString(StartGame.survivor);
        String lava = Integer.toString(StartGame.lava);
        String time = formatTime(StartGame.seconds);
        String pvps = StartGame.PvP ? ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("pvp-enabled"))) :
                ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("pvp-disabled")));

        ScoreboardManager manager = Bukkit.getScoreboardManager();
        assert manager != null;
        Scoreboard scoreboard = manager.getNewScoreboard();
        Objective objective = scoreboard.registerNewObjective("scoreboardtitle", "dummy");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(plugin.getConfig().getString("scoreboard.scoreboard-title"))));

        int scoreValue = plugin.getConfig().getStringList("scoreboard.scoreboard").size();
        for (String scores : plugin.getConfig().getStringList("scoreboard.scoreboard")) {
            String formattedScore = ChatColor.translateAlternateColorCodes('&', scores)
                    .replace("%survivor%", survivor)
                    .replace("%mode%", mode)
                    .replace("%pvp%", pvps)
                    .replace("%lava%", lava)
                    .replace("%time%", time);

            Score score = objective.getScore(formattedScore);
            score.setScore(scoreValue);
            scoreValue--;
        }

        player.setScoreboard(scoreboard);
    }

    private static String formatTime(int totalSeconds) {
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return minutes + ":" + String.format("%02d", seconds);
    }
}