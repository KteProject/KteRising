package com.kteproject.kterising.game;

import com.kteproject.kterising.KteRising;
import com.kteproject.kterising.utils.ChatUtil;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;

public class LavaTask {

    private static final BlockData LAVA_DATA = Bukkit.createBlockData(Material.LAVA);
    private static List<Chunk> CACHED_CHUNKS;

    public static void cacheChunks() {
        CACHED_CHUNKS = new ArrayList<>(256);
        World w = Game.world;

        int minCx = Game.minX >> 4;
        int maxCx = Game.maxX >> 4;
        int minCz = Game.minZ >> 4;
        int maxCz = Game.maxZ >> 4;

        for (int cx = minCx; cx <= maxCx; cx++) {
            for (int cz = minCz; cz <= maxCz; cz++) {
                if (w.isChunkLoaded(cx, cz)) {
                    CACHED_CHUNKS.add(w.getChunkAt(cx, cz));
                }
            }
        }
    }

    public static void startTime(int startSecond) {
        KteRising plugin = KteRising.getInstance();
        Game.seconds = startSecond;

        plugin.getServer().getGlobalRegionScheduler().runAtFixedRate(plugin, task -> {
            Game.checkLive();
            checkSpectators();
            if(Game.end) {task.cancel();return;}

            if (Game.time) {
                Game.seconds++;
            } else {
                Game.seconds--;
                if (Game.seconds <= 0) {
                    Game.time = true;
                    Game.lavarising = true;

                    cacheChunks();
                    startLava();

                    for (Player player : Bukkit.getOnlinePlayers()) {
                        ChatUtil.sendTitle(player,
                                "titles.lavarising.title",
                                "titles.lavarising.subtitle",
                                5, 40, 5,
                                Map.of());
                    }
                }
            }
        }, 1L, 20L);
    }

    public static void startLava() {
        KteRising plugin = KteRising.getInstance();

        int delay = plugin.getConfig().getInt("game-configurations.lava-delay");
        int maxHeight = plugin.getConfig().getInt("game-configurations.lava-finish-height");

        Queue<List<Block>> fillQueue = new ArrayDeque<>();

        plugin.getServer().getGlobalRegionScheduler().runAtFixedRate(plugin, task -> {

            if(Game.end) {task.cancel();return;}

            if (Game.lava >= maxHeight) {
                task.cancel();
                return;
            }

            checkLavaHeight();
            int targetY = Game.lava;

            for (Chunk chunk : CACHED_CHUNKS) {
                int baseX = chunk.getX() * 16;
                int baseZ = chunk.getZ() * 16;

                int sx = Math.max(Game.minX, baseX);
                int ex = Math.min(Game.maxX, baseX + 15);

                int sz = Math.max(Game.minZ, baseZ);
                int ez = Math.min(Game.maxZ, baseZ + 15);

                List<Block> pipeline = new ArrayList<>(40);

                for (int x = sx; x <= ex; x++) {
                    for (int z = sz; z <= ez; z++) {
                        Block b = Game.world.getBlockAt(x, targetY, z);
                        Material t = b.getType();

                        if (t != Material.LAVA &&
                                (t == Material.AIR ||
                                        t == Material.WATER ||
                                        t == Material.CAVE_AIR ||
                                        t == Material.VOID_AIR)) {
                            pipeline.add(b);
                        }
                    }
                }

                if (!pipeline.isEmpty()) {
                    fillQueue.add(pipeline);
                }
            }

            plugin.getServer().getGlobalRegionScheduler().runAtFixedRate(plugin, t2 -> {
                List<Block> next = fillQueue.poll();
                if (next == null) {
                    t2.cancel();
                    return;
                }

                for (Block b : next) {
                    b.setBlockData(LAVA_DATA, false);
                }
            }, 1L, 1L);

            if (!Game.lavaFrozen) {
                Game.lava++;
            }

        }, 1L, 20L * delay);
    }

    public static void checkLavaHeight() {
        int pvpHeight = KteRising.getConfiguration()
                .getInt("game-configurations.pvp-allow-height");

        if (Game.lava == pvpHeight) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                ChatUtil.sendTitle(
                        p,
                        "titles.pvp-allow.title",
                        "titles.pvp-allow.subtitle",
                        5, 40, 5,
                        Map.of()
                );
            }
            Game.pvp = true;

        } else if (Game.lava ==
                KteRising.getConfiguration().getInt("game-configurations.lava-finish-height")-1) {

            for (Player player : Bukkit.getOnlinePlayers()) {
                ChatUtil.sendTitle(
                        player,
                        "titles.worldborder-shrink.title",
                        "titles.worldborder-shrink.subtitle",
                        5, 40, 5,
                        Map.of()
                );
            }

            Game.world.getWorldBorder().setSize(2.0, 120L);
        }
    }

    private static void checkSpectators() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.getGameMode() != GameMode.SPECTATOR) continue;

            if (!p.getWorld().getWorldBorder().isInside(p.getLocation())) {
                p.teleportAsync(KteRising.getSpawnLocation());
            }
        }
    }
}
