package com.kterising.Listeners;

import com.kterising.Functions.MessagesConfig;
import com.kterising.Functions.StartGame;
import com.kterising.KteRising;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
public class PlayerJoin implements Listener {

    private final KteRising plugin;
    public PlayerJoin(KteRising plugin) {
        this.plugin = plugin;
    }


    @EventHandler
    public void playerjoin(PlayerJoinEvent event) {

        Player player = event.getPlayer();

        if(StartGame.match) {
            player.teleport(new Location(player.getWorld(), 0, 160, 0));
            player.setGameMode(GameMode.SPECTATOR);
                String title = ChatColor.translateAlternateColorCodes('&', MessagesConfig.get().getString("title.eliminated.title"));
                String subtitle = ChatColor.translateAlternateColorCodes('&', MessagesConfig.get().getString("title.eliminated.sub"));
                player.sendTitle(title, subtitle);

        } else {
            player.teleport(new Location(player.getWorld(), 0, 160, 0));
            player.setGameMode(GameMode.ADVENTURE);

        }
    }
}
