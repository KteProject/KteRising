package com.kteproject.kterising.managers;
import com.kteproject.kterising.KteRising;
import com.kteproject.kterising.game.Game;
import com.kteproject.kterising.utils.ChatUtil;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class LobbyItems implements Listener {

    private static final int COMPASS_SLOT = 4;
    private static ItemStack CACHED_COMPASS;

    private static void buildCachedCompass() {
        ItemStack compass = new ItemStack(Material.COMPASS);
        ItemMeta meta = compass.getItemMeta();
        meta.displayName(MiniMessage.miniMessage().deserialize("<!i>" + ChatUtil.getText("vote.vote-item")));
        compass.setItemMeta(meta);
        CACHED_COMPASS = compass;
    }

    public static void giveLobbyItem(Player player) {
        if (Game.match) return;

        if (CACHED_COMPASS == null) {
            buildCachedCompass();
        }

        player.getInventory().clear();
        KteRising.getInstance().getServer().getGlobalRegionScheduler().runDelayed(
                KteRising.getInstance(),
                (ScheduledTask task) -> player.getInventory().setItem(COMPASS_SLOT, CACHED_COMPASS),
                1L
        );
    }

    public static void openModeSelector(Player player) {
        KteRising.getInstance().getVoteGui().open(player);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (!isLobbyCompass(event)) return;

        Action a = event.getAction();
        if (a == Action.RIGHT_CLICK_AIR || a == Action.RIGHT_CLICK_BLOCK) {
            event.setCancelled(true);
            openModeSelector(event.getPlayer());
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        if (!Game.match){
            event.setCancelled(true);
            return;
        }
        if (isLobbyCompass(event)) {
            event.setCancelled(true);
        }
    }
    @EventHandler
    public void onItemPickup(EntityPickupItemEvent e){
        if (!(e.getEntity() instanceof Player)) return;
        if (!Game.match){e.setCancelled(true);}
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (isLobbyCompass(event)) {
            event.setCancelled(true);
        }
    }

    private boolean isLobbyCompass(PlayerInteractEvent e) {
        ItemStack item = e.getItem();
        return item != null
                && item.getType() == Material.COMPASS
                && e.getPlayer().getInventory().getHeldItemSlot() == COMPASS_SLOT;
    }

    private boolean isLobbyCompass(PlayerDropItemEvent e) {
        ItemStack item = e.getItemDrop().getItemStack();
        return item.getType() == Material.COMPASS
                && e.getPlayer().getInventory().getHeldItemSlot() == COMPASS_SLOT;
    }

    private boolean isLobbyCompass(InventoryClickEvent e) {
        ItemStack item = e.getCurrentItem();
        return item != null
                && item.getType() == Material.COMPASS
                && e.getSlot() == COMPASS_SLOT;
    }
}
