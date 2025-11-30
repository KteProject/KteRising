package com.kteproject.kterising.managers;
import com.kteproject.kterising.KteRising;
import org.bukkit.*;
import org.bukkit.block.Biome;
import java.util.HashSet;
import java.util.Set;

public class SafeBiomeManager {

    private static Location safeLocation;

    private static final Set<Biome> BAD_BIOMES = new HashSet<>();

    static {
        BAD_BIOMES.add(Biome.OCEAN);
        BAD_BIOMES.add(Biome.WARM_OCEAN);
        BAD_BIOMES.add(Biome.LUKEWARM_OCEAN);
        BAD_BIOMES.add(Biome.COLD_OCEAN);
        BAD_BIOMES.add(Biome.FROZEN_OCEAN);
        BAD_BIOMES.add(Biome.DEEP_OCEAN);
        BAD_BIOMES.add(Biome.DEEP_LUKEWARM_OCEAN);
        BAD_BIOMES.add(Biome.DEEP_COLD_OCEAN);
        BAD_BIOMES.add(Biome.DEEP_FROZEN_OCEAN);
        BAD_BIOMES.add(Biome.SNOWY_BEACH);
    }

    private static boolean isBad(Biome biome) {
        return BAD_BIOMES.contains(biome);
    }

    private static boolean isSafeSpot(World world, int x, int z) {

        int y = world.getHighestBlockYAt(x, z);

        Material base = world.getBlockAt(x, y, z).getType();
        Material above1 = world.getBlockAt(x, y + 1, z).getType();
        Material above2 = world.getBlockAt(x, y + 2, z).getType();

        if (base.isAir()) return false;
        if (!above1.isAir()) return false;
        if (!above2.isAir()) return false;

        return true;
    }

    public static void findSafeLocation(int x, int z) {

        World world = Bukkit.getWorld(
                KteRising.getConfiguration().getString("world-configurations.world-name", "world")
        );

        if (world == null) {
            world = Bukkit.getWorlds().get(0);
            Bukkit.getLogger().warning("[SafeBiome] Config world not found. Using default world.");
        }

        int dx = 1;
        int dz = 0;
        int segmentLength = 1;
        int stepsInSegment = 0;
        int turnCounter = 0;

        for (int i = 0; i < 3000; i++) {
            int surfaceY = world.getHighestBlockYAt(x, z);

            Biome biome = world.getBiome(x, surfaceY, z);

            if (!isBad(biome) && isSafeSpot(world, x, z)) {

                safeLocation = new Location(world, x + 0.5, surfaceY + 1, z + 0.5);

                Bukkit.getLogger().info(
                        "[SafeBiome] Safe biome found at X:" + x +
                                " Y:" + (surfaceY + 1) +
                                " Z:" + z +
                                " | Biome: " + biome
                );

                return;
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
                if (turnCounter % 2 == 0) segmentLength++;
            }
        }

        int fallbackY = world.getHighestBlockYAt(x, z);
        safeLocation = new Location(world, x, fallbackY + 1, z);
        Bukkit.getLogger().warning("[SafeBiome] No safe biome found. Using fallback spawn.");
    }

    public static Location getSafeLocation() {
        return safeLocation;
    }
}
