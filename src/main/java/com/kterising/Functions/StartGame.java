package com.kterising.Functions;

import com.kterising.Command.Command;
import com.kterising.Team.Team;
import com.kterising.Team.TeamManager;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

import static com.kterising.Functions.ModVoteGUI.modManager;

public class StartGame implements Listener {
    public static int seconds;
    public static boolean time;
    public static boolean win = false;
    public static boolean lavarising;
    public static int survivor;
    public static int lava;
    public static String winner;
    public static boolean PvP;
    public static String mode;
    public static Boolean match;
    public static Boolean end = false;
    private static BukkitRunnable task;

    public static HashMap<UUID, Location> leavedPlayers = new HashMap<UUID, Location>();


    public static Sound getSoundFromConfig(Plugin plugin, String path) {
        return Sound.valueOf(plugin.getConfig().getString(path));
    }

    public static void skipTime(Plugin plugin) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (lavarising) {
                System.out.println(ChatColor.translateAlternateColorCodes('&', "[KteRising] Cancel Event! Command: Skip"));
            } else {
                player.getWorld().playSound(player.getLocation(), getSoundFromConfig(plugin, "sound.skip-sound"), 1.0f, 1.0f);
                seconds = 3;
            }
        }
    }

    public static void start(Plugin plugin) {
        if (match) {
            return;
        }

        lavarising = false;
        time = false;
        match = true;
        lava = plugin.getConfig().getInt("lava-start-block");

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.getInventory().clear();
            player.setHealth(20);
            player.setFoodLevel(20);
            player.setGameMode(GameMode.SURVIVAL);

            String title = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("title.start-game.title")));
            String subtitle = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("title.start-game.sub")));
            player.sendTitle(title, subtitle);
            player.getWorld().playSound(player.getLocation(), getSoundFromConfig(plugin, "sound.start-sound"), 1.0f, 1.0f);

            PvP = false;

            ItemStack netheritePickaxe = new ItemStack(Material.valueOf(ItemsConfig.getConfig().getString("items.pickaxe.material")));
            ItemMeta pickaxeMeta = netheritePickaxe.getItemMeta();
            assert pickaxeMeta != null;
            pickaxeMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(ItemsConfig.getConfig().getString("items.pickaxe.name"))));
            netheritePickaxe.setItemMeta(pickaxeMeta);
            netheritePickaxe.addUnsafeEnchantment(Enchantment.DIG_SPEED, 5);

            ItemStack cookedBeef = new ItemStack(Material.valueOf(ItemsConfig.getConfig().getString("items.food.material")), ItemsConfig.getConfig().getInt("items.food.amount"));
            ItemMeta foodMeta = cookedBeef.getItemMeta();
            assert foodMeta != null;
            foodMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(ItemsConfig.getConfig().getString("items.food.name"))));
            cookedBeef.setItemMeta(foodMeta);

            ItemStack log = new ItemStack(Material.valueOf(ItemsConfig.getConfig().getString("items.oak.material")), ItemsConfig.getConfig().getInt("items.oak.amount"));

            if ("Classic".equals(mode)) {
                seconds = plugin.getConfig().getInt("classic-start-time");
                player.getInventory().addItem(netheritePickaxe, cookedBeef, log);
            } else if ("OP".equals(mode)) {
                seconds = plugin.getConfig().getInt("op-start-time");
                ItemStack diamond = new ItemStack(Material.DIAMOND, 64);
                ItemStack iron = new ItemStack(Material.IRON_INGOT, 64);
                ItemStack stone = new ItemStack(Material.COBBLESTONE, 576);
                player.getInventory().addItem(netheritePickaxe, cookedBeef, diamond, iron, stone, log);
            } else if ("Elytra".equals(mode)) {
                seconds = plugin.getConfig().getInt("classic-start-time");
                ItemStack elytra = new ItemStack(Material.valueOf(ItemsConfig.getConfig().getString("items.elytra.material")), ItemsConfig.getConfig().getInt("items.elytra.amount"));
                ItemMeta elytraMeta = elytra.getItemMeta();
                assert elytraMeta != null;
                elytraMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(ItemsConfig.getConfig().getString("items.elytra.name"))));
                elytra.setItemMeta(elytraMeta);
                ItemStack firework = new ItemStack(Material.valueOf(ItemsConfig.getConfig().getString("items.firework.material")), ItemsConfig.getConfig().getInt("items.firework.amount"));
                player.getInventory().addItem(netheritePickaxe, cookedBeef, log, elytra, firework);
            } else if ("ElytraOP".equals(mode)) {
                seconds = plugin.getConfig().getInt("op-start-time");
                ItemStack diamond = new ItemStack(Material.DIAMOND, 64);
                ItemStack iron = new ItemStack(Material.IRON_INGOT, 64);
                ItemStack stone = new ItemStack(Material.COBBLESTONE, 576);
                ItemStack elytra = new ItemStack(Material.valueOf(ItemsConfig.getConfig().getString("items.elytra.material")), ItemsConfig.getConfig().getInt("items.elytra.amount"));
                ItemMeta elytraMeta = elytra.getItemMeta();
                assert elytraMeta != null;
                elytraMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(ItemsConfig.getConfig().getString("items.elytra.name"))));
                elytra.setItemMeta(elytraMeta);
                ItemStack firework = new ItemStack(Material.valueOf(ItemsConfig.getConfig().getString("items.firework.material")), ItemsConfig.getConfig().getInt("items.firework.op-amount"));
                player.getInventory().addItem(netheritePickaxe, cookedBeef, diamond, iron, stone, log, firework, elytra);
            } else if ("Trident".equals(mode)) {
                seconds = plugin.getConfig().getInt("classic-start-time");
                ItemStack tridentRiptide = new ItemStack(Material.valueOf(ItemsConfig.getConfig().getString("items.trident.material")));
                ItemMeta tridentRiptideMeta = tridentRiptide.getItemMeta();
                assert tridentRiptideMeta != null;
                tridentRiptideMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(ItemsConfig.getConfig().getString("items.trident.riptide-name"))));
                tridentRiptide.setItemMeta(tridentRiptideMeta);
                tridentRiptide.addUnsafeEnchantment(Enchantment.RIPTIDE, 3);

                ItemStack tridentLoyalty = new ItemStack(Material.valueOf(ItemsConfig.getConfig().getString("items.trident.material")));
                ItemMeta tridentLoyaltyMeta = tridentLoyalty.getItemMeta();
                assert tridentLoyaltyMeta != null;
                tridentLoyaltyMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(ItemsConfig.getConfig().getString("items.trident.loyalty-name"))));
                tridentLoyalty.setItemMeta(tridentLoyaltyMeta);
                tridentLoyalty.addUnsafeEnchantment(Enchantment.LOYALTY, 3);

                player.getInventory().addItem(netheritePickaxe, cookedBeef, log, tridentRiptide, tridentLoyalty);
            } else if ("TridentOP".equals(mode)) {
                seconds = plugin.getConfig().getInt("op-start-time");
                ItemStack diamond = new ItemStack(Material.DIAMOND, 64);
                ItemStack iron = new ItemStack(Material.IRON_INGOT, 64);
                ItemStack stone = new ItemStack(Material.COBBLESTONE, 576);
                ItemStack tridentRiptide = new ItemStack(Material.valueOf(ItemsConfig.getConfig().getString("items.trident.material")));
                ItemMeta tridentRiptideMeta = tridentRiptide.getItemMeta();
                assert tridentRiptideMeta != null;
                tridentRiptideMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(ItemsConfig.getConfig().getString("items.trident.riptide-name"))));
                tridentRiptide.setItemMeta(tridentRiptideMeta);
                tridentRiptide.addUnsafeEnchantment(Enchantment.RIPTIDE, 3);

                ItemStack tridentLoyalty = new ItemStack(Material.valueOf(ItemsConfig.getConfig().getString("items.trident.material")));
                ItemMeta tridentLoyaltyMeta = tridentLoyalty.getItemMeta();
                assert tridentLoyaltyMeta != null;
                tridentLoyaltyMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(ItemsConfig.getConfig().getString("items.trident.loyalty-name"))));
                tridentLoyalty.setItemMeta(tridentLoyaltyMeta);
                tridentLoyalty.addUnsafeEnchantment(Enchantment.LOYALTY, 3);

                player.getInventory().addItem(netheritePickaxe, cookedBeef, diamond, iron, stone, log, tridentRiptide, tridentLoyalty);
            } else if ("UltraOP".equals(mode)) {
                seconds = plugin.getConfig().getInt("op-start-time");
                ItemStack diamond = new ItemStack(Material.DIAMOND, 64);
                ItemStack iron = new ItemStack(Material.IRON_INGOT, 64);
                ItemStack stone = new ItemStack(Material.COBBLESTONE, 576);
                ItemStack goldenApple = new ItemStack(Material.valueOf(ItemsConfig.getConfig().getString("items.ultra-food.apple.material")), ItemsConfig.getConfig().getInt("items.ultra-food.apple.amount"));
                ItemStack bow = new ItemStack(Material.valueOf(ItemsConfig.getConfig().getString("items.bow.material")));
                ItemMeta bowMeta = bow.getItemMeta();
                assert bowMeta != null;
                bowMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(ItemsConfig.getConfig().getString("items.bow.bow-name"))));
                bow.setItemMeta(bowMeta);
                ItemStack arrow = new ItemStack(Material.valueOf(ItemsConfig.getConfig().getString("items.arrow.material")), ItemsConfig.getConfig().getInt("items.arrow.amount"));
                ItemStack goldencarrot = new ItemStack(Material.valueOf(ItemsConfig.getConfig().getString("items.ultra-food.carrot.material")), ItemsConfig.getConfig().getInt("items.ultra-food.carrot.amount"));

                player.getInventory().addItem(netheritePickaxe, cookedBeef, diamond, iron, stone, log, goldenApple, bow, arrow, goldencarrot);
            }
        }


            task = new BukkitRunnable() {
                @Override
                public void run() {
                    live(plugin);

                    if (time) {
                        seconds++;
                    } else {
                        seconds--;
                    }

                    if (seconds == 0) {
                        time = true;
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            String title = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("title.lava-starting.title")));
                            String subtitle = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("title.lava-starting.sub")));
                            player.sendTitle(title, subtitle);
                            player.getWorld().playSound(player.getLocation(), getSoundFromConfig(plugin, "sound.lava-rise-sound"), 1.0f, 1.0f);
                            lavarising = true;
                            leavedPlayers.clear();
                        }
                        upLava(plugin);

                    }
                }
            };

            task.runTaskTimer(plugin, 20L, 20L);
        }

    public static void live(Plugin plugin) {
        Bukkit.getServer().getConsoleSender();
        survivor = 0;
        Player winnerPlayer = null;
        Team winnerTeam = null;
        List<Player> survivingPlayers = new ArrayList<>();

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getGameMode() == GameMode.SURVIVAL) {
                survivor++;
                survivingPlayers.add(player);
                if (winnerPlayer == null) {
                    winnerPlayer = player;
                    winnerTeam = TeamManager.getPlayerTeam(player);
                } else if (winnerTeam != null && !winnerTeam.getPlayers().contains(player)) {
                    winnerTeam = null;
                }
            }
        }

        if (survivor == 1 || (survivor == 2 && winnerTeam != null)) {
            String winner;
            if (survivor == 1) {
                assert winnerPlayer != null;
                winner = winnerPlayer.getName();
                if (!win) {
                    PlayerStats.addWin(winnerPlayer);
                    win = true;
                }
            } else {
                winner = survivingPlayers.get(0).getName() + ", " + survivingPlayers.get(1).getName();
                if (!win && winnerTeam != null) {
                    for (Player player : winnerTeam.getPlayers()) {
                        PlayerStats.addWin(player);
                    }
                    win = true;
                }
            }

            for (Player player : Bukkit.getOnlinePlayers()) {
                String title = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("title.finish-game.title")));
                title = title.replace("%winner%", winner);
                String subtitle = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("title.finish-game.sub")).replace("%winner%", winner));
                player.sendTitle(title, subtitle);
                player.getWorld().playSound(player.getLocation(), getSoundFromConfig(plugin, "sound.winner-sound"), 1.0f, 1.0f);
            }


                if(!end) {
                    Bukkit.getScheduler().runTaskLater(plugin, () -> resetWorldBorder(plugin), 100L);
                    end = true;
                }
        }

        if (survivor == 0) {
            if(!end) {
                Bukkit.getScheduler().runTaskLater(plugin, () -> resetWorldBorder(plugin), 100L);
                end = true;
            }
        }
    }

    public static int centerX = 0;
    public static int centerZ = 0;

    public static void resetWorldBorder(Plugin plugin) {
        if (task != null) {
            task.cancel();
            task = null;
        }
        centerX += 1000;
        centerZ += 1000;

        StartGame.lavarising = false;
        StartGame.match = false;
        StartGame.PvP = false;
        StartGame.survivor = 0;
        StartGame.seconds = 0;
        StartGame.winner = null;
        StartGame.win = false;
        StartGame.end = false;
        StartGame.leavedPlayers.clear();
        lava = plugin.getConfig().getInt("lava-start-block");
        Command.selectedmode = false;
        modManager.resetVotes();

        World world = Bukkit.getWorld("world");
        assert world != null;
        WorldBorder worldBorder = world.getWorldBorder();
        worldBorder.setSize(plugin.getConfig().getInt("world-size"));
        worldBorder.setCenter(centerX, centerZ);
        worldBorder.setDamageAmount(5);
        worldBorder.setDamageBuffer(2);

        world.setSpawnLocation(centerX, 100, centerZ);

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.getInventory().clear();
            player.setGameMode(GameMode.ADVENTURE);
            player.setHealth(20);
            player.setFoodLevel(20);

            for (int y = 60; y < 100; y++) {
                Location loc = new Location(world, centerX, y, centerZ);
                if (world.getBlockAt(loc).getType() == Material.AIR) {
                    player.teleport(loc);

                    player.setBedSpawnLocation(loc, true);
                    break;
                }
            }
            SpecialItems.giveSpecialItems(player);
            TeamManager.removePlayerFromTeam(player);
            TeamManager.assignPlayerToTeam(player);
        }
    }

    public static void upLava(final Plugin plugin) {
        final World world = Bukkit.getWorld("world");
        assert world != null;
        final WorldBorder worldBorder = world.getWorldBorder();
        final int minX = (int)(worldBorder.getCenter().getX() - worldBorder.getSize() / 2.0D);
        final int minZ = (int)(worldBorder.getCenter().getZ() - worldBorder.getSize() / 2.0D);
        final int maxX = minX + (int)worldBorder.getSize();
        final int maxZ = minZ + (int)worldBorder.getSize();
        (new BukkitRunnable() {
            public void run() {
                if (!StartGame.lavarising)
                    return;
                int currentLavaLevel = StartGame.lava;
                List<Location> lavaLocations = new ArrayList<>();
                for (int x = minX; x <= maxX; x++) {
                    for (int z = minZ; z <= maxZ; z++) {
                        Location location = new Location(world, x, currentLavaLevel, z);
                        if (StartGame.lava == plugin.getConfig().getInt("lava-border") - 1) {
                            if (location.getBlock().getType() == Material.AIR)
                                lavaLocations.add(location);
                        } else {
                            lavaLocations.add(location);
                        }
                    }
                }
                for (Location location : lavaLocations)
                    location.getBlock().setType(Material.LAVA, false);
                if (StartGame.lava == plugin.getConfig().getInt("pvp-allow") && !StartGame.PvP) {
                    StartGame.PvP = true;
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        String title = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("title.pvp-allow.title")));
                        String subtitle = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("title.pvp-allow.sub")));
                        player.getWorld().playSound(player.getLocation(), StartGame.getSoundFromConfig(plugin, "sound.pvp-sound"), 1.0F, 1.0F);
                        player.sendTitle(title, subtitle);
                    }
                }
                if (StartGame.lava == plugin.getConfig().getInt("lava-border")) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        if (player.getLocation().getY() <= plugin.getConfig().getInt("border-kill")) {
                            player.setHealth(0.0D);
                        }
                    }
                    StartGame.live(plugin);
                    worldBorder.setSize(3.0D, 180L);
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        String title = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("title.worldborder-shrink.title")));
                        String subtitle = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("title.worldborder-shrink.sub")));
                        player.getWorld().playSound(player.getLocation(), StartGame.getSoundFromConfig(plugin, "sound.shrink-sound"), 1.0F, 1.0F);
                        player.sendTitle(title, subtitle);
                    }

                    this.cancel();
                }
                if (StartGame.lava < plugin.getConfig().getInt("lava-border")) {
                    StartGame.lava++;
                }
            }
        }).runTaskTimer(plugin, 0L, 20L * plugin.getConfig().getInt("lava-delay"));
    }
}
