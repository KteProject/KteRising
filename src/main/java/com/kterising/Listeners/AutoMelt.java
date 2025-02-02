package com.kterising.Listeners;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public class AutoMelt implements Listener {

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        ItemStack droppedItem = null;
            switch (event.getBlock().getType()) {
                case IRON_ORE:
                case DEEPSLATE_IRON_ORE:
                    droppedItem = new ItemStack(Material.IRON_INGOT, 1);
                    break;
                case GOLD_ORE:
                case DEEPSLATE_GOLD_ORE:
                    droppedItem = new ItemStack(Material.GOLD_INGOT, 1);
                    break;
                case COPPER_ORE:
                case DEEPSLATE_COPPER_ORE:
                    droppedItem = new ItemStack(Material.COPPER_INGOT, 1);
                    break;
                default:
                    break;
            }

        if (droppedItem != null) {
            event.setDropItems(false);
            player.getInventory().addItem(droppedItem);
        }
    }
}
