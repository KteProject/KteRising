package com.kteproject.kterising.listeners;

import com.kteproject.kterising.KteRising;
import com.kteproject.kterising.game.AutoStart;
import com.kteproject.kterising.game.MatchSession;
import com.kteproject.kterising.managers.LobbyItems;
import com.kteproject.kterising.managers.RewardsManager;
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

    private static final PotionEffect NIGHT_VISION =
            new PotionEffect(PotionEffectType.NIGHT_VISION, PotionEffect.INFINITE_DURATION,
                    0, false, false, false);

    private int maxHeight() {
        return KteRising.getConfiguration().getInt("world-configurations.world-height", 180);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        p.addPotionEffect(NIGHT_VISION);

        Location spawn = KteRising.getInstance().getSpawnLocation();
        if (spawn != null && spawn.getWorld() != null) {
            spawn.getWorld().getChunkAtAsync(spawn).thenAccept(chunk -> p.teleportAsync(spawn));
        }

        if (KteRising.getInstance().isReady()) {
            StatsManager.load(p);
        }

        MatchSession session = KteRising.getMatch();
        if (session != null && session.isMatch()) {
            if (!session.isLavaRising()) {
                p.setGameMode(GameMode.SURVIVAL);
                ChatUtil.sendTitle(p, "titles.rejoin.title", "titles.rejoin.subtitle", 5, 200, 5, Map.of());
                if (!session.getJoinedBefore().contains(p.getUniqueId())) {
                    p.getInventory().clear();
                    session.giveItems(p);
                }
            } else {
                p.setGameMode(GameMode.SPECTATOR);
            }
            return;
        }

        if (KteRising.isVotingMenuEnabled()) {
            KteRising.getInstance().getServer().getGlobalRegionScheduler().runDelayed(
                    KteRising.getInstance(),
                    (ScheduledTask task) -> {
                        if (p.isOnline()) {
                            LobbyItems.giveLobbyItem(p);
                        }
                    },
                    5L
            );
        }

        int need = KteRising.getConfiguration().getInt("autostart-configuration.need-player-count");
        if (Bukkit.getOnlinePlayers().size() >= need) {
            AutoStart.startCountdown();
        } else {
            AutoStart.stopCountdown();
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent e) {
        MatchSession session = KteRising.getMatch();
        if (session == null || !session.isMatch()) {
            int need = KteRising.getConfiguration().getInt("autostart-configuration.need-player-count");
            if (Bukkit.getOnlinePlayers().size() - 1 >= need) {
                AutoStart.startCountdown();
            } else {
                AutoStart.stopCountdown();
            }
        }

        Player p = e.getPlayer();
        StatsManager.unload(p);

        if (session == null || !session.isMatch()) return;

        if (p.getGameMode() == GameMode.SURVIVAL && !session.isLavaRising()) {
            session.getJoinedBefore().add(p.getUniqueId());
        }

        session.checkLive();

        if (!session.isLavaRising()) return;
        p.getInventory().clear();
        if (p.getHealth() > 0) {
            p.setHealth(0.0);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDeath(PlayerDeathEvent e) {
        MatchSession session = KteRising.getMatch();
        if (session == null || !session.isMatch()) return;

        Player p = e.getEntity();
        Player killer = p.getKiller();
        p.setGameMode(GameMode.SPECTATOR);
        p.addPotionEffect(NIGHT_VISION);
        RewardsManager.deathPlayer(p);
        StatsCache.recordDeath(p.getUniqueId());

        if (killer != null) {
            RewardsManager.killPlayer(killer);
            StatsCache.recordKill(killer.getUniqueId());
        }

        session.checkLive();
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onDamage(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player)) return;
        if (e instanceof EntityDamageByEntityEvent) return;

        MatchSession session = KteRising.getMatch();
        if (session == null || !session.isMatch()) {
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onDamageByEntity(EntityDamageByEntityEvent e) {
        if (!(e.getEntity() instanceof Player)) return;

        MatchSession session = KteRising.getMatch();
        if (session == null || !session.isMatch()) {
            e.setCancelled(true);
            return;
        }

        if (session.isPvpAllowed()) return;

        Object damager = e.getDamager();
        if (damager instanceof Player
                || damager instanceof Projectile proj && proj.getShooter() instanceof Player) {
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent e) {
        Player p = e.getPlayer();
        MatchSession session = KteRising.getMatch();

        if (session == null || !session.isMatch()) {
            if (!p.isOp()) e.setCancelled(true);
            return;
        }

        if (e.getBlockPlaced().getY() >= maxHeight()) {
            e.setCancelled(true);
            ChatUtil.sendActionBar(p, "action-bar.max-height");
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBucket(PlayerBucketEmptyEvent e) {
        MatchSession session = KteRising.getMatch();
        if (session == null || !session.isMatch()) {
            e.setCancelled(true);
            return;
        }

        int y = e.getBlockClicked().getRelative(e.getBlockFace()).getY();
        if (y >= maxHeight()) {
            e.setCancelled(true);
            ChatUtil.sendActionBar(e.getPlayer(), "action-bar.max-height");
        }
    }
}
