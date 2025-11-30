package com.kteproject.kterising.listeners;
import com.kteproject.kterising.KteRising;
import com.kteproject.kterising.game.Game;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class AutoPickUp implements Listener {

    public AutoPickUp(KteRising plugin) {}

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBreak(BlockBreakEvent e) {
        Player p = e.getPlayer();
        if(p.getGameMode() == GameMode.CREATIVE)return;
        if (!Game.match && !p.isOp()) {
            e.setCancelled(true);
            return;
        }
        if(!KteRising.getConfiguration().getBoolean("game-configurations.auto-pickup"))return;
        Block block = e.getBlock();
        e.setDropItems(false);

        Collection<ItemStack> drops = block.getDrops(p.getInventory().getItemInMainHand());
        drops = autoSmeltDrops(drops);
        for (ItemStack drop : drops) {
            HashMap<Integer, ItemStack> leftover = p.getInventory().addItem(drop);
            if (!leftover.isEmpty()) {
                leftover.values().forEach(item -> p.getWorld().dropItemNaturally(p.getLocation(), item));
            }
        }
    }

    private Collection<ItemStack> autoSmeltDrops(Collection<ItemStack> drops) {
        List<ItemStack> newDrops = new ArrayList<>();
        for (ItemStack item : drops) {
            Material type = item.getType();
            switch (type) {
                case RAW_IRON,IRON_ORE,DEEPSLATE_IRON_ORE -> newDrops.add(new ItemStack(Material.IRON_INGOT, item.getAmount()));
                case RAW_GOLD,GOLD_ORE,DEEPSLATE_GOLD_ORE -> newDrops.add(new ItemStack(Material.GOLD_INGOT, item.getAmount()));
                case RAW_COPPER,COPPER_ORE,DEEPSLATE_COPPER_ORE -> newDrops.add(new ItemStack(Material.COPPER_INGOT, item.getAmount()));
                default -> newDrops.add(item);
            }
        }return newDrops;
    }
}
