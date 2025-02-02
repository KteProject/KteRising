package com.kterising.Listeners;

import com.kterising.Functions.StartGame;
import com.kterising.KteRising;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockFromToEvent;

public class LavaWaterFix implements Listener {

    private final KteRising plugin;

    public LavaWaterFix(KteRising plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockForm(BlockFormEvent event) {
        Material newType = event.getNewState().getType();
        if (newType == Material.OBSIDIAN || newType == Material.COBBLESTONE) {
            if (StartGame.lava == plugin.getConfig().getInt("lava-border")) {
                return;
            }
            event.setCancelled(true);
        }
    }


    @EventHandler
    public void onBlockFlow(BlockFromToEvent event) {
        Material blockType = event.getBlock().getType();
        Material toBlockType = event.getToBlock().getType();

        if ((blockType == Material.LAVA && toBlockType == Material.WATER) ||
                (blockType == Material.WATER && toBlockType == Material.LAVA)) {
            if (StartGame.lava == plugin.getConfig().getInt("lava-border")) {
                return;
            }
            event.setCancelled(true);
        }
    }

}
