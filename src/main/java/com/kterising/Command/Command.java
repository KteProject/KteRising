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
        if(sender.hasPermission("kterising.risingcommand.start") && sender.hasPermission("kterising.risingcommand.mode") && sender.hasPermission("rootrising.risingcommand.reload")) {


        if(args.length == 0) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', MessagesConfig.get().getString("message.command-title")));
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', ""));
            if(sender.hasPermission("kterising.risingcommand.start")) {sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6  /kterising start &7- &fStart the Game"));}
            if(sender.hasPermission("kterising.risingcommand.mode")) {sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6  /kterising mode &7- &fChange the mode."));}
            if(sender.hasPermission("kterising.risingcommand.reload")) {sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6  /kterising reload &7- &fReload the Plugin."));}
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', ""));
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', MessagesConfig.get().getString("message.command-title")));
            return true;
        }

        String subCommand = args[0].toLowerCase();

        if(subCommand.equals("start")) {
            if(!sender.hasPermission("kterising.risingcommand.start")) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', MessagesConfig.get().getString("message.dont-permission")));
                return true;
            }
            if(StartGame.match) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', MessagesConfig.get().getString("messages.already-started")));
                return true;
            }

            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', MessagesConfig.get().getString("message.command-game-started")));
            StartGame.start(plugin);
        }
        if(subCommand.equals("reload")) {
            MessagesConfig.reload();
            plugin.reloadConfig();
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', MessagesConfig.get().getString("message.plugin-reload")));
        }
        if(subCommand.equals("mode")) {
            if(!sender.hasPermission("kterising.risingcommand.mode")) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', MessagesConfig.get().getString("message.dont-permission")));
                return true;
            }
            if(args.length == 1) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', MessagesConfig.get().getString("message.command-title")));
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "                 &6Mods"));
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', ""));
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6  Classic   &7- &fPlay the game with mining."));
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6  OP        &7- &fPlay the game without mining."));
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6  Elytra    &7- &fPlay in Classic mode with elytra given."));
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6  ElytraOP  &7- &fPlay in OP mode with elytra given."));
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6  Trident   &7- &fPlay in Classic mode with Trident given."));
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6  TridentOP &7- &fPlay in OP mode with Trident given."));
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', ""));
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', MessagesConfig.get().getString("message.command-title")));
                return true;
            }
            if(args[1].equals("classic") || args[1].equals("Classic") || args[1].equals("CLASSIC")) {
                StartGame.mode = "Classic";
                for(Player player : Bukkit.getOnlinePlayers()) {
                    String title = ChatColor.translateAlternateColorCodes('&', MessagesConfig.get().getString("title.mode-refresh.title").replace("%mode%",StartGame.mode));
                    String subtitle = ChatColor.translateAlternateColorCodes('&', MessagesConfig.get().getString("title.mode-refresh.sub").replace("%mode%",StartGame.mode));
                    player.sendTitle(title, subtitle);
                }

                return true;
            }  else if(args[1].equals("op") || args[1].equals("Op") || args[1].equals("OP"))  {
                StartGame.mode = "OP";
                for(Player player : Bukkit.getOnlinePlayers()) {
                    String title = ChatColor.translateAlternateColorCodes('&', MessagesConfig.get().getString("title.mode-refresh.title").replace("%mode%",StartGame.mode));
                    String subtitle = ChatColor.translateAlternateColorCodes('&', MessagesConfig.get().getString("title.mode-refresh.sub").replace("%mode%",StartGame.mode));
                    player.sendTitle(title,subtitle);
                }
                return true;
            } else if(args[1].equals("elytraop") || args[1].equals("Elytraop") || args[1].equals("ElytraOp") || args[1].equals("ELYTRAOP")) {
                StartGame.mode = "ElytraOP";
                for(Player player : Bukkit.getOnlinePlayers()) {
                    String title = ChatColor.translateAlternateColorCodes('&', MessagesConfig.get().getString("title.mode-refresh.title").replace("%mode%",StartGame.mode));
                    String subtitle = ChatColor.translateAlternateColorCodes('&', MessagesConfig.get().getString("title.mode-refresh.sub").replace("%mode%",StartGame.mode));
                    player.sendTitle(title,subtitle);
                }
                return true;
            } else if(args[1].equals("elytra") || args[1].equals("Elytra") || args[1].equals("ELYTRA")) {
                StartGame.mode = "Elytra";
                for(Player player : Bukkit.getOnlinePlayers()) {
                    String title = ChatColor.translateAlternateColorCodes('&', MessagesConfig.get().getString("title.mode-refresh.title").replace("%mode%",StartGame.mode));
                    String subtitle = ChatColor.translateAlternateColorCodes('&', MessagesConfig.get().getString("title.mode-refresh.sub").replace("%mode%",StartGame.mode));
                    player.sendTitle(title,subtitle);

                }
                return true;
            } else if(args[1].equals("trident") || args[1].equals("Trident")|| args[1].equals("TRIDENT")) {
                StartGame.mode = "Trident";
                for(Player player : Bukkit.getOnlinePlayers()) {
                    String title = ChatColor.translateAlternateColorCodes('&', MessagesConfig.get().getString("title.mode-refresh.title").replace("%mode%",StartGame.mode));
                    String subtitle = ChatColor.translateAlternateColorCodes('&', MessagesConfig.get().getString("title.mode-refresh.sub").replace("%mode%",StartGame.mode));
                    player.sendTitle(title,subtitle);
                }
                return true;
            } else if(args[1].equals("tridentop") || args[1].equals("Tridentop") || args[1].equals("TridentOp") || args[1].equals("TRIDENTOP")) {
                StartGame.mode = "TridentOP";
                for(Player player : Bukkit.getOnlinePlayers()) {
                    String title = ChatColor.translateAlternateColorCodes('&', MessagesConfig.get().getString("title.mode-refresh.title").replace("%mode%",StartGame.mode));
                    String subtitle = ChatColor.translateAlternateColorCodes('&', MessagesConfig.get().getString("title.mode-refresh.sub").replace("%mode%",StartGame.mode));
                    player.sendTitle(title,subtitle);
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
