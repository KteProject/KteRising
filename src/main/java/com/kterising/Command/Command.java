package com.kterising.Command;

import com.kterising.Functions.*;
import com.kterising.KteRising;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static com.kterising.Functions.ModVoteGUI.openModVoteMenu;
import static com.kterising.Functions.StartGame.getSoundFromConfig;

public class Command implements CommandExecutor {
    public static boolean selectedmode = false;
    private final KteRising plugin;

    public Command(KteRising plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, org.bukkit.command.@NotNull Command command, @NotNull String s, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("message.command-title"))));
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', ""));
            if (sender.hasPermission("kterising.start")) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("message.command-start"))));
            }
            if (sender.hasPermission("kterising.mode")) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("message.command-mode"))));
            }
            if (sender.hasPermission("kterising.reload")) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("message.command-reload"))));
            }
            if (sender.hasPermission("kterising.skip")) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("message.command-skip"))));
            }
            if (sender.hasPermission("kterising.freeze")) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("message.command-freeze"))));
            }
            if (sender.hasPermission("kterising.vote")) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("message.command-vote"))));
            }
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', ""));
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("message.command-title"))));
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "start":
                if (!sender.hasPermission("kterising.start")) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("message.dont-permission"))));
                    return true;
                }
                if (StartGame.match) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("message.already-started"))));
                    return true;
                }

                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("message.command-game-started"))));

                ModVoteGUI.setVotingEnabled(false);
                ModVoteGUI.selectWinningMod();
                for (Player player : Bukkit.getOnlinePlayers()) {
                    ModVoteGUI.closeModVoteMenu(player);
                }

                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    int countdownSeconds = plugin.getConfig().getInt("start-countdown");
                    for (int i = countdownSeconds; i > 0; i--) {
                        int finalI = i;
                        Bukkit.getScheduler().runTaskLater(plugin, () -> {
                            String countdownMessage = MessagesConfig.get().getString("title.start-time");
                            if (countdownMessage != null) {
                                countdownMessage = countdownMessage.replace("%time%", String.valueOf(finalI));
                                for (Player player : Bukkit.getOnlinePlayers()) {
                                    player.sendTitle("", ChatColor.translateAlternateColorCodes('&', countdownMessage), 0, 20, 0);
                                    player.getWorld().playSound(player.getLocation(), getSoundFromConfig(plugin, "sound.countdown-sound"), 1.0f, 1.0f);
                                }
                            }
                        }, (countdownSeconds - finalI) * 20L);
                    }
                    Bukkit.getScheduler().runTaskLater(plugin, () -> StartGame.start(plugin), countdownSeconds * 20L);
                }, 60L);
                return true;

            case "vote":
                if (!sender.hasPermission("kterising.vote")) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("message.dont-permission"))));
                    return true;
                }
                if (StartGame.match) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("message.already-started"))));
                    return true;
                }
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    openModVoteMenu(player);
                }
                return true;

            case "skip":
                if (!sender.hasPermission("kterising.skip")) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("message.dont-permission"))));
                    return true;
                }

                StartGame.skipTime(plugin);
                for (Player player : Bukkit.getOnlinePlayers()) {
                    String title = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("title.skip.title")));
                    String subtitle = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("title.skip.sub")));
                    player.sendTitle(title, subtitle);
                }
                return true;

            case "freeze":
                if (!sender.hasPermission("kterising.freeze")) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("message.dont-permission"))));
                    return true;
                }
                if (StartGame.match) {
                    if (StartGame.lavarising) {
                        StartGame.lavarising = false;
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            String title = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("title.freeze.title")));
                            String subtitle = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("title.freeze.sub")));
                            player.getWorld().playSound(player.getLocation(), getSoundFromConfig(plugin, "sound.freeze-sound"), 1.0f, 1.0f);
                            player.sendTitle(title, subtitle);
                        }
                    } else {
                        StartGame.lavarising = true;
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            String title = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("title.freeze.title")));
                            String subtitle = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("title.freeze-started.sub")));
                            player.getWorld().playSound(player.getLocation(), getSoundFromConfig(plugin, "sound.freeze-sound"), 1.0f, 1.0f);
                            player.sendTitle(title, subtitle);
                        }
                    }
                } else {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("message.not-started"))));
                }
                return true;

            case "reload":
                if (!sender.hasPermission("kterising.reload")) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("message.dont-permission"))));
                    return true;
                }
                MessagesConfig.reload();
                PlayerStats.reload();
                plugin.reloadConfig();
                ItemsConfig.reload();
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("message.plugin-reload"))));
                return true;

            case "mode":
                if (!sender.hasPermission("kterising.mode")) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("message.dont-permission"))));
                    return true;
                }
                if (args.length == 1) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("message.command-title"))));
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("message.command-mod"))));
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', ""));
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("message.command-classic"))));
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("message.command-op"))));
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("message.command-ultraop"))));
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("message.command-elytra"))));
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("message.command-elytraop"))));
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("message.command-trident"))));
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("message.command-tridentop"))));
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', ""));
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("message.command-title"))));
                    return true;
                }

                    switch (args[1]) {
                        case "classic":
                        case "Classic":
                        case "CLASSIC":
                            selectedmode = true;
                            StartGame.mode = "Classic";
                            for (Player player : Bukkit.getOnlinePlayers()) {
                                String title = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("title.mode-refresh.title")).replace("%mode%", StartGame.mode));
                                String subtitle = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("title.mode-refresh.sub")).replace("%mode%", StartGame.mode));
                                player.sendTitle(title, subtitle);
                            }

                            return true;
                        case "op":
                        case "Op":
                        case "OP":
                            selectedmode = true;
                            StartGame.mode = "OP";
                            for (Player player : Bukkit.getOnlinePlayers()) {
                                String title = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("title.mode-refresh.title")).replace("%mode%", StartGame.mode));
                                String subtitle = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("title.mode-refresh.sub")).replace("%mode%", StartGame.mode));
                                player.sendTitle(title, subtitle);
                            }
                            return true;
                        case "ultraop":
                        case "Ultraop":
                        case "UltraOp":
                        case "ULTRAOP":
                        case "UltraOP":
                            selectedmode = true;
                            StartGame.mode = "UltraOP";
                            for (Player player : Bukkit.getOnlinePlayers()) {
                                String title = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("title.mode-refresh.title")).replace("%mode%", StartGame.mode));
                                String subtitle = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("title.mode-refresh.sub")).replace("%mode%", StartGame.mode));
                                player.sendTitle(title, subtitle);
                            }
                            return true;
                        case "elytraop":
                        case "Elytraop":
                        case "ElytraOp":
                        case "ELYTRAOP":
                        case "ElytraOP":
                            selectedmode = true;
                            StartGame.mode = "ElytraOP";
                            for (Player player : Bukkit.getOnlinePlayers()) {
                                String title = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("title.mode-refresh.title")).replace("%mode%", StartGame.mode));
                                String subtitle = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("title.mode-refresh.sub")).replace("%mode%", StartGame.mode));
                                player.sendTitle(title, subtitle);
                            }
                            return true;
                        case "elytra":
                        case "Elytra":
                        case "ELYTRA":
                            selectedmode = true;
                            StartGame.mode = "Elytra";
                            for (Player player : Bukkit.getOnlinePlayers()) {
                                String title = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("title.mode-refresh.title")).replace("%mode%", StartGame.mode));
                                String subtitle = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("title.mode-refresh.sub")).replace("%mode%", StartGame.mode));
                                player.sendTitle(title, subtitle);

                            }
                            return true;
                        case "trident":
                        case "Trident":
                        case "TRIDENT":
                            selectedmode = true;
                            StartGame.mode = "Trident";
                            for (Player player : Bukkit.getOnlinePlayers()) {
                                String title = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("title.mode-refresh.title")).replace("%mode%", StartGame.mode));
                                String subtitle = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("title.mode-refresh.sub")).replace("%mode%", StartGame.mode));
                                player.sendTitle(title, subtitle);
                            }
                            return true;
                        case "tridentop":
                        case "Tridentop":
                        case "TridentOp":
                        case "TRIDENTOP":
                        case "TridentOP":
                            selectedmode = true;
                            StartGame.mode = "TridentOP";
                            for (Player player : Bukkit.getOnlinePlayers()) {
                                String title = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("title.mode-refresh.title")).replace("%mode%", StartGame.mode));
                                String subtitle = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("title.mode-refresh.sub")).replace("%mode%", StartGame.mode));
                                player.sendTitle(title, subtitle);
                            }
                            return true;
                        default:
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("message.invalid-command"))));
                            return true;
                    }
            default:
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("message.invalid-command"))));
                return true;
        }
    }
}