package com.kterising.Functions;

import com.kterising.KteRising;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.Objects;

public class CustomDeathMessage implements Listener {
    private final KteRising plugin;

    public CustomDeathMessage(KteRising plugin) {
        this.plugin = plugin;
    }
    @EventHandler
    private void onPlayerDeath(PlayerDeathEvent e) {
        if(!plugin.getConfig().getBoolean("custom-death-messages.enabled")) return;
        if(plugin.getConfig().getString("custom-death-messages.havent-killer") == null) return;
        if(plugin.getConfig().getString("custom-death-messages.have-killer") == null) return;
        String deathMessage = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(plugin.getConfig().getString("custom-death-messages.havent-killer")).replace("%player%",e.getEntity().getDisplayName()));
        if (e.getEntity().getKiller() != null) {
            deathMessage = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(plugin.getConfig().getString("custom-death-messages.have-killer")).replace("%player%",e.getEntity().getDisplayName()).replace("%killer%",e.getEntity().getKiller().getDisplayName() ));
        }
        e.setDeathMessage(deathMessage);
    }
}
