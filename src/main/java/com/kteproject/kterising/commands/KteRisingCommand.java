package com.kteproject.kterising.commands;

import com.kteproject.kterising.KteRising;
import com.kteproject.kterising.game.AutoStart;
import com.kteproject.kterising.game.Game;
import com.kteproject.kterising.managers.gamemodes.ModeManager;
import com.kteproject.kterising.stats.PlayerStats;
import com.kteproject.kterising.stats.StatsCache;
import com.kteproject.kterising.utils.ChatUtil;
import com.kteproject.kterising.utils.MessagesConfig;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.BaseCommand;
import dev.triumphteam.cmd.core.annotation.Command;
import dev.triumphteam.cmd.core.annotation.Default;
import dev.triumphteam.cmd.core.annotation.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.Map;

@Command("kterising")
@Permission("kterising.command.use")
public class KteRisingCommand extends BaseCommand {

    @Default
    public void defaultCommand(CommandSender sender) {
        ChatUtil.sendListMessage(sender, "command.help-command");
    }

    @SubCommand("start")
    @Permission("kterising.command.start")
    public void startCommand(CommandSender sender) {
        ChatUtil.sendMessage(sender, "command.start-command");
        AutoStart.stopCountdown();
        Game.startGame();
    }

    @SubCommand("skip")
    @Permission("kterising.command.skip")
    public void skipCommand(Player sender) {
        if (!Game.match || Game.lavarising) {
            ChatUtil.sendMessage(sender, "command.command-error");
            return;
        }

        Game.seconds = 3;

        for (Player player : Bukkit.getOnlinePlayers()) {
            ChatUtil.sendTitle(
                    player,
                    "titles.skip.title",
                    "titles.skip.subtitle",
                    5, 40, 5,
                    Map.of()
            );
        }
    }

    @SubCommand("freeze")
    @Permission("kterising.command.freeze")
    public void freezeCommand(Player sender) {
        if (!Game.match) {
            ChatUtil.sendMessage(sender, "command.command-error");
            return;
        }

        Game.lavaFrozen = !Game.lavaFrozen;

        if (Game.lavaFrozen) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                ChatUtil.sendTitle(
                        sender,
                        "titles.freeze-on.title",
                        "titles.freeze-on.subtitle",
                        5, 40, 5,
                        Map.of()
                );
            }
        } else {
            ChatUtil.sendTitle(
                    sender,
                    "titles.freeze-off.title",
                    "titles.freeze-off.subtitle",
                    5, 40, 5,
                    Map.of()
            );
        }
    }

    @SubCommand("reload")
    @Permission("kterising.command.reload")
    public void reloadCommand(CommandSender sender) {
        MessagesConfig.reload();
        KteRising.getInstance().reloadConfig();
        ModeManager.loadModes();
        ChatUtil.sendMessage(sender, "command.reload-command");
    }

    @SubCommand("mode")
    @Permission("kterising.command.mode")
    public void modeCommand(CommandSender sender, String modeName) {
        ConfigurationSection modesSection =
                KteRising.getConfiguration().getConfigurationSection("modes-configuration");

        if (modesSection != null && modesSection.getKeys(false).stream()
                .map(String::toLowerCase)
                .anyMatch(modeName.trim().toLowerCase()::equals)) {

            ChatUtil.sendMessage(sender, "command.mode-command", Map.of("mode", modeName));
            Game.mode = modeName;

        } else {
            ChatUtil.sendMessage(
                    sender,
                    "command.mode-command-error",
                    Map.of("mode", modeName)
            );
        }
    }

    @SubCommand("vote")
    public void voteCommand(Player player) {
        KteRising.getInstance().getVoteGui().open(player);
    }

    @SubCommand("stats")
    @Permission("kterising.command.stats")
    public void statsCommand(CommandSender sender, Player targetName) {
        Player target = targetName;

        if (target == null || (!target.hasPlayedBefore() && !target.isOnline())) {
            ChatUtil.sendMessage(sender, "stats.not-found");
            return;
        }

        PlayerStats stats = StatsCache.get(target.getUniqueId());

        if (stats == null) {
            ChatUtil.sendMessage(sender, "stats.not-found");
            return;
        }

        ChatUtil.sendMessage(
                sender,
                "stats.header",
                Map.of("player", target.getName())
        );

        ChatUtil.sendMessage(
                sender,
                "stats.kill",
                Map.of("kill", String.valueOf(stats.kills))
        );

        ChatUtil.sendMessage(
                sender,
                "stats.death",
                Map.of("death", String.valueOf(stats.deaths))
        );

        ChatUtil.sendMessage(
                sender,
                "stats.win",
                Map.of("win", String.valueOf(stats.wins))
        );
    }
}
