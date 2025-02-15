package com.kterising.Listeners;

import com.kterising.Functions.PlayerStats;
import com.kterising.Functions.StartGame;
import com.kterising.KteRising;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuit implements Listener {

    private final KteRising plugin;
    public PlayerQuit(KteRising plugin) {
        this.plugin = plugin;
    }
    @EventHandler
    public void PlayerLeave(PlayerQuitEvent event) {
        if(StartGame.match) {
            if(StartGame.lavarising) {
                event.getPlayer().damage(999999999);
                PlayerStats.addDeath(event.getPlayer());
            } else {
                StartGame.leavedPlayers.put(event.getPlayer().getUniqueId(), event.getPlayer().getLocation());
            }
            StartGame.live(plugin);
        }
    }
}
