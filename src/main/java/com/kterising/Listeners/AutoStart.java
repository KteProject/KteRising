package com.kterising.Listeners;

import com.kterising.Functions.MessagesConfig;
import com.kterising.Functions.ModVoteGUI;
import com.kterising.Functions.SpecialItems;
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

import java.util.concurrent.atomic.AtomicInteger;

public class AutoStart implements Listener {
    private final KteRising plugin;
    private static BukkitRunnable autostartTask;
    private static final AtomicInteger players = new AtomicInteger(0);
    private static final AtomicInteger time = new AtomicInteger(0);

    public AutoStart(KteRising plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    private void join(PlayerJoinEvent event) {
        updatePlayerCount();

        if (!StartGame.match) {
            if (plugin.getConfig().getBoolean("player-start")) {
                if (players.get() >= plugin.getConfig().getInt("player-start-count")) {
                    if (autostartTask == null) {
                        startCountdown();
                    }
                }
            }
        }
    }

    @EventHandler
    private void leave(PlayerQuitEvent event) {
        updatePlayerCount();

        if (!StartGame.match) {
            if (plugin.getConfig().getBoolean("player-start")) {
                if (players.get() <= plugin.getConfig().getInt("player-start-count")) {
                    stopCountdown();
                }
            }
        }
    }

    private void updatePlayerCount() {
        players.set(Bukkit.getOnlinePlayers().size());
    }

    private void startCountdown() {
        autostartTask = new BukkitRunnable() {
            @Override
            public void run() {
                int remainingTime = time.decrementAndGet();
                if (remainingTime <= 0) {
                    if (plugin.getConfig().getBoolean("vote-start")) {
                        ModVoteGUI.selectWinningVotes();
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            ModVoteGUI.closeModVoteMenu(player);
                            SpecialItems.removeSpecialItems(player);
                        }
                    }
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            StartGame.start(plugin);
                        }
                    }.runTaskLater(plugin, 20L);
                    cancel();
                }

                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.translateAlternateColorCodes('&', MessagesConfig.get().getString("start-time") + remainingTime + " " + MessagesConfig.get().getString("start-second"))));
                }
            }
        };
        autostartTask.runTaskTimer(plugin, 20L, 20L);
        time.set(plugin.getConfig().getInt("player-start-time"));
    }

    private void stopCountdown() {
        if (autostartTask != null) {
            autostartTask.cancel();
            autostartTask = null;
        }
        time.set(plugin.getConfig().getInt("player-start-time"));
    }
}