package com.kterising.Listeners;

import com.kterising.Functions.MessagesConfig;
import com.kterising.Functions.SpecialItems;
import com.kterising.Functions.StartGame;
import com.kterising.KteRising;
import com.kterising.Team.TeamGUI;
import com.kterising.Team.TeamManager;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;
import java.util.UUID;

import static com.kterising.Functions.StartGame.centerX;
import static com.kterising.Functions.StartGame.centerZ;

public class PlayerJoin implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        if (StartGame.match) {
            if(!StartGame.lavarising) {
                if (StartGame.leavedPlayers.containsKey(uuid)) {
                    Location returnLoc = StartGame.leavedPlayers.get(uuid);
                    player.setGameMode(GameMode.SURVIVAL);
                    player.teleport(returnLoc);

                    ItemStack[] savedInv = StartGame.leavedInventories.get(uuid);
                    if (savedInv != null) {
                        player.getInventory().setContents(savedInv);
                        StartGame.leavedInventories.remove(uuid);
                    }

                    StartGame.leavedPlayers.remove(uuid);

                    String title = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("title.rejoined.title")));
                    String subtitle = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("title.rejoined.sub")));
                    player.sendTitle(title, subtitle, 10, 60, 10);
                } else {
                    Location specLoc = new Location(player.getWorld(), centerX, KteRising.getInstance().getConfig().getInt("teleport-y"), centerZ);
                    player.teleport(specLoc);
                    player.getInventory().clear();
                    player.setGameMode(GameMode.SPECTATOR);

                    String title = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("title.eliminated.title")));
                    String subtitle = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("title.eliminated.sub")));
                    player.sendTitle(title, subtitle, 10, 60, 10);
                }
            } else {
                Location specLoc = new Location(player.getWorld(), centerX, KteRising.getInstance().getConfig().getInt("teleport-y"), centerZ);
                player.teleport(specLoc);
                player.getInventory().clear();
                player.setGameMode(GameMode.SPECTATOR);

                String title = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("title.eliminated.title")));
                String subtitle = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("title.eliminated.sub")));
                player.sendTitle(title, subtitle, 10, 60, 10);
            }

        } else {
            Location spawnLoc = new Location(player.getWorld(), centerX, KteRising.getInstance().getConfig().getInt("teleport-y"), centerZ);
            player.teleport(spawnLoc);
            player.setGameMode(GameMode.ADVENTURE);

            TeamManager.assignPlayerToTeam(player);
            TeamGUI.updateTeamMenuForAll();

            player.getInventory().clear();
            SpecialItems.giveSpecialItems(player);
        }
    }

}
