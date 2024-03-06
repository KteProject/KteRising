package com.kterising.Functions;

import com.kterising.KteRising;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.*;

public class ScoreBoard {
    private final KteRising plugin;
    public ScoreBoard(KteRising plugin) {
        this.plugin = plugin;
    }



    public static void scoreboard(Plugin plugin, Player player){
        if(!plugin.getConfig().getBoolean("scoreboard.scoreboard-enabled")) {
            ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
            Scoreboard scoreboard = scoreboardManager.getNewScoreboard();

            player.setScoreboard(scoreboard);
            return;
        }
        String pvps = null;
        int totalSeconds = StartGame.seconds;
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        String time = minutes + ":" + String.format("%02d", seconds);
        String mode = StartGame.mode;
        String survivor = Integer.toString(StartGame.survivor);
        String lava = Integer.toString(StartGame.lava);
        if(StartGame.PvP) {
            pvps = ChatColor.translateAlternateColorCodes('&',MessagesConfig.get().getString("pvp-enabled"));
        } else {
            pvps = ChatColor.translateAlternateColorCodes('&',MessagesConfig.get().getString("pvp-disabled"));
        }

        ScoreboardManager manager = Bukkit.getScoreboardManager();
        Scoreboard scoreboard = manager.getNewScoreboard();
        Objective objective = scoreboard.registerNewObjective("scoreboardtitle", "Kte Project");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName(ChatColor.translateAlternateColorCodes('&',plugin.getConfig().getString("scoreboard.scoreboard-title")));
        int scorevalue = plugin.getConfig().getStringList("scoreboard.scoreboard").size();

        for (String scores : plugin.getConfig().getStringList("scoreboard.scoreboard")) {
            Score score = objective.getScore(ChatColor.translateAlternateColorCodes('&', scores)
                    .replace("%survivor%", survivor)
                    .replace("%mode%", mode)
                    .replace("%pvp%", pvps)
                    .replace("%lava%", lava)
                    .replace("%time%", time));

            score.setScore(scorevalue);
            scorevalue--;
        }
        player.setScoreboard(scoreboard);
    }
}