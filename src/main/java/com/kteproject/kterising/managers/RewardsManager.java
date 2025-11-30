package com.kteproject.kterising.managers;
import com.kteproject.kterising.KteRising;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import java.util.List;

public class RewardsManager {

    private static final String ROOT = "rewards-configuration.";

    public static void killPlayer(Player player) {
        executeReward(player, "kill-player");
    }

    public static void winPlayer(Player player) {
        executeReward(player, "win-player");
    }

    public static void deathPlayer(Player player) {
        executeReward(player, "death-player");
    }

    private static void executeReward(Player player, String path) {
        FileConfiguration config = KteRising.getConfiguration();

        if (!config.getBoolean(ROOT + "enabled"))
            return;

        List<String> commands = config.getStringList(ROOT + path);
        if (commands.isEmpty())
            return;

        Bukkit.getAsyncScheduler().runNow(KteRising.getInstance(), task -> {

            List<String> prepared = commands.stream()
                    .map(cmd -> cmd.replace("<player>", player.getName()))
                    .toList();

            Bukkit.getScheduler().runTask(
                    KteRising.getInstance(),
                    () -> prepared.forEach(cmd ->
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd)
                    )
            );

        });
    }
}
