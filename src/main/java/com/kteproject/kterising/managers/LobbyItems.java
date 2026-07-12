package com.kteproject.kterising.managers;

import com.kteproject.kterising.KteRising;
import com.kteproject.kterising.game.MatchSession;
import com.kteproject.kterising.utils.ChatUtil;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class LobbyItems implements Listener {

    private static final int COMPASS_SLOT = 4;
    private static final String LOBBY_TAG = "lobby_vote";

    private static ItemStack cachedCompass;
    private static NamespacedKey lobbyKey;

    private static NamespacedKey key() {
        if (lobbyKey == null) {
            lobbyKey = new NamespacedKey(KteRising.getInstance(), LOBBY_TAG);
        }
        return lobbyKey;
    }

    private static void buildCachedCompass() {
        ItemStack compass = new ItemStack(Material.COMPASS);
        ItemMeta meta = compass.getItemMeta();
        meta.displayName(MiniMessage.miniMessage().deserialize("<!i>" + ChatUtil.getText("vote.vote-item")));
        meta.getPersistentDataContainer().set(key(), PersistentDataType.STRING, "1");
        compass.setItemMeta(meta);
        cachedCompass = compass;
    }

    public static void invalidateCache() {
        cachedCompass = null;
    }

    public static void giveLobbyItem(Player player) {
        MatchSession session = KteRising.getMatch();
        if (session != null && session.isMatch()) return;

        if (cachedCompass == null) {
            buildCachedCompass();
        }

        player.getInventory().clear();
        KteRising.getInstance().getServer().getGlobalRegionScheduler().runDelayed(
                KteRising.getInstance(),
                (ScheduledTask task) -> {
                    if (player.isOnline()) {
                        player.getInventory().setItem(COMPASS_SLOT, cachedCompass.clone());
                    }
                },
                1L
        );
    }

    private static boolean isLobbyItem(ItemStack item) {
        if (item == null || item.getType() != Material.COMPASS || !item.hasItemMeta()) {
            return false;
        }
        return item.getItemMeta().getPersistentDataContainer().has(key(), PersistentDataType.STRING);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (!isLobbyItem(event.getItem())) return;
        Action a = event.getAction();
        if (a == Action.RIGHT_CLICK_AIR || a == Action.RIGHT_CLICK_BLOCK) {
            event.setCancelled(true);
            KteRising.getInstance().getVoteGui().open(event.getPlayer());
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        MatchSession session = KteRising.getMatch();
        if (session == null || !session.isMatch()) {
            event.setCancelled(true);
            return;
        }
        if (isLobbyItem(event.getItemDrop().getItemStack())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onItemPickup(EntityPickupItemEvent e) {
        if (!(e.getEntity() instanceof Player)) return;
        MatchSession session = KteRising.getMatch();
        if (session == null || !session.isMatch()) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (isLobbyItem(event.getCurrentItem())) {
            event.setCancelled(true);
        }
    }
}
