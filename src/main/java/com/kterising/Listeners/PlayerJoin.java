package com.kterising.Listeners;

import com.kterising.Functions.MessagesConfig;
import com.kterising.Functions.SpecialItems;
import com.kterising.Functions.StartGame;
import com.kterising.Team.TeamGUI;
import com.kterising.Team.TeamManager;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Objects;

import static com.kterising.Functions.StartGame.centerX;
import static com.kterising.Functions.StartGame.centerZ;

public class PlayerJoin implements Listener {

    @EventHandler
    public void playerjoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (StartGame.match) {
            if(StartGame.leavedPlayers.get(player.getUniqueId()) != null) {
                player.setGameMode(GameMode.SURVIVAL);
                player.teleport(StartGame.leavedPlayers.get(player.getUniqueId()));
                String title = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("title.rejoined.title")));
                String subtitle = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("title.rejoined.sub")));
                player.sendTitle(title, subtitle);
            } else {
                player.teleport(new Location(player.getWorld(), centerX, 160, centerZ));
                player.setGameMode(GameMode.SPECTATOR);
                String title = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("title.eliminated.title")));
                String subtitle = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("title.eliminated.sub")));
                player.sendTitle(title, subtitle);
            }
        } else {
            for (int y = 60; y < 100; y++) {
                Location loc = new Location(player.getWorld(), centerX, y, centerZ);
                if (player.getWorld().getBlockAt(loc).getType() == Material.AIR) {
                    player.teleport(loc);
                    break;
                }
            }
            player.setGameMode(GameMode.ADVENTURE);
            TeamManager.assignPlayerToTeam(event.getPlayer());
            TeamGUI.updateTeamMenuForAll();
            player.getInventory().clear();
            SpecialItems.giveSpecialItems(event.getPlayer());
        }
    }
}