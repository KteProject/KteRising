package com.kterising.Listeners;

import com.kterising.Functions.MessagesConfig;
import com.kterising.Functions.StartGame;
import com.kterising.KteRising;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerDeath implements Listener {
    private final KteRising plugin;
    public PlayerDeath(KteRising plugin) {
        this.plugin = plugin;
    }
    @EventHandler
    public void Death(PlayerDeathEvent event) {
        event.getEntity().setGameMode(GameMode.SPECTATOR);
        String title = ChatColor.translateAlternateColorCodes('&', MessagesConfig.get().getString("player-died-title"));
        String subtitle = ChatColor.translateAlternateColorCodes('&', MessagesConfig.get().getString("player-died-sub"));
        event.getEntity().sendTitle(title,subtitle);
        StartGame.live(plugin);
    }
}
