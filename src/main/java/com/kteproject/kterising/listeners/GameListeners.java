package com.kteproject.kterising.listeners;
import com.kteproject.kterising.KteRising;
import com.kteproject.kterising.game.AutoStart;
import com.kteproject.kterising.game.Game;
import com.kteproject.kterising.managers.LobbyItems;
import com.kteproject.kterising.managers.RewardsManager;
import com.kteproject.kterising.stats.PlayerStats;
import com.kteproject.kterising.stats.StatsCache;
import com.kteproject.kterising.stats.StatsManager;
import com.kteproject.kterising.utils.ChatUtil;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Map;

public class GameListeners implements Listener {

    private static final int MAX_HEIGHT =
            KteRising.getConfiguration().getInt("world-configurations.world-height", 180);

    private static final PotionEffect NIGHT_VISION =
            new PotionEffect(PotionEffectType.NIGHT_VISION, PotionEffect.INFINITE_DURATION,
                    0, false, false, false);

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        p.addPotionEffect(NIGHT_VISION);
        Location spawn = KteRising.getSpawnLocation();
        spawn.getWorld().getChunkAtAsync(spawn).thenAccept(chunk -> {
            p.teleportAsync(spawn);
        });
        StatsManager.load(p);
        if (Game.match) {
            if (!Game.lavarising) {
                p.setGameMode(GameMode.SURVIVAL);
                ChatUtil.sendTitle(
                        p,
                        "titles.rejoin.title",
                        "titles.rejoin.subtitle",
                        5, 200, 5,
                        Map.of()
                );

                if (!Game.joinedBefore.contains(p.getUniqueId())) {
                    p.getInventory().clear();

                    Game.giveItems(p);
                }

            } else {
                p.setGameMode(GameMode.SPECTATOR);
            }
        } else {
            if(KteRising.isVotingMenuEnabled() && !Game.match) {
                KteRising.getInstance().getServer().getGlobalRegionScheduler().runDelayed(
                        KteRising.getInstance(),
                        (ScheduledTask task) -> LobbyItems.giveLobbyItem(p.getPlayer()),
                        5L
                );
            }
            if (Bukkit.getOnlinePlayers().size() >= KteRising.getConfiguration().getInt("autostart-configuration.need-player-count")) {
                AutoStart.startCountdown();
            } else {
                AutoStart.stopCountdown();
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent e) {
        if (!Game.match){
            if (Bukkit.getOnlinePlayers().size() >= KteRising.getConfiguration().getInt("autostart-configuration.need-player-count")) {
                AutoStart.startCountdown();
            } else {
                AutoStart.stopCountdown();
            }
        }
        Player p = e.getPlayer();
        StatsManager.unload(p);
        if (Game.match) {
            Game.checkLive();

            if(p.getGameMode() == GameMode.SURVIVAL && !Game.lavarising){
                Game.joinedBefore.add(p.getUniqueId());
            }

            if (!Game.lavarising) return;
            p.getInventory().clear();
            p.setHealth(0.0);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDeath(PlayerDeathEvent e) {
        if (!Game.match) return;
        Player p = e.getEntity();
        Player killer = p.getKiller();
        p.setGameMode(GameMode.SPECTATOR);
        p.addPotionEffect(NIGHT_VISION);
        RewardsManager.deathPlayer(p);
        PlayerStats ps = StatsCache.get(p.getUniqueId());
        if (ps != null) ps.deaths++;
        if (killer != null) {
            RewardsManager.killPlayer(killer);
            PlayerStats ks = StatsCache.get(killer.getUniqueId());
            if (ks != null) ks.kills++;
        }
        Game.checkLive();
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onDamage(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player)) return;
        if (!Game.pvp) {
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onDamageByEntity(EntityDamageByEntityEvent e) {
        if (!(e.getEntity() instanceof Player)) return;
        if (Game.pvp) return;
        Object damager = e.getDamager();
        if (damager instanceof Player
                || damager instanceof Projectile proj && proj.getShooter() instanceof Player) {
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent e) {
        Player p = e.getPlayer();

        if (!Game.match) {
            if (!p.isOp()) e.setCancelled(true);
            return;
        }

        if (e.getBlockPlaced().getY() >= MAX_HEIGHT) {
            e.setCancelled(true);
            ChatUtil.sendActionBar(p, "action-bar.max-height");
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBucket(PlayerBucketEmptyEvent e) {
        if (!Game.match) {
            e.setCancelled(true);
            return;
        }

        int y = e.getBlockClicked().getRelative(e.getBlockFace()).getY();

        if (y >= MAX_HEIGHT) {
            e.setCancelled(true);
            ChatUtil.sendActionBar(e.getPlayer(), "action-bar.max-height");
        }
    }
}
