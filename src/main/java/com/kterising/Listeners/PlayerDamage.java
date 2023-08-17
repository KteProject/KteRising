package com.kterising.Listeners;

import com.kterising.Functions.StartGame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class PlayerDamage implements Listener {
    @EventHandler
    public void playerDamage(EntityDamageEvent event) {
        if(event.getEntity() instanceof Player) {
            if(!StartGame.PvP) {
                Player player = (Player) event.getEntity();
                event.setCancelled(true);
            }
        }
    }
}
