package com.kterising.Functions;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.entity.Player;

import java.util.*;


public class ModVoteGUI implements Listener {
    private static final String MENU_TITLE = (ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("mod.vote-gui"))));
    private static final ModManager modManager = new ModManager();
    private static boolean votingEnabled = true;
    private static final Set<UUID> votingPlayers = new HashSet<>();

    public static void openModVoteMenu(Player player) {
        if (isVotingEnabled()) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("mod.voting-disabled"))));
            return;
        }

        Inventory inventory = Bukkit.createInventory(null, 27, MENU_TITLE);

        int[] slots = {10, 11, 12, 13, 14, 15, 16};

        int index = 0;
        for (String mod : modManager.getModNames()) {
            ItemStack modItem = createModItem(mod);
            inventory.setItem(slots[index++], modItem);
        }

        player.openInventory(inventory);
        votingPlayers.add(player.getUniqueId());
    }

    private static ItemStack createModItem(String mod) {
        ItemStack modItem = null;

        switch (mod) {
            case "Classic":
                modItem = new ItemStack(Material.DIAMOND_PICKAXE);
                break;
            case "Elytra":
                modItem = new ItemStack(Material.ELYTRA);
                break;
            case "Trident":
                modItem = new ItemStack(Material.TRIDENT);
                break;
            case "OP":
                modItem = new ItemStack(Material.DIAMOND_PICKAXE);
                modItem.addUnsafeEnchantment(Enchantment.DIG_SPEED, 5);
                break;
            case "UltraOP":
                modItem = new ItemStack(Material.BOW);
                modItem.addUnsafeEnchantment(Enchantment.DURABILITY, 5);
                break;
            case "ElytraOP":
                modItem = new ItemStack(Material.ELYTRA);
                modItem.addUnsafeEnchantment(Enchantment.DURABILITY, 3);
                break;
            case "TridentOP":
                modItem = new ItemStack(Material.TRIDENT);
                modItem.addUnsafeEnchantment(Enchantment.RIPTIDE, 3);
                break;
        }

        if (modItem != null) {
            ItemMeta meta = modItem.getItemMeta();
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&6" + mod));
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("mod.votes")).replace("%votes%", String.valueOf(modManager.getVotes(mod)))));
            meta.setLore(lore);
            modItem.setItemMeta(meta);
        }

        return modItem;
    }

    public static void setVotingEnabled(boolean enabled) {
        votingEnabled = enabled;
    }

    public static boolean isVotingEnabled() {
        return !votingEnabled;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (isVotingEnabled()) return;

        if (event.getView().getTitle().equals(MENU_TITLE)) {
            event.setCancelled(true);
            if (event.getCurrentItem() != null && event.getCurrentItem().hasItemMeta()) {
                Player player = (Player) event.getWhoClicked();
                String displayName = event.getCurrentItem().getItemMeta().getDisplayName();
                String mod = ChatColor.stripColor(displayName);

                String previousVote = modManager.getVoteForPlayer(player.getUniqueId());
                if (previousVote != null && previousVote.equals(mod)) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("mod.already-voted"))));
                    return;
                }

                modManager.voteForMod(player, mod);
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("mod.voted")).replace("%mod%", mod)));
                player.closeInventory();
            }
        }
    }

    public static String getWinningMod() {
        Map<String, Integer> modVotes = modManager.getModVotes();
        List<String> topMods = new ArrayList<>();
        int maxVotes = 0;

        for (Map.Entry<String, Integer> entry : modVotes.entrySet()) {
            if (entry.getValue() > maxVotes) {
                maxVotes = entry.getValue();
                topMods.clear();
                topMods.add(entry.getKey());
            } else if (entry.getValue() == maxVotes) {
                topMods.add(entry.getKey());
            }
        }

        if (topMods.isEmpty()) {
            return null;
        }

        Random random = new Random();
        return topMods.get(random.nextInt(topMods.size()));
    }

    public static void selectWinningMod() {
        String winningMod = getWinningMod();
        if (winningMod != null) {
            StartGame.mode = winningMod;
            String title = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("title.mod-winner.title")));
            title = title.replace("%mod%", winningMod);
            String subtitle = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("title.mod-winner.sub")).replace("%mod%", winningMod));
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.sendTitle(title, subtitle);
                player.playSound(player.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 1.0f, 1.0f);
            }
        } else {
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("mod.no_winner"))));
        }
    }

    public static void closeModVoteMenu(Player player) {
        if (votingPlayers.contains(player.getUniqueId())) {
            player.closeInventory();
            votingPlayers.remove(player.getUniqueId());
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        closeModVoteMenu(player);
    }
}