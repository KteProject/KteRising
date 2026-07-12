package com.kteproject.kterising.game;

import com.kteproject.kterising.KteRising;
import com.kteproject.kterising.utils.ChatUtil;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;

public final class AutoStart {

    private static ScheduledTask countdownTask;
    private static boolean countdownStarted;
    private static int countdownSeconds;

    private AutoStart() {}

    public static void startCountdown() {
        if (countdownStarted) return;

        MatchSession session = KteRising.getMatch();
        if (session != null && session.isMatch()) return;

        int neededPlayers = KteRising.getConfiguration().getInt("autostart-configuration.need-player-count");
        if (Bukkit.getOnlinePlayers().size() < neededPlayers) return;

        countdownStarted = true;
        countdownSeconds = KteRising.getConfiguration().getInt("autostart-configuration.autostart-countdown");

        countdownTask = KteRising.getInstance().getServer().getGlobalRegionScheduler().runAtFixedRate(
                KteRising.getInstance(),
                (ScheduledTask task) -> {
                    int online = Bukkit.getOnlinePlayers().size();
                    if (online < neededPlayers) {
                        stopCountdown();
                        return;
                    }

                    countdownSeconds--;

                    for (Player p : Bukkit.getOnlinePlayers()) {
                        ChatUtil.sendActionBar(
                                p,
                                "action-bar.autostart-countdown",
                                Map.of("seconds", String.valueOf(countdownSeconds))
                        );
                    }

                    if (countdownSeconds <= 0) {
                        stopCountdown();
                        MatchSession match = KteRising.getMatch();
                        if (match != null) {
                            match.startGame();
                        }
                    }
                },
                1,
                20
        );
    }

    public static void stopCountdown() {
        if (countdownTask != null) {
            countdownTask.cancel();
            countdownTask = null;
        }
        countdownStarted = false;
    }
}
