package com.kterising.Command;

import com.kterising.Functions.MessagesConfig;
import com.kterising.Functions.StartGame;
import com.kterising.KteRising;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Command implements CommandExecutor {
    private final KteRising plugin;


    public Command(KteRising plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String s, String[] args) {
        if (sender.hasPermission("kterising.risingcommand.start") && sender.hasPermission("kterising.risingcommand.mode") && sender.hasPermission("kterising.risingcommand.reload") && sender.hasPermission("kterising.risingcommand.skip") && sender.hasPermission("kterising.risingcommand.freeze")) {


            if (args.length == 0) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', MessagesConfig.get().getString("message.command-title")));
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', ""));
                if (sender.hasPermission("kterising.risingcommand.start")) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', MessagesConfig.get().getString("message.command-start")));
                }
                if (sender.hasPermission("kterising.risingcommand.mode")) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', MessagesConfig.get().getString("message.command-mode")));
                }
                if (sender.hasPermission("kterising.risingcommand.reload")) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', MessagesConfig.get().getString("message.command-reload")));
                }
                if (sender.hasPermission("kterising.risingcommand.skip")) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', MessagesConfig.get().getString("message.command-skip")));
                }
                if (sender.hasPermission("kterising.risingcommand.freeze")) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', MessagesConfig.get().getString("message.command-freeze")));
                }
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', ""));
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', MessagesConfig.get().getString("message.command-title")));
                return true;
            }

            String subCommand = args[0].toLowerCase();

            if (subCommand.equals("start")) {
                if (!sender.hasPermission("kterising.risingcommand.start")) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', MessagesConfig.get().getString("message.dont-permission")));
                    return true;
                }
                if (StartGame.match) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', MessagesConfig.get().getString("message.already-started")));
                    return true;
                }

                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', MessagesConfig.get().getString("message.command-game-started")));
                StartGame.start(plugin);
            }
            if (subCommand.equals("skip")) {
                if (!sender.hasPermission("kterising.risingcommand.skip")) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', MessagesConfig.get().getString("message.dont-permission")));
                    return true;
                }

                StartGame.skipTime();
                for (Player player : Bukkit.getOnlinePlayers()) {
                    String title = ChatColor.translateAlternateColorCodes('&', MessagesConfig.get().getString("title.skip.title"));
                    String subtitle = ChatColor.translateAlternateColorCodes('&', MessagesConfig.get().getString("title.skip.sub"));
                    player.sendTitle(title, subtitle);

                }
                return true;
            }
                if (subCommand.equals("freeze")) {
                    if (sender.hasPermission("kterising.risingcommand.freeze")) {
                        if (StartGame.match == true) {

                            if (StartGame.lavarising) {
                                StartGame.lavarising = false;
                                for (Player player : Bukkit.getOnlinePlayers()) {
                                    String title = ChatColor.translateAlternateColorCodes('&', MessagesConfig.get().getString("title.freeze.title"));
                                    String subtitle = ChatColor.translateAlternateColorCodes('&', MessagesConfig.get().getString("title.freeze.sub"));
                                    player.sendTitle(title, subtitle);
                                }
                                return true;
                            } else {
                                StartGame.lavarising = true;
                                for (Player player : Bukkit.getOnlinePlayers()) {
                                    String title = ChatColor.translateAlternateColorCodes('&', MessagesConfig.get().getString("title.freeze.title"));
                                    String subtitle = ChatColor.translateAlternateColorCodes('&', MessagesConfig.get().getString("title.freeze-started.sub"));
                                    player.sendTitle(title, subtitle);
                                }
                                return true;
                            }
                        } else {
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', MessagesConfig.get().getString("message.not-started")));
                        }
                    }
                }
                if (subCommand.equals("reload")) {
                    if(!sender.hasPermission("kterising.risingcommand.reload")) {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', MessagesConfig.get().getString("message.dont-permission")));
                        return true;
                    }
                    MessagesConfig.reload();
                    plugin.reloadConfig();
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', MessagesConfig.get().getString("message.plugin-reload")));
                }
                if (subCommand.equals("mode")) {
                    if(!sender.hasPermission("kterising.risingcommand.mode")) {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', MessagesConfig.get().getString("message.dont-permission")));
                        return true;
                    }
                    if (args.length == 1) {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', MessagesConfig.get().getString("message.command-title")));
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', MessagesConfig.get().getString("message.command-mod")));
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', ""));
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', MessagesConfig.get().getString("message.command-classic")));
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', MessagesConfig.get().getString("message.command-op")));
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', MessagesConfig.get().getString("message.command-elytra")));
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', MessagesConfig.get().getString("message.command-elytraop")));
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', MessagesConfig.get().getString("message.command-trident")));
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', MessagesConfig.get().getString("message.command-tridentop")));
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', ""));
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', MessagesConfig.get().getString("message.command-title")));
                        return true;
                    }
                    if (args[1].equals("classic") || args[1].equals("Classic") || args[1].equals("CLASSIC")) {
                        StartGame.mode = "Classic";
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            String title = ChatColor.translateAlternateColorCodes('&', MessagesConfig.get().getString("title.mode-refresh.title").replace("%mode%", StartGame.mode));
                            String subtitle = ChatColor.translateAlternateColorCodes('&', MessagesConfig.get().getString("title.mode-refresh.sub").replace("%mode%", StartGame.mode));
                            player.sendTitle(title, subtitle);
                        }

                        return true;
                    } else if (args[1].equals("op") || args[1].equals("Op") || args[1].equals("OP")) {
                        StartGame.mode = "OP";
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            String title = ChatColor.translateAlternateColorCodes('&', MessagesConfig.get().getString("title.mode-refresh.title").replace("%mode%", StartGame.mode));
                            String subtitle = ChatColor.translateAlternateColorCodes('&', MessagesConfig.get().getString("title.mode-refresh.sub").replace("%mode%", StartGame.mode));
                            player.sendTitle(title, subtitle);
                        }
                        return true;
                    } else if (args[1].equals("elytraop") || args[1].equals("Elytraop") || args[1].equals("ElytraOp") || args[1].equals("ELYTRAOP")) {
                        StartGame.mode = "ElytraOP";
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            String title = ChatColor.translateAlternateColorCodes('&', MessagesConfig.get().getString("title.mode-refresh.title").replace("%mode%", StartGame.mode));
                            String subtitle = ChatColor.translateAlternateColorCodes('&', MessagesConfig.get().getString("title.mode-refresh.sub").replace("%mode%", StartGame.mode));
                            player.sendTitle(title, subtitle);
                        }
                        return true;
                    } else if (args[1].equals("elytra") || args[1].equals("Elytra") || args[1].equals("ELYTRA")) {
                        StartGame.mode = "Elytra";
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            String title = ChatColor.translateAlternateColorCodes('&', MessagesConfig.get().getString("title.mode-refresh.title").replace("%mode%", StartGame.mode));
                            String subtitle = ChatColor.translateAlternateColorCodes('&', MessagesConfig.get().getString("title.mode-refresh.sub").replace("%mode%", StartGame.mode));
                            player.sendTitle(title, subtitle);

                        }
                        return true;
                    } else if (args[1].equals("trident") || args[1].equals("Trident") || args[1].equals("TRIDENT")) {
                        StartGame.mode = "Trident";
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            String title = ChatColor.translateAlternateColorCodes('&', MessagesConfig.get().getString("title.mode-refresh.title").replace("%mode%", StartGame.mode));
                            String subtitle = ChatColor.translateAlternateColorCodes('&', MessagesConfig.get().getString("title.mode-refresh.sub").replace("%mode%", StartGame.mode));
                            player.sendTitle(title, subtitle);
                        }
                        return true;
                    } else if (args[1].equals("tridentop") || args[1].equals("Tridentop") || args[1].equals("TridentOp") || args[1].equals("TRIDENTOP")) {
                        StartGame.mode = "TridentOP";
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            String title = ChatColor.translateAlternateColorCodes('&', MessagesConfig.get().getString("title.mode-refresh.title").replace("%mode%", StartGame.mode));
                            String subtitle = ChatColor.translateAlternateColorCodes('&', MessagesConfig.get().getString("title.mode-refresh.sub").replace("%mode%", StartGame.mode));
                            player.sendTitle(title, subtitle);
                        }
                        return true;
                    }
                }
            } else {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', MessagesConfig.get().getString("message.dont-permission")));
                return true;
            }
            return true;
        }
    }