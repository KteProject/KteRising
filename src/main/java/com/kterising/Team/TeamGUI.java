package com.kterising.Team;

import com.kterising.Functions.MessagesConfig;
import com.kterising.Functions.StartGame;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TeamGUI implements Listener {

    public static void startTeamInfoTask(JavaPlugin plugin) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!StartGame.match) return;

                for (Player player : Bukkit.getOnlinePlayers()) {
                    Team team = TeamManager.getPlayerTeam(player);
                    if (team != null && team.getPlayers().size() > 1) {
                        Player closestTeammate = null;
                        double closestDistance = Double.MAX_VALUE;

                        for (Player teammate : team.getPlayers()) {
                            if (teammate.equals(player) || !teammate.isOnline() || teammate.isDead()) continue;

                            double distance = player.getLocation().distance(teammate.getLocation());
                            if (distance < closestDistance) {
                                closestDistance = distance;
                                closestTeammate = teammate;
                            }
                        }

                        if (closestTeammate != null) {
                            String direction = getDirection(player, closestTeammate);
                            String message = ChatColor.translateAlternateColorCodes('&',
                                            Objects.requireNonNull(MessagesConfig.get().getString("messages.closest-teammate")))
                                    .replace("%player%", closestTeammate.getName())
                                    .replace("%distance%", String.valueOf((int) closestDistance))
                                    .replace("%direction%", direction);
                            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));
                        } else {
                            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(
                                    ChatColor.translateAlternateColorCodes('&',
                                            Objects.requireNonNull(MessagesConfig.get().getString("messages.no-team-or-dead")))));
                        }
                    } else {
                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(
                                ChatColor.translateAlternateColorCodes('&',
                                        Objects.requireNonNull(MessagesConfig.get().getString("messages.no-team")))));
                    }
                }
            }
        }.runTaskTimer(plugin, 0, 20);
    }

    private static String getDirection(Player player, Player target) {
        double dx = target.getLocation().getX() - player.getLocation().getX();
        double dz = target.getLocation().getZ() - player.getLocation().getZ();

        float yaw = player.getLocation().getYaw();

        double angle = Math.toDegrees(Math.atan2(dz, dx)) - yaw - 90;

        if (angle < 0) {
            angle += 360;
        }

        if (angle >= 0 && angle < 45 || angle >= 315 && angle < 360) {
            return "⬆";
        } else if (angle >= 45 && angle < 135) {
            return "➡";
        } else if (angle >= 135 && angle < 225) {
            return "⬇";
        } else if (angle >= 225 && angle < 315) {
            return "⬅";
        } else {
            return "⬆";
        }
    }

    public static void openTeamMenu(Player player) {
        List<Team> teams = TeamManager.getTeams();
        if (teams == null) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("messages.team-load-failed"))));
            return;
        }

        Inventory inv = Bukkit.createInventory(null, 54, ChatColor.translateAlternateColorCodes('&',
                Objects.requireNonNull(MessagesConfig.get().getString("messages.team-gui"))));

        int slot = 0;
        for (Team team : teams) {
            ItemStack item;
            if (team.getPlayers().contains(player)) {
                item = new ItemStack(Material.GREEN_WOOL);
                ItemMeta meta = item.getItemMeta();
                if (meta != null) {
                    meta.addEnchant(Enchantment.ARROW_DAMAGE, 1, true);
                    meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                    meta.setDisplayName(ChatColor.GOLD + team.getDisplayName());
                    List<String> lore = new ArrayList<>();
                    lore.add(ChatColor.YELLOW + team.getPlayersNames());
                    meta.setLore(lore);
                    item.setItemMeta(meta);
                }
            } else {
                item = new ItemStack(Material.WHITE_WOOL);
                ItemMeta meta = item.getItemMeta();
                if (meta != null) {
                    meta.setDisplayName(ChatColor.GOLD + team.getDisplayName());
                    List<String> lore = new ArrayList<>();
                    lore.add(ChatColor.YELLOW + team.getPlayersNames());
                    meta.setLore(lore);
                    item.setItemMeta(meta);
                }
            }
            inv.setItem(slot++, item);
        }

        player.openInventory(inv);
    }

    public static void updateTeamMenuForAll() {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (onlinePlayer.getOpenInventory().getTitle().equals(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("messages.team-gui"))))) {
                openTeamMenu(onlinePlayer);
            }
        }
    }

    @EventHandler
    public void handleInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || !clickedItem.hasItemMeta()) {
            return;
        }

        String displayName = ChatColor.stripColor(Objects.requireNonNull(clickedItem.getItemMeta()).getDisplayName());

        for (Team team : TeamManager.getTeams()) {
            if (ChatColor.stripColor(team.getDisplayName()).equals(displayName)) {

                if (team.getPlayers().size() < team.getMaxSize()) {
                    TeamManager.removePlayerFromTeam(player);
                    team.addPlayer(player);

                    clickedItem.setType(Material.GREEN_WOOL);
                    ItemMeta meta = clickedItem.getItemMeta();
                    if (meta != null) {
                        meta.setDisplayName(ChatColor.GREEN + team.getDisplayName());
                        List<String> lore = new ArrayList<>();
                        lore.add(ChatColor.YELLOW + team.getPlayersNames());
                        meta.setLore(lore);
                        meta.addEnchant(Enchantment.ARROW_DAMAGE, 1, true);
                        clickedItem.setItemMeta(meta);
                    }

                    player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                    Objects.requireNonNull(MessagesConfig.get().getString("messages.team-joined")))
                            .replace("%team%", team.getName()));
                    player.closeInventory();

                    updateTeamMenuForAll();
                } else {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                            Objects.requireNonNull(MessagesConfig.get().getString("messages.team-full"))));
                    player.closeInventory();

                    updateTeamMenuForAll();
                }
                return;
            }
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            Player damaged = (Player) event.getEntity();
            Player damager = (Player) event.getDamager();

            if (TeamManager.getTeams().stream().anyMatch(team -> team.getPlayers().contains(damaged) && team.getPlayers().contains(damager))) {
                event.setCancelled(true);
            }
        }

        if (event.getEntity() instanceof Player && event.getDamager() instanceof org.bukkit.entity.Projectile) {
            org.bukkit.entity.Projectile projectile = (org.bukkit.entity.Projectile) event.getDamager();
            if (projectile.getShooter() instanceof Player) {
                Player shooter = (Player) projectile.getShooter();
                Player damaged = (Player) event.getEntity();

                if (TeamManager.getTeams().stream().anyMatch(team -> team.getPlayers().contains(damaged) && team.getPlayers().contains(shooter))) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        TeamManager.removePlayerFromTeam(event.getPlayer());
    }
}
