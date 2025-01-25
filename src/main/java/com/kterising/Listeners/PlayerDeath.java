package com.kterising.Listeners;

import com.kterising.Functions.MessagesConfig;
import com.kterising.Functions.PlayerStats;
import com.kterising.Functions.StartGame;
import com.kterising.KteRising;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.Objects;

public class PlayerDeath implements Listener {
    private final KteRising plugin;
    public PlayerDeath(KteRising plugin) {
        this.plugin = plugin;
    }
    @EventHandler
    public void Death(PlayerDeathEvent event) {
        event.getEntity().setGameMode(GameMode.SPECTATOR);
        String title = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("title.player-died.title")));
        String subtitle = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("title.player-died.sub")));
        event.getEntity().sendTitle(title,subtitle);
        StartGame.live(plugin);
        PlayerStats.addDeath(event.getEntity());
        if (event.getEntity().getKiller() != null) {
            PlayerStats.addKill(event.getEntity().getKiller());
        }
    }
}
