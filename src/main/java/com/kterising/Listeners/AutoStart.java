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
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.atomic.AtomicInteger;

public class AutoStart implements Listener {
    private final KteRising plugin;
    public static BukkitRunnable autostartTask;
    private static final AtomicInteger players = new AtomicInteger(0);
    private static final AtomicInteger time = new AtomicInteger(0);

    public AutoStart(KteRising plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    private void join(PlayerJoinEvent event) {
        updatePlayerCount();

        if (!StartGame.match && plugin.getConfig().getBoolean("player-start")) {
            if (players.get() >= plugin.getConfig().getInt("player-start-count")) {
                if (autostartTask == null) {
                    startCountdown(plugin);
                }
            }
        }
    }

    @EventHandler
    private void leave(PlayerQuitEvent event) {
        updatePlayerCount();

        if (!StartGame.match && plugin.getConfig().getBoolean("player-start")) {
            if (players.get() < plugin.getConfig().getInt("player-start-count")) {
                stopCountdown();
            }
        }
    }

    private void updatePlayerCount() {
        players.set(Bukkit.getOnlinePlayers().size());
    }

    public static void startCountdown(Plugin plugin) {
        time.set(plugin.getConfig().getInt("player-start-time"));

        autostartTask = new BukkitRunnable() {
            @Override
            public void run() {
                int remainingTime = time.decrementAndGet();

                if (remainingTime <= 0) {
                    cancel();
                    autostartTask = null;

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

                    return;
                }

                for (Player player : Bukkit.getOnlinePlayers()) {
                    String message = ChatColor.translateAlternateColorCodes('&',
                            MessagesConfig.get().getString("start-time") + remainingTime + " " +
                                    MessagesConfig.get().getString("start-second"));
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));
                }
            }
        };

        autostartTask.runTaskTimer(plugin, 20L, 20L);
    }

    public static void stopCountdown() {
        if (autostartTask != null) {
            autostartTask.cancel();
            autostartTask = null;
        }
        time.set(KteRising.getInstance().getConfig().getInt("player-start-time"));
    }
}
