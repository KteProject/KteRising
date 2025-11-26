package com.kteproject.kterising.listeners;

import com.kteproject.kterising.KteRising;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.Location;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class AutoPickUp implements Listener {

    private final KteRising plugin;

    public AutoPickUp(KteRising plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBreak(BlockBreakEvent e) {

        Player p = e.getPlayer();
        Block b = e.getBlock();
        Material type = b.getType();
        boolean autoSmelt = KteRising.getConfiguration().getBoolean("game-configurations.auto-smelt");
        boolean ore = isOre(type);
        e.setDropItems(false);
        if (autoSmelt) {
            if (!ore) return;
            Material smelted = getSmelted(type);
            int amount = getOreAmount();
            b.getWorld().dropItemNaturally(b.getLocation(), new ItemStack(smelted, amount));
            return;
        }
        if (ore) {
            Material drop = autoSmelt ? getSmelted(type) : getRawOreDrop(type);
            int amount = getOreAmount();
            give(p, new ItemStack(drop, amount));
        }
    }


    private boolean isOre(Material t) {
        return switch (t) {
            case IRON_ORE, DEEPSLATE_IRON_ORE,
                 GOLD_ORE, DEEPSLATE_GOLD_ORE,
                 COPPER_ORE, DEEPSLATE_COPPER_ORE,
                 DIAMOND_ORE, DEEPSLATE_DIAMOND_ORE,
                 EMERALD_ORE, DEEPSLATE_EMERALD_ORE,
                 LAPIS_ORE, DEEPSLATE_LAPIS_ORE,
                 REDSTONE_ORE, DEEPSLATE_REDSTONE_ORE -> true;
            default -> false;
        };
    }

    private Material getSmelted(Material t) {
        return switch (t) {
            case IRON_ORE, DEEPSLATE_IRON_ORE -> Material.IRON_INGOT;
            case GOLD_ORE, DEEPSLATE_GOLD_ORE -> Material.GOLD_INGOT;
            case COPPER_ORE, DEEPSLATE_COPPER_ORE -> Material.COPPER_INGOT;
            case DIAMOND_ORE, DEEPSLATE_DIAMOND_ORE -> Material.DIAMOND;
            case EMERALD_ORE, DEEPSLATE_EMERALD_ORE -> Material.EMERALD;
            case LAPIS_ORE, DEEPSLATE_LAPIS_ORE -> Material.LAPIS_LAZULI;
            case REDSTONE_ORE, DEEPSLATE_REDSTONE_ORE -> Material.REDSTONE;
            default -> Material.AIR;
        };
    }

    private Material getRawOreDrop(Material t) {
        return switch (t) {
            case IRON_ORE, DEEPSLATE_IRON_ORE -> Material.RAW_IRON;
            case GOLD_ORE, DEEPSLATE_GOLD_ORE -> Material.RAW_GOLD;
            case COPPER_ORE, DEEPSLATE_COPPER_ORE -> Material.RAW_COPPER;
            case DIAMOND_ORE, DEEPSLATE_DIAMOND_ORE -> Material.DIAMOND;
            case EMERALD_ORE, DEEPSLATE_EMERALD_ORE -> Material.EMERALD;
            case LAPIS_ORE, DEEPSLATE_LAPIS_ORE -> Material.LAPIS_LAZULI;
            case REDSTONE_ORE, DEEPSLATE_REDSTONE_ORE -> Material.REDSTONE;
            default -> Material.AIR;
        };
    }

    private int getOreAmount() {
        int r = ThreadLocalRandom.current().nextInt(100);
        return (r < 50) ? 1 : (r < 80) ? 2 : 3;
    }

    private void give(Player p, ItemStack stack) {
        PlayerInventory inv = p.getInventory();

        Map<Integer, ItemStack> leftover = inv.addItem(stack);
        if (leftover.isEmpty()) return;

        Location loc = p.getLocation();
        for (ItemStack item : leftover.values()) {
            p.getWorld().dropItemNaturally(loc, item);
        }
    }
}
