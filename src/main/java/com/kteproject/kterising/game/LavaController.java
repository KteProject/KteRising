package com.kteproject.kterising.game;

import com.kteproject.kterising.KteRising;
import com.kteproject.kterising.utils.ChatUtil;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public class LavaController {

    private static final BlockData LAVA_DATA = Bukkit.createBlockData(Material.LAVA);

    private final KteRising plugin;
    private final MatchSession session;

    private List<Chunk> cachedChunks;
    private final Queue<List<Block>> fillQueue = new ArrayDeque<>();

    private ScheduledTask graceTask;
    private ScheduledTask lavaTask;
    private ScheduledTask fillTask;
    private ScheduledTask borderTask;
    private ScheduledTask deathmatchTask;

    private int borderCountdown;
    private int deathmatchRemaining;
    private boolean deathmatchActive;

    public LavaController(KteRising plugin, MatchSession session) {
        this.plugin = plugin;
        this.session = session;
    }

    public int getDeathmatchRemaining() {
        return deathmatchRemaining;
    }

    public boolean isDeathmatchActive() {
        return deathmatchActive;
    }

    public void resetDeathmatch() {
        deathmatchRemaining = plugin.getConfig().getInt("game-configurations.deathmatch-duration");
        deathmatchActive = false;
    }

    public void cancelAll() {
        cancel(graceTask);
        cancel(lavaTask);
        cancel(fillTask);
        cancel(borderTask);
        cancel(deathmatchTask);
        graceTask = null;
        lavaTask = null;
        fillTask = null;
        borderTask = null;
        deathmatchTask = null;
        fillQueue.clear();
        cachedChunks = null;
    }

    private void cancel(ScheduledTask task) {
        if (task != null) {
            task.cancel();
        }
    }

    public void startGraceCountdown(int startSecond) {
        cancel(graceTask);
        session.setSeconds(startSecond);
        session.setCountingUp(false);

        graceTask = plugin.getServer().getGlobalRegionScheduler().runAtFixedRate(plugin, task -> {
            if (session.getPhase() == MatchPhase.ENDED) {
                task.cancel();
                graceTask = null;
                return;
            }

            session.checkLive();
            checkSpectators();

            if (session.isCountingUp()) {
                session.incrementSeconds();
                return;
            }

            session.decrementSeconds();
            if (session.getSeconds() > 0) {
                return;
            }

            session.setCountingUp(true);
            session.transitionTo(MatchPhase.LAVA);
            cacheChunks();
            startLava();

            session.broadcastTitle(
                    "titles.lavarising.title",
                    "titles.lavarising.subtitle",
                    5, 40, 5,
                    Map.of()
            );
        }, 1L, 20L);
    }

    private void cacheChunks() {
        cachedChunks = new ArrayList<>(256);
        World w = session.getWorld();
        if (w == null) return;

        int minCx = session.getMinX() >> 4;
        int maxCx = session.getMaxX() >> 4;
        int minCz = session.getMinZ() >> 4;
        int maxCz = session.getMaxZ() >> 4;

        for (int cx = minCx; cx <= maxCx; cx++) {
            for (int cz = minCz; cz <= maxCz; cz++) {
                cachedChunks.add(w.getChunkAt(cx, cz));
            }
        }
    }

    private void startLava() {
        cancel(lavaTask);
        cancel(fillTask);
        fillQueue.clear();

        int delay = Math.max(1, plugin.getConfig().getInt("game-configurations.lava-delay"));
        int maxHeight = plugin.getConfig().getInt("game-configurations.lava-finish-height");

        ensureFillTask();

        lavaTask = plugin.getServer().getGlobalRegionScheduler().runAtFixedRate(plugin, task -> {
            if (session.getPhase() == MatchPhase.ENDED) {
                task.cancel();
                lavaTask = null;
                return;
            }

            if (session.getLavaY() >= maxHeight) {
                killBelowThreshold();
                task.cancel();
                lavaTask = null;
                return;
            }

            checkLavaHeight();
            enqueueLayer(session.getLavaY());

            if (!session.isLavaFrozen()) {
                session.incrementLavaY();
            }
        }, 1L, 20L * delay);
    }

    private void ensureFillTask() {
        if (fillTask != null) return;

        fillTask = plugin.getServer().getGlobalRegionScheduler().runAtFixedRate(plugin, task -> {
            if (session.getPhase() == MatchPhase.ENDED) {
                task.cancel();
                fillTask = null;
                fillQueue.clear();
                return;
            }

            List<Block> next = fillQueue.poll();
            if (next == null) {
                return;
            }

            for (Block b : next) {
                b.setBlockData(LAVA_DATA, true);
            }
        }, 1L, 1L);
    }

    private void enqueueLayer(int targetY) {
        if (cachedChunks == null || cachedChunks.isEmpty()) {
            cacheChunks();
        }
        if (cachedChunks == null) return;

        World world = session.getWorld();
        if (world == null) return;

        for (Chunk chunk : cachedChunks) {
            if (!chunk.isLoaded()) {
                chunk.load(true);
            }

            int baseX = chunk.getX() * 16;
            int baseZ = chunk.getZ() * 16;

            int sx = Math.max(session.getMinX(), baseX);
            int ex = Math.min(session.getMaxX(), baseX + 15);
            int sz = Math.max(session.getMinZ(), baseZ);
            int ez = Math.min(session.getMaxZ(), baseZ + 15);

            List<Block> pipeline = new ArrayList<>(64);

            for (int x = sx; x <= ex; x++) {
                for (int z = sz; z <= ez; z++) {
                    Block b = world.getBlockAt(x, targetY, z);
                    Material t = b.getType();
                    if (t == Material.AIR || t == Material.WATER || t == Material.CAVE_AIR || t == Material.VOID_AIR) {
                        pipeline.add(b);
                    }
                }
            }

            if (!pipeline.isEmpty()) {
                fillQueue.add(pipeline);
            }
        }
    }

    private void checkLavaHeight() {
        int pvpHeight = plugin.getConfig().getInt("game-configurations.pvp-allow-height");

        if (session.getLavaY() == pvpHeight && !session.isPvpAllowed()) {
            session.setPvpAllowed(true);
            session.broadcastTitle(
                    "titles.pvp-allow.title",
                    "titles.pvp-allow.subtitle",
                    5, 40, 5,
                    Map.of()
            );
        }

        if (deathmatchActive) return;

        int finish = plugin.getConfig().getInt("game-configurations.lava-finish-height");
        if (session.getLavaY() != finish - 1) return;

        session.broadcastTitle(
                "titles.worldborder-shrink.title",
                "titles.worldborder-shrink.subtitle",
                5, 40, 5,
                Map.of()
        );

        World world = session.getWorld();
        if (world != null) {
            world.getWorldBorder().setSize(
                    plugin.getConfig().getDouble("world-configurations.area-shrinkage"),
                    plugin.getConfig().getInt("world-configurations.area-delay")
            );
        }

        startBorderCountdown();
    }

    private void startBorderCountdown() {
        deathmatchActive = true;
        if (!plugin.getConfig().getBoolean("game-configurations.deathmatch-enabled")) {
            return;
        }

        cancel(borderTask);
        borderCountdown = plugin.getConfig().getInt("world-configurations.area-delay");

        borderTask = plugin.getServer().getGlobalRegionScheduler().runAtFixedRate(plugin, task -> {
            if (session.getPhase() == MatchPhase.ENDED) {
                task.cancel();
                borderTask = null;
                return;
            }

            if (borderCountdown <= 0) {
                task.cancel();
                borderTask = null;
                startDeathmatch();
                session.broadcastTitle(
                        "titles.deathmatch.title",
                        "titles.deathmatch.subtitle",
                        5, 100, 5,
                        Map.of()
                );
                return;
            }

            borderCountdown--;
        }, 1L, 20L);
    }

    private void startDeathmatch() {
        session.transitionTo(MatchPhase.DEATHMATCH);
        cancel(deathmatchTask);
        deathmatchRemaining = plugin.getConfig().getInt("game-configurations.deathmatch-duration");

        deathmatchTask = plugin.getServer().getGlobalRegionScheduler().runAtFixedRate(plugin, task -> {
            if (session.getPhase() == MatchPhase.ENDED || !session.isMatch()) {
                task.cancel();
                deathmatchTask = null;
                return;
            }

            if (deathmatchRemaining == 30) {
                session.broadcastTitle(
                        "titles.deathmatch-30s.title",
                        "titles.deathmatch-30s.subtitle",
                        5, 100, 5,
                        Map.of()
                );
            }

            if (deathmatchRemaining <= 0) {
                task.cancel();
                deathmatchTask = null;
                session.checkWin();
                return;
            }

            deathmatchRemaining--;
        }, 1L, 20L);
    }

    private void checkSpectators() {
        Location loc = plugin.getSpawnLocation();
        if (loc == null) return;

        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.getGameMode() != GameMode.SPECTATOR) continue;
            if (!p.getWorld().getWorldBorder().isInside(p.getLocation())) {
                Location dest = loc.clone();
                dest.setY(p.getLocation().getY());
                p.teleportAsync(dest);
            }
        }
    }

    private void killBelowThreshold() {
        int killHeight = plugin.getConfig().getInt("game-configurations.kill-height");
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.getGameMode() != GameMode.SURVIVAL) continue;
            if (p.getLocation().getY() <= killHeight) {
                p.setHealth(0);
            }
        }
    }
}
