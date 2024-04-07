package com.kterising.Command;

import com.kterising.Functions.ItemsConfig;
import com.kterising.Functions.MessagesConfig;
import com.kterising.Functions.StartGame;
import com.kterising.KteRising;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class Command implements CommandExecutor {
    private final KteRising plugin;


    public Command(KteRising plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.@NotNull Command command, @NotNull String s, String[] args) {
        if (sender.hasPermission("kterising.start") && sender.hasPermission("kterising.mode") && sender.hasPermission("kterising.reload") && sender.hasPermission("kterising.skip") && sender.hasPermission("kterising.risingcommand.freeze")) {


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
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', ""));
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("message.command-title"))));
                return true;
            }

            String subCommand = args[0].toLowerCase();

            if (subCommand.equals("start")) {
                if (!sender.hasPermission("kterising.start")) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("message.dont-permission"))));
                    return true;
                }
                if (StartGame.match) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("message.already-started"))));
                    return true;
                }

                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("message.command-game-started"))));
                StartGame.start(plugin);
            }
            if (subCommand.equals("skip")) {
                if (!sender.hasPermission("kterising.skip")) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("message.dont-permission"))));
                    return true;
                }

                StartGame.skipTime();
                for (Player player : Bukkit.getOnlinePlayers()) {
                    String title = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("title.skip.title")));
                    String subtitle = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("title.skip.sub")));
                    player.sendTitle(title, subtitle);

                }
                return true;
            }
                if (subCommand.equals("freeze")) {
                    if (sender.hasPermission("kterising.freeze")) {
                        if (StartGame.match) {

                            if (StartGame.lavarising) {
                                StartGame.lavarising = false;
                                for (Player player : Bukkit.getOnlinePlayers()) {
                                    String title = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("title.freeze.title")));
                                    String subtitle = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("title.freeze.sub")));
                                    player.getWorld().playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.0f);
                                    player.sendTitle(title, subtitle);
                                }
                            } else {
                                StartGame.lavarising = true;
                                for (Player player : Bukkit.getOnlinePlayers()) {
                                    String title = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("title.freeze.title")));
                                    String subtitle = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("title.freeze-started.sub")));
                                    player.getWorld().playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.0f);
                                    player.sendTitle(title, subtitle);
                                }
                            }
                            return true;
                        } else {
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("message.not-started"))));
                        }
                    }
                }
                if (subCommand.equals("reload")) {
                    if(!sender.hasPermission("kterising.reload")) {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("message.dont-permission"))));
                        return true;
                    }
                    MessagesConfig.reload();
                    plugin.reloadConfig();
                    ItemsConfig.reload();
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("message.plugin-reload"))));
                }
                if (subCommand.equals("mode")) {
                    if(!sender.hasPermission("kterising.mode")) {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("message.dont-permission"))));
                        return true;
                    }
                    if (args.length == 1) {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("message.command-title"))));
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("message.command-mod"))));
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', ""));
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("message.command-classic"))));
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("message.command-op"))));
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
                            StartGame.mode = "OP";
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
                            StartGame.mode = "TridentOP";
                            for (Player player : Bukkit.getOnlinePlayers()) {
                                String title = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("title.mode-refresh.title")).replace("%mode%", StartGame.mode));
                                String subtitle = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("title.mode-refresh.sub")).replace("%mode%", StartGame.mode));
                                player.sendTitle(title, subtitle);
                            }
                            return true;
                    }
                }
            } else {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("message.dont-permission"))));
                return true;
            }
            return true;
        }
    }