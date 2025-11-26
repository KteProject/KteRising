package com.kteproject.kterising.listeners;

import com.kteproject.kterising.KteRising;
import com.kteproject.kterising.game.Game;
import com.kteproject.kterising.managers.RewardsManager;
import com.kteproject.kterising.stats.PlayerStats;
import com.kteproject.kterising.stats.StatsCache;
import com.kteproject.kterising.stats.StatsManager;
import com.kteproject.kterising.utils.ChatUtil;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

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
            p.teleport(spawn);
        });
        p.setGameMode(Game.match ? GameMode.SPECTATOR : GameMode.SURVIVAL);
        StatsManager.load(p);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        StatsManager.unload(p);

        if (!Game.match) return;

        if (p.getGameMode() == GameMode.SURVIVAL) {
            p.setHealth(0.0);
        }

        Game.checkLive();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDeath(PlayerDeathEvent e) {
        if (!Game.match) return;
        Player p = e.getEntity();
        p.setGameMode(GameMode.SPECTATOR);
        p.addPotionEffect(NIGHT_VISION);
        RewardsManager.deathPlayer(p);
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

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent e) {
        Player p = e.getPlayer();
        if (!Game.match && !p.isOp()) {
            e.setCancelled(true);
        }
    }
}
