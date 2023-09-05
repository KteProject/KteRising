package com.kterising.Listeners;

import com.kterising.Functions.MessagesConfig;
import com.kterising.Functions.StartGame;
import com.kterising.KteRising;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class AutoStart implements Listener {
    private final KteRising plugin;

    public AutoStart(KteRising plugin) {
        this.plugin = plugin;
    }

    private static BukkitRunnable autostartTask;

    public static int players;
    public static int time;

    @EventHandler
    private void join(PlayerJoinEvent event) {

        players = 0;
        for(Player player : Bukkit.getOnlinePlayers()) {
            players++;

        }

        if(!StartGame.match) {

            if (plugin.getConfig().getBoolean("player-start")) {
                    if (players >= plugin.getConfig().getInt("player-start-count")) {
                        autostartTask.runTaskTimer(plugin, 20L, 20L);
                        time = plugin.getConfig().getInt("player-start-time");
                    }
            }
            autostartTask = new BukkitRunnable() {
                @Override
                public void run() {
                        time--;
                        if(time == 0) {
                            StartGame.start(plugin);
                            cancel();
                        }

                        for (Player player : Bukkit.getOnlinePlayers()) {
                            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.translateAlternateColorCodes('&', MessagesConfig.get().getString("start-time") + time + " " + MessagesConfig.get().getString("start-second"))));
                        }

                    }



                };

        }
    }

    @EventHandler
    private void leave(PlayerQuitEvent event) {
        players = 0;
        for(Player player : Bukkit.getOnlinePlayers()) {
            players++;
        }

        if(!StartGame.match) {
            if (plugin.getConfig().getBoolean("player-start")) {
                    if (players <= plugin.getConfig().getInt("player-start-count")) {
                        autostartTask.cancel();
                        time = plugin.getConfig().getInt("player-start-time");
                    }
            }
        }
    }

}