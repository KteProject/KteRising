package com.kterising.Functions;

import com.kterising.Team.TeamGUI;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Objects;

public class SpecialItems implements Listener {

    public static void giveSpecialItems(Player player) {
        ItemStack voteCompass = new ItemStack(Material.COMPASS);
        ItemMeta voteMeta = voteCompass.getItemMeta();
        if (voteMeta != null) {
            voteMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("item.vote-name"))));
            voteCompass.setItemMeta(voteMeta);
        }

        ItemStack teamPaper = new ItemStack(Material.PAPER);
        ItemMeta teamMeta = teamPaper.getItemMeta();
        if (teamMeta != null) {
            teamMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("item.team-name"))));
            teamPaper.setItemMeta(teamMeta);
        }


        player.getInventory().setItem(3, teamPaper);
        player.getInventory().setItem(4, voteCompass);
    }

    public static void removeSpecialItems(Player player) {
        player.getInventory().remove(Material.COMPASS);
        player.getInventory().remove(Material.PAPER);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        if (item != null && item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
            String displayName = item.getItemMeta().getDisplayName();
            if (displayName.equals(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("item.vote-name"))))) {
                ModVoteGUI.openModVoteMenu(event.getPlayer());
                event.setCancelled(true);
            } else if (displayName.equals(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("item.team-name"))))) {
                TeamGUI.openTeamMenu(event.getPlayer());
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        ItemStack item = event.getItemDrop().getItemStack();
        if (item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
            String displayName = item.getItemMeta().getDisplayName();
            String voteName = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("item.vote-name")));
            String teamName = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("item.team-name")));

            if (displayName.equals(voteName) || displayName.equals(teamName)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        ItemStack item = event.getCurrentItem();
        if (item != null && item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
            String displayName = item.getItemMeta().getDisplayName();
            String voteName = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("item.vote-name")));
            String teamName = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("item.team-name")));

            if (displayName.equals(voteName) || displayName.equals(teamName)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            Player player = (Player) event.getDamager();
            ItemStack item = player.getInventory().getItemInMainHand();
            if (item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
                String displayName = item.getItemMeta().getDisplayName();
                if (displayName.equals(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("item.vote-name")))) || displayName.equals(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("item.team-name"))))) {
                    event.setCancelled(true);
                }
            }
        }
    }
}