package com.kterising.Listeners;

import com.kterising.KteRising;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public class AutoMelt implements Listener {
    private final KteRising plugin;

    public AutoMelt(KteRising plugin) {this.plugin = plugin;}

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        ItemStack droppedItem = null;
        if(plugin.getConfig().getBoolean("automelt")) {
            switch (event.getBlock().getType()) {
                case IRON_ORE:
                    droppedItem = new ItemStack(Material.IRON_INGOT, 1);
                    break;
                case GOLD_ORE:
                    droppedItem = new ItemStack(Material.GOLD_INGOT, 1);
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
