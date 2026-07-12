package com.kteproject.kterising.managers;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;

import java.util.EnumSet;
import java.util.Set;

public final class SafeBiomeManager {

    private static final Set<Biome> BAD_BIOMES = EnumSet.of(
            Biome.OCEAN,
            Biome.WARM_OCEAN,
            Biome.LUKEWARM_OCEAN,
            Biome.COLD_OCEAN,
            Biome.FROZEN_OCEAN,
            Biome.DEEP_OCEAN,
            Biome.DEEP_LUKEWARM_OCEAN,
            Biome.DEEP_COLD_OCEAN,
            Biome.DEEP_FROZEN_OCEAN,
            Biome.SNOWY_BEACH
    );

    private SafeBiomeManager() {}

    private static boolean isSafeSpot(World world, int x, int z) {
        int y = world.getHighestBlockYAt(x, z);
        Material base = world.getBlockAt(x, y, z).getType();
        Material above1 = world.getBlockAt(x, y + 1, z).getType();
        Material above2 = world.getBlockAt(x, y + 2, z).getType();
        return !base.isAir() && above1.isAir() && above2.isAir();
    }

    public static Location findSafeLocation(World world, int startX, int startZ) {
        if (world == null) {
            world = Bukkit.getWorlds().get(0);
            Bukkit.getLogger().warning("[SafeBiome] World was null. Using default world.");
        }

        int x = startX;
        int z = startZ;
        int dx = 1;
        int dz = 0;
        int segmentLength = 1;
        int stepsInSegment = 0;
        int turnCounter = 0;

        for (int i = 0; i < 3000; i++) {
            int surfaceY = world.getHighestBlockYAt(x, z);
            Biome biome = world.getBiome(x, surfaceY, z);

            if (!BAD_BIOMES.contains(biome) && isSafeSpot(world, x, z)) {
                Location safe = new Location(world, x + 0.5, surfaceY + 1, z + 0.5);
                Bukkit.getLogger().info(
                        "[SafeBiome] Safe biome found at X:" + x +
                                " Y:" + (surfaceY + 1) +
                                " Z:" + z +
                                " | Biome: " + biome
                );
                return safe;
            }

            x += dx;
            z += dz;
            stepsInSegment++;

            if (stepsInSegment == segmentLength) {
                stepsInSegment = 0;
                int tmp = dx;
                dx = -dz;
                dz = tmp;
                turnCounter++;
                if (turnCounter % 2 == 0) {
                    segmentLength++;
                }
            }
        }

        int fallbackY = world.getHighestBlockYAt(x, z);
        Bukkit.getLogger().warning("[SafeBiome] No safe biome found. Using fallback spawn.");
        return new Location(world, x + 0.5, fallbackY + 1, z + 0.5);
    }
}
