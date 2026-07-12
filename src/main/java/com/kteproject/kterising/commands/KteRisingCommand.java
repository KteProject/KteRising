package com.kteproject.kterising.commands;

import com.kteproject.kterising.KteRising;
import com.kteproject.kterising.game.AutoStart;
import com.kteproject.kterising.game.MatchSession;
import com.kteproject.kterising.managers.LobbyItems;
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
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.Locale;
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
        MatchSession session = KteRising.getMatch();
        if (session == null) {
            ChatUtil.sendMessage(sender, "command.command-error");
            return;
        }
        ChatUtil.sendMessage(sender, "command.start-command");
        AutoStart.stopCountdown();
        session.startGame();
    }

    @SubCommand("skip")
    @Permission("kterising.command.skip")
    public void skipCommand(Player sender) {
        MatchSession session = KteRising.getMatch();
        if (session == null || !session.isMatch() || session.isLavaRising()) {
            ChatUtil.sendMessage(sender, "command.command-error");
            return;
        }

        session.setSeconds(3);
        session.broadcastTitle("titles.skip.title", "titles.skip.subtitle", 5, 40, 5, Map.of());
    }

    @SubCommand("freeze")
    @Permission("kterising.command.freeze")
    public void freezeCommand(Player sender) {
        MatchSession session = KteRising.getMatch();
        if (session == null || !session.isMatch()) {
            ChatUtil.sendMessage(sender, "command.command-error");
            return;
        }

        boolean frozen = session.toggleLavaFrozen();
        if (frozen) {
            session.broadcastTitle("titles.freeze-on.title", "titles.freeze-on.subtitle", 5, 40, 5, Map.of());
        } else {
            session.broadcastTitle("titles.freeze-off.title", "titles.freeze-off.subtitle", 5, 40, 5, Map.of());
        }
    }

    @SubCommand("reload")
    @Permission("kterising.command.reload")
    public void reloadCommand(CommandSender sender) {
        MessagesConfig.reload();
        KteRising plugin = KteRising.getInstance();
        plugin.reloadConfig();
        ModeManager.loadModes();
        plugin.getVoteManager().reload(plugin.getConfig().getConfigurationSection("modes-configuration"));
        plugin.getVoteGui().reload();
        LobbyItems.invalidateCache();
        plugin.resolveArenaWorld();
        ChatUtil.sendMessage(sender, "command.reload-command");
    }

    @SubCommand("mode")
    @Permission("kterising.command.mode")
    public void modeCommand(CommandSender sender, String modeName) {
        MatchSession session = KteRising.getMatch();
        ConfigurationSection modesSection =
                KteRising.getConfiguration().getConfigurationSection("modes-configuration");

        String cleaned = modeName.trim().toLowerCase(Locale.ROOT);
        boolean valid = modesSection != null && modesSection.getKeys(false).stream()
                .map(s -> s.toLowerCase(Locale.ROOT))
                .anyMatch(cleaned::equals);

        if (valid && session != null) {
            ChatUtil.sendMessage(sender, "command.mode-command", Map.of("mode", modeName));
            session.setMode(modeName);
        } else {
            ChatUtil.sendMessage(sender, "command.mode-command-error", Map.of("mode", modeName));
        }
    }

    @SubCommand("autostart")
    @Permission("kterising.command.autostart")
    public void autostartCommand(CommandSender player, String args) {
        if (args == null || args.isEmpty()) {
            ChatUtil.sendMessage(player, "command.autostart-usage");
            return;
        }

        MatchSession session = KteRising.getMatch();
        if (session != null && session.isMatch()) {
            ChatUtil.sendMessage(player, "command.command-error-2");
            return;
        }

        if (args.equalsIgnoreCase("start")) {
            ChatUtil.sendMessage(player, "command.autostart-start");
            AutoStart.startCountdown();
        } else if (args.equalsIgnoreCase("stop")) {
            ChatUtil.sendMessage(player, "command.autostart-stop");
            AutoStart.stopCountdown();
        } else {
            ChatUtil.sendMessage(player, "command.autostart-usage");
        }
    }

    @SubCommand("vote")
    public void voteCommand(Player player) {
        KteRising.getInstance().getVoteGui().open(player);
    }

    @SubCommand("stats")
    @Permission("kterising.command.stats")
    public void statsCommand(CommandSender sender, Player target) {
        if (target == null) {
            ChatUtil.sendMessage(sender, "stats.not-found");
            return;
        }

        PlayerStats stats = StatsCache.get(target.getUniqueId());
        if (stats == null) {
            ChatUtil.sendMessage(sender, "stats.not-found");
            return;
        }

        ChatUtil.sendMessage(sender, "stats.header", Map.of("player", target.getName()));
        ChatUtil.sendMessage(sender, "stats.kill", Map.of("kill", String.valueOf(stats.kills)));
        ChatUtil.sendMessage(sender, "stats.death", Map.of("death", String.valueOf(stats.deaths)));
        ChatUtil.sendMessage(sender, "stats.win", Map.of("win", String.valueOf(stats.wins)));
    }
}
