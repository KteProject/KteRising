package com.kterising.Listeners;

import com.kterising.KteRising;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class AutoPickUp implements Listener {
    private final KteRising plugin;
    private final Random random;

    public AutoPickUp(KteRising plugin) {
        this.plugin = plugin;
        this.random = new Random();
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        ItemStack droppedItem = null;
        if(plugin.getConfig().getBoolean("autopickup")) {
            double chance = random.nextDouble();
            switch (event.getBlock().getType()) {
                case STONE:
                case COBBLESTONE:
                    droppedItem = new ItemStack(Material.COBBLESTONE, 1);
                    break;
                case GRASS_BLOCK:
                case DIRT:
                    droppedItem = new ItemStack(Material.DIRT, 1);
                    break;
                case COAL_ORE:
                    droppedItem = new ItemStack(Material.COAL, 1);
                    break;
                case DIAMOND_ORE:
                    droppedItem = new ItemStack(Material.DIAMOND, 1);
                    break;
                case OAK_LOG:
                    droppedItem = new ItemStack(Material.OAK_LOG, 1);
                    break;
                case ACACIA_LOG:
                    droppedItem = new ItemStack(Material.ACACIA_LOG, 1);
                    break;
                case BIRCH_LOG:
                    droppedItem = new ItemStack(Material.BIRCH_LOG, 1);
                    break;
                case DARK_OAK_LOG:
                    droppedItem = new ItemStack(Material.DARK_OAK_LOG, 1);
                    break;
                case JUNGLE_LOG:
                    droppedItem = new ItemStack(Material.JUNGLE_LOG, 1);
                    break;
                case SPRUCE_LOG:
                    droppedItem = new ItemStack(Material.SPRUCE_LOG, 1);
                    break;
                case DIORITE:
                    droppedItem = new ItemStack(Material.DIORITE, 1);
                    break;
                case ANDESITE:
                    droppedItem = new ItemStack(Material.ANDESITE, 1);
                    break;
                case GRANITE:
                    droppedItem = new ItemStack(Material.GRANITE, 1);
                    break;
                case EMERALD_ORE:
                    droppedItem = new ItemStack(Material.EMERALD_ORE, 1);
                    break;
                case GRAVEL:
                    if (chance < 0.7) {
                        droppedItem = new ItemStack(Material.GRAVEL, 1);
                    } else {
                        droppedItem = new ItemStack(Material.FLINT, 1);
                    }
                    break;
                case REDSTONE_ORE:
                    droppedItem = new ItemStack(Material.REDSTONE, 2);
                    break;
                case LAPIS_ORE:
                    droppedItem = new ItemStack(Material.LAPIS_LAZULI, 2);
                    break;
                case SAND:
                    droppedItem = new ItemStack(Material.SAND, 1);
                    break;
                case CLAY:
                    droppedItem = new ItemStack(Material.CLAY_BALL, 3);
                    break;
                case OBSIDIAN:
                    droppedItem = new ItemStack(Material.OBSIDIAN, 1);
                    break;
                case SANDSTONE:
                    droppedItem = new ItemStack(Material.SANDSTONE, 1);
                    break;
                default:
                    break;
            }
        }

        if (droppedItem != null) {
            event.setDropItems(false);
            player.getInventory().addItem(droppedItem);
        }
    }
}