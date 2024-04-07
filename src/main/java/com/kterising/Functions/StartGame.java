package com.kterising.Functions;


import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.Objects;

public class StartGame implements Listener {
    public static int seconds;
    public static boolean time;
    public static boolean lavarising;
    public static int survivor;
    public static int lava;
    public static String winner;
    public static boolean PvP;
    public static String mode;
    public static Boolean match;

    public static void skipTime() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (lavarising) {
                System.out.println(ChatColor.translateAlternateColorCodes('&', "[KteRising] Cancel Event! Command: Skip"));
            } else {
                player.getWorld().playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.0f);
                seconds = 3;
            }
        }
    }

    public StartGame() {
    }


    public static void start(Plugin plugin) {
        if(match) {
            return;
        }

        lavarising = false;
        time = false;
        match = true;
        lava = plugin.getConfig().getInt("lava-start-block");

        for(Player player : Bukkit.getOnlinePlayers()) {
            player.getInventory().clear();
            player.setHealth(20);
            player.setFoodLevel(20);
            player.setGameMode(GameMode.SURVIVAL);
            String title = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("title.start-game.title")));
            String subtitle = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("title.start-game.sub")));
            player.sendTitle(title, subtitle);
            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 1.0f);
            PvP = false;

            ItemStack netheritePickaxe = new ItemStack(Material.valueOf(ItemsConfig.getConfig().getString("items.pickaxe.material")));
            ItemMeta pickaxeMeta = netheritePickaxe.getItemMeta();
            assert pickaxeMeta != null;
            pickaxeMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', ItemsConfig.getConfig().getString("items.pickaxe.name")));
            netheritePickaxe.setItemMeta(pickaxeMeta);
            netheritePickaxe.addUnsafeEnchantment(Enchantment.DIG_SPEED, 5);

            ItemStack cookedBeef = new ItemStack(Material.valueOf(ItemsConfig.getConfig().getString("items.food.material")), ItemsConfig.getConfig().getInt("items.food.amount"));
            ItemMeta foodMeta = cookedBeef.getItemMeta();
            assert foodMeta != null;
            foodMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', ItemsConfig.getConfig().getString("items.food.name")));
            cookedBeef.setItemMeta(foodMeta);

            ItemStack log = new ItemStack(Material.valueOf(ItemsConfig.getConfig().getString("items.oak.material")), ItemsConfig.getConfig().getInt("items.oak.amount"));

            if("Classic".equals(mode)) {
                seconds = plugin.getConfig().getInt("classic-start-time");
                player.getInventory().addItem(netheritePickaxe, cookedBeef, log);
            } else if ("OP".equals(mode))  {
                seconds = plugin.getConfig().getInt("op-start-time");
                ItemStack diamond = new ItemStack(Material.DIAMOND, 64);
                ItemStack iron = new ItemStack(Material.IRON_INGOT, 64);
                ItemStack stone = new ItemStack(Material.COBBLESTONE, 576);
                player.getInventory().addItem(netheritePickaxe, cookedBeef, diamond, iron, stone, log);
            } else if ("Elytra".equals(mode))  {
                seconds = plugin.getConfig().getInt("classic-start-time");
                ItemStack elytra = new ItemStack(Material.valueOf(ItemsConfig.getConfig().getString("items.elytra.material")), ItemsConfig.getConfig().getInt("items.elytra.amount"));
                ItemMeta elytraMeta = elytra.getItemMeta();
                assert elytraMeta != null;
                elytraMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', ItemsConfig.getConfig().getString("items.elytra.name")));
                elytra.setItemMeta(elytraMeta);
                ItemStack firework = new ItemStack(Material.valueOf(ItemsConfig.getConfig().getString("items.firework.material")), ItemsConfig.getConfig().getInt("items.firework.amount"));
                player.getInventory().addItem(netheritePickaxe, cookedBeef, log, elytra, firework);
            } else if ("ElytraOP".equals(mode))  {
                seconds = plugin.getConfig().getInt("op-start-time");
                ItemStack diamond = new ItemStack(Material.DIAMOND, 64);
                ItemStack iron = new ItemStack(Material.IRON_INGOT, 64);
                ItemStack stone = new ItemStack(Material.COBBLESTONE, 576);
                ItemStack elytra = new ItemStack(Material.valueOf(ItemsConfig.getConfig().getString("items.elytra.material")), ItemsConfig.getConfig().getInt("items.elytra.amount"));
                ItemMeta elytraMeta = elytra.getItemMeta();
                assert elytraMeta != null;
                elytraMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', ItemsConfig.getConfig().getString("items.elytra.name")));
                elytra.setItemMeta(elytraMeta);
                ItemStack firework = new ItemStack(Material.valueOf(ItemsConfig.getConfig().getString("items.firework.material")), ItemsConfig.getConfig().getInt("items.firework.amount"));
                player.getInventory().addItem(netheritePickaxe, cookedBeef, diamond, iron, stone, log, firework, elytra);
            } else if ("Trident".equals(mode))  {
                seconds = plugin.getConfig().getInt("classic-start-time");
                ItemStack tridentRiptide = new ItemStack(Material.valueOf(ItemsConfig.getConfig().getString("items.trident.material")));
                ItemMeta tridentRiptideMeta = tridentRiptide.getItemMeta();
                assert tridentRiptideMeta != null;
                tridentRiptideMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', ItemsConfig.getConfig().getString("items.trident.riptide-name")));
                tridentRiptide.setItemMeta(tridentRiptideMeta);
                tridentRiptide.addUnsafeEnchantment(Enchantment.RIPTIDE, 3);

                ItemStack tridentLoyalty = new ItemStack(Material.valueOf(ItemsConfig.getConfig().getString("items.trident.material")));
                ItemMeta tridentLoyaltyMeta = tridentLoyalty.getItemMeta();
                assert tridentLoyaltyMeta != null;
                tridentLoyaltyMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', ItemsConfig.getConfig().getString("items.trident.loyalty-name")));
                tridentLoyalty.setItemMeta(tridentLoyaltyMeta);
                tridentLoyalty.addUnsafeEnchantment(Enchantment.LOYALTY, 3);

                player.getInventory().addItem(netheritePickaxe, cookedBeef, log, tridentRiptide, tridentLoyalty);
            } else if ("TridentOP".equals(mode))  {
                seconds = plugin.getConfig().getInt("op-start-time");
                ItemStack diamond = new ItemStack(Material.DIAMOND, 64);
                ItemStack iron = new ItemStack(Material.IRON_INGOT, 64);
                ItemStack stone = new ItemStack(Material.COBBLESTONE, 576);
                ItemStack tridentRiptide = new ItemStack(Material.valueOf(ItemsConfig.getConfig().getString("items.trident.material")));
                ItemMeta tridentRiptideMeta = tridentRiptide.getItemMeta();
                assert tridentRiptideMeta != null;
                tridentRiptideMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', ItemsConfig.getConfig().getString("items.trident.riptide-name")));
                tridentRiptide.setItemMeta(tridentRiptideMeta);
                tridentRiptide.addUnsafeEnchantment(Enchantment.RIPTIDE, 3);

                ItemStack tridentLoyalty = new ItemStack(Material.valueOf(ItemsConfig.getConfig().getString("items.trident.material")));
                ItemMeta tridentLoyaltyMeta = tridentLoyalty.getItemMeta();
                assert tridentLoyaltyMeta != null;
                tridentLoyaltyMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', ItemsConfig.getConfig().getString("items.trident.loyalty-name")));
                tridentLoyalty.setItemMeta(tridentLoyaltyMeta);
                tridentLoyalty.addUnsafeEnchantment(Enchantment.LOYALTY, 3);

                player.getInventory().addItem(netheritePickaxe, cookedBeef, diamond, iron, stone, log, tridentRiptide, tridentLoyalty);
            }
        }


        new BukkitRunnable() {
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
                        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.0f);
                        lavarising = true;

                    }
                    upLava(plugin);
                }
            }
        }.runTaskTimer(plugin, 20L, 20L);
    }

    public static void live(Plugin plugin) {
        Bukkit.getServer().getConsoleSender();
        survivor = 0;
        for (Player player : Bukkit.getOnlinePlayers()) {
            if(player.getGameMode() == GameMode.SURVIVAL) {
                survivor++;
            }
        }

        if(survivor == 1) {
            for(Player player : Bukkit.getOnlinePlayers()) {

                if(player.getGameMode() == GameMode.SURVIVAL) {
                    winner = player.getName();
                }
                String title = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("title.finish-game.title")));
                if (winner != null) {
                    title = title.replace("%winner%", winner);
                }                String subtitle = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("title.finish-game.sub")).replace("%winner%",winner));
                player.getWorld().playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_TWINKLE, 1.0f, 1.0f);
                player.sendTitle(title, subtitle);

                if(plugin.getConfig().getBoolean("server-stop")) {
                    BukkitScheduler scheduler = Bukkit.getScheduler();
                    scheduler.runTaskLater(plugin, () -> plugin.getServer().shutdown(), 200L);
                }
            }

        }
    }

    public static void upLava(Plugin plugin) {
        World world = Bukkit.getWorld("world");
        assert world != null;
        WorldBorder worldBorder = world.getWorldBorder();
        int minX = (int) (worldBorder.getCenter().getX() - worldBorder.getSize() / 2);
        int minZ = (int) (worldBorder.getCenter().getZ() - worldBorder.getSize() / 2);
        int maxX = minX + (int) worldBorder.getSize();
        int maxZ = minZ + (int) worldBorder.getSize();


        new BukkitRunnable() {

            @Override
            public void run() {
                if (!lavarising) {
                    return;
                }
                for (int x = minX; x <= maxX; x++) {
                    for (int z = minZ; z <= maxZ; z++) {
                        Location location = new Location(world, x, lava, z);
                        Block block = location.getBlock();
                        if (block.getType() == Material.AIR || block.getType() == Material.WATER || block.getType() == Material.CAVE_AIR) {
                            block.setType(Material.LAVA);
                        }

                    }
                }
                if (lava == plugin.getConfig().getInt("pvp-allow")) {
                    if (!PvP) {
                        PvP = true;
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            String title = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("title.pvp-allow.title")));
                            String subtitle = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("title.pvp-allow.sub")));
                            player.getWorld().playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.0f);
                            player.sendTitle(title, subtitle);
                        }
                    }
                }
                if (lava == 255) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        if (player.getLocation().getY() <= 253) {
                            player.setHealth(0);
                        }
                    }
                }

                if (lava == 255) {
                    worldBorder.setSize(3, 180);
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        String title = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("title.worldborder-shrink.title")));
                        String subtitle = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("title.worldborder-shrink.sub")));
                        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.0f);
                        player.sendTitle(title, subtitle);
                    }
                }

                if (lava < 256) {
                    lava++;
                }


            }
        }.runTaskTimer(plugin, 0L, 20L * plugin.getConfig().getInt("lava-delay"));
    }
}
