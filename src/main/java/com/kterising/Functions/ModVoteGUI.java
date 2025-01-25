package com.kterising.Functions;

import com.kterising.Command.Command;
import com.kterising.Team.TeamManager;
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
    private static final String MENU_TITLE = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("mod.vote-gui")));
    public static final ModManager modManager = new ModManager();
    private static final Set<UUID> votingPlayers = new HashSet<>();
    private static String cachedWinningTeam = null;

    public static void openModVoteMenu(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 36, MENU_TITLE);

        int[] slots = {10, 11, 12, 13, 14, 15, 16};

        int index = 0;
        for (String mod : modManager.getModNames()) {
            ItemStack modItem = createModItem(mod);
            inventory.setItem(slots[index++], modItem);
        }

        ItemStack teamItem = createTeamItem(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("vote.team"))), Material.DIAMOND_SWORD);
        inventory.setItem(27, teamItem);
        ItemStack noTeamItem = createTeamItem(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("vote.no-team"))), Material.BARRIER);
        inventory.setItem(28, noTeamItem);

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
            if (meta != null) {
                meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&6" + mod));
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

                List<String> lore = new ArrayList<>();
                lore.add(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("mod.votes")).replace("%votes%", String.valueOf(modManager.getVotes(mod)))));
                meta.setLore(lore);
                modItem.setItemMeta(meta);
            }
        }

        return modItem;
    }

    private static ItemStack createTeamItem(String name, Material material) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&6" + name));

            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("mod.votes")).replace("%votes%", String.valueOf(modManager.getTeamVotes(name)))));
            meta.setLore(lore);

            item.setItemMeta(meta);
        }
        return item;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals(MENU_TITLE)) {
            event.setCancelled(true);
            if (event.getCurrentItem() != null && event.getCurrentItem().hasItemMeta()) {
                Player player = (Player) event.getWhoClicked();
                String displayName = Objects.requireNonNull(event.getCurrentItem().getItemMeta()).getDisplayName();
                String mod = ChatColor.stripColor(displayName);
                if (mod.equals(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("vote.team")))) || mod.equals(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("vote.no-team"))))) {
                    handleTeamVote(player, mod);
                } else {
                    handleModVote(player, mod);
                }
            }
        }
    }

    private void handleModVote(Player player, String mod) {
        String previousVote = modManager.getVoteForPlayer(player.getUniqueId());
        if (previousVote != null && previousVote.equals(mod)) {
            player.closeInventory();
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("mod.already-voted"))));
            return;
        }

        modManager.voteForMod(player, mod);
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("mod.voted")).replace("%mod%", mod)));
        player.closeInventory();
    }

    private void handleTeamVote(Player player, String teamVote) {
        String previousVote = modManager.getTeamVoteForPlayer(player.getUniqueId());
        if (previousVote != null && previousVote.equals(teamVote)) {
            player.closeInventory();
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("mod.already-voted"))));
            return;
        }

        modManager.voteForTeam(player, teamVote);
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("mod.voted")).replace("%mod%", teamVote)));
        player.closeInventory();
    }

    public static String getWinningMod() {
        if (Command.selectedmode) {
            return StartGame.mode;
        } else {
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

            Random random = new Random();
            return topMods.get(random.nextInt(topMods.size()));
        }
    }

    public static String getWinningTeam() {

        Map<String, Integer> teamVotes = modManager.getTeamVotes();
        List<String> topTeams = new ArrayList<>();
        int maxVotes = 0;

        for (Map.Entry<String, Integer> entry : teamVotes.entrySet()) {
            if (entry.getValue() > maxVotes) {
                maxVotes = entry.getValue();
                topTeams.clear();
                topTeams.add(entry.getKey());
            } else if (entry.getValue() == maxVotes) {
                topTeams.add(entry.getKey());
            }
        }

        if (topTeams.size() > 1) {
            Random random = new Random();
            cachedWinningTeam = topTeams.get(random.nextInt(topTeams.size()));
        } else if (!topTeams.isEmpty()) {
            cachedWinningTeam = topTeams.get(0);
        }

        return cachedWinningTeam;
    }

    public static void selectWinningVotes() {
        String winningMod = getWinningMod();
        String winningTeam = getWinningTeam();

        if (winningMod != null && winningTeam != null) {
            String combinedTitle = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("title.mod-winner.title")));
            combinedTitle = combinedTitle.replace("%mod%", winningMod).replace("%team%", winningTeam);
            String combinedSubtitle = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("title.mod-winner.sub")).replace("%mod%", winningMod).replace("%team%", winningTeam));

            for (Player player : Bukkit.getOnlinePlayers()) {
                player.sendTitle(combinedTitle, combinedSubtitle);
                player.playSound(player.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 1.0f, 1.0f);
            }

            if (!Command.selectedmode) {
                switch (winningMod) {
                    case "Classic":
                        StartGame.mode = "Classic";
                        break;
                    case "Elytra":
                        StartGame.mode = "Elytra";
                        break;
                    case "Trident":
                        StartGame.mode = "Trident";
                        break;
                    case "OP":
                        StartGame.mode = "OP";
                        break;
                    case "UltraOP":
                        StartGame.mode = "UltraOP";
                        break;
                    case "ElytraOP":
                        StartGame.mode = "ElytraOP";
                        break;
                    case "TridentOP":
                        StartGame.mode = "TridentOP";
                        break;
                }

                if (ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("vote.no-team"))).equals(winningTeam)) {
                    TeamManager.removeAllPlayersFromTeams();
                }
            }
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
