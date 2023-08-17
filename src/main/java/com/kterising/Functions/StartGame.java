package com.kterising.Functions;

import com.kterising.KteRising;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

public class StartGame {
    private final KteRising plugin;
    private static BukkitTask countdownTask;
    public static int seconds;
    public static boolean time;
    public static boolean lavarising;
    public static int survivor;
    public static int lava;
    public static String winner;
    public static boolean PvP;
    public static String mode;
    public static Boolean match;


    public StartGame(KteRising plugin) {
        this.plugin = plugin;
    }

    public static void start(Plugin plugin) {
        lavarising = false;
        time = false;
        match = true;
        lava = plugin.getConfig().getInt("lava-start-block");

        for(Player player : Bukkit.getOnlinePlayers()) {
            player.getInventory().clear();
            player.setHealth(20);
            player.setFoodLevel(20);
            player.teleport(new Location(player.getWorld(),0,128,0));
            player.setGameMode(GameMode.SURVIVAL);
            String title = ChatColor.translateAlternateColorCodes('&', MessagesConfig.get().getString("start-game-title"));
            String subtitle = ChatColor.translateAlternateColorCodes('&', MessagesConfig.get().getString("start-game-sub"));
            player.sendTitle(title,subtitle);
            PvP = false;
            if(mode == "Classic") {
                seconds = plugin.getConfig().getInt("classic-start-time");
                ItemStack netheritePickaxe = new ItemStack(Material.NETHERITE_PICKAXE);
                ItemMeta itemMeta = netheritePickaxe.getItemMeta();
                itemMeta.setDisplayName(ChatColor.GOLD + "Lava Rising");
                netheritePickaxe.setItemMeta(itemMeta);
                netheritePickaxe.addUnsafeEnchantment(Enchantment.DIG_SPEED, 5);
                ItemStack cookedBeef = new ItemStack(Material.COOKED_BEEF, 16);
                ItemMeta itemMeta2 = cookedBeef.getItemMeta();
                itemMeta2.setDisplayName(ChatColor.GOLD + "Lava Rising");
                ItemStack log = new ItemStack(Material.OAK_LOG, 16);
                player.getInventory().addItem(netheritePickaxe,cookedBeef,log);
            } else if (mode == "OP") {
                seconds = plugin.getConfig().getInt("op-start-time");
                ItemStack netheritePickaxe = new ItemStack(Material.NETHERITE_PICKAXE);
                ItemMeta itemMeta = netheritePickaxe.getItemMeta();
                itemMeta.setDisplayName(ChatColor.GOLD + "Lava Rising");
                netheritePickaxe.setItemMeta(itemMeta);
                netheritePickaxe.addUnsafeEnchantment(Enchantment.DIG_SPEED, 5);
                ItemStack cookedBeef = new ItemStack(Material.COOKED_BEEF, 64);
                ItemMeta itemMeta2 = cookedBeef.getItemMeta();
                itemMeta2.setDisplayName(ChatColor.GOLD + "Lava Rising");
                ItemStack diamond = new ItemStack(Material.DIAMOND, 64);
                ItemStack iron = new ItemStack(Material.IRON_INGOT, 64);
                ItemStack stone = new ItemStack(Material.COBBLESTONE, 576);
                ItemStack log = new ItemStack(Material.OAK_LOG, 64);
                player.getInventory().addItem(netheritePickaxe,cookedBeef,diamond,iron,stone,log);
            } else if (mode == "Elytra") {
                seconds = plugin.getConfig().getInt("classic-start-time");
                ItemStack netheritePickaxe = new ItemStack(Material.NETHERITE_PICKAXE);
                ItemMeta itemMeta = netheritePickaxe.getItemMeta();
                itemMeta.setDisplayName(ChatColor.GOLD + "Lava Rising");
                netheritePickaxe.setItemMeta(itemMeta);
                netheritePickaxe.addUnsafeEnchantment(Enchantment.DIG_SPEED, 5);
                ItemStack cookedBeef = new ItemStack(Material.COOKED_BEEF, 16);
                ItemMeta itemMeta2 = cookedBeef.getItemMeta();
                itemMeta2.setDisplayName(ChatColor.GOLD + "Lava Rising");
                ItemStack log = new ItemStack(Material.OAK_LOG, 16);
                ItemStack elytra = new ItemStack(Material.ELYTRA, 1);
                ItemMeta itemMeta3 = elytra.getItemMeta();
                itemMeta3.setDisplayName(ChatColor.GOLD + "Lava Rising");
                ItemStack firework = new ItemStack(Material.FIREWORK_ROCKET, 6);
                player.getInventory().addItem(netheritePickaxe,cookedBeef,log,elytra,firework);
            } else if (mode ==  "ElytraOP") {
                seconds = plugin.getConfig().getInt("op-start-time");
                ItemStack netheritePickaxe = new ItemStack(Material.NETHERITE_PICKAXE);
                ItemMeta itemMeta = netheritePickaxe.getItemMeta();
                itemMeta.setDisplayName(ChatColor.GOLD + "Lava Rising");
                netheritePickaxe.setItemMeta(itemMeta);
                netheritePickaxe.addUnsafeEnchantment(Enchantment.DIG_SPEED, 5);
                ItemStack cookedBeef = new ItemStack(Material.COOKED_BEEF, 64);
                ItemMeta itemMeta2 = cookedBeef.getItemMeta();
                itemMeta2.setDisplayName(ChatColor.GOLD + "Lava Rising");
                ItemStack diamond = new ItemStack(Material.DIAMOND, 64);
                ItemStack iron = new ItemStack(Material.IRON_INGOT, 64);
                ItemStack stone = new ItemStack(Material.COBBLESTONE, 576);
                ItemStack log = new ItemStack(Material.OAK_LOG, 64);
                ItemStack elytra = new ItemStack(Material.ELYTRA, 1);
                ItemMeta itemMeta3 = elytra.getItemMeta();
                itemMeta3.setDisplayName(ChatColor.GOLD + "Lava Rising");
                ItemStack firework = new ItemStack(Material.FIREWORK_ROCKET, 16);
                player.getInventory().addItem(netheritePickaxe,cookedBeef,diamond,iron,stone,log,firework,elytra);
            } else if (mode == "Trident") {
                seconds = plugin.getConfig().getInt("classic-start-time");
                ItemStack netheritePickaxe = new ItemStack(Material.NETHERITE_PICKAXE);
                ItemMeta itemMeta = netheritePickaxe.getItemMeta();
                itemMeta.setDisplayName(ChatColor.GOLD + "Lava Rising");
                netheritePickaxe.setItemMeta(itemMeta);
                netheritePickaxe.addUnsafeEnchantment(Enchantment.DIG_SPEED, 5);
                ItemStack cookedBeef = new ItemStack(Material.COOKED_BEEF, 16);
                ItemMeta itemMeta2 = cookedBeef.getItemMeta();
                itemMeta2.setDisplayName(ChatColor.GOLD + "Lava Rising");
                ItemStack log = new ItemStack(Material.OAK_LOG, 16);
                ItemStack trident = new ItemStack(Material.TRIDENT);
                ItemMeta itemMeta3 = trident.getItemMeta();
                itemMeta.setDisplayName(ChatColor.BLUE + "Riptide");
                trident.setItemMeta(itemMeta);
                trident.addUnsafeEnchantment(Enchantment.RIPTIDE, 3);
                ItemStack tridentt = new ItemStack(Material.TRIDENT);
                ItemMeta itemMeta4 = trident.getItemMeta();
                itemMeta.setDisplayName(ChatColor.BLUE + "Loyalty");
                tridentt.setItemMeta(itemMeta);
                tridentt.addUnsafeEnchantment(Enchantment.LOYALTY, 3);
                player.getInventory().addItem(netheritePickaxe,cookedBeef,log, trident, tridentt);
            } else if (mode == "TridentOP") {
                seconds = plugin.getConfig().getInt("op-start-time");
                ItemStack netheritePickaxe = new ItemStack(Material.NETHERITE_PICKAXE);
                ItemMeta itemMeta = netheritePickaxe.getItemMeta();
                itemMeta.setDisplayName(ChatColor.GOLD + "Lava Rising");
                netheritePickaxe.setItemMeta(itemMeta);
                netheritePickaxe.addUnsafeEnchantment(Enchantment.DIG_SPEED, 5);
                ItemStack cookedBeef = new ItemStack(Material.COOKED_BEEF, 64);
                ItemMeta itemMeta2 = cookedBeef.getItemMeta();
                itemMeta2.setDisplayName(ChatColor.GOLD + "Lava Rising");
                ItemStack diamond = new ItemStack(Material.DIAMOND, 64);
                ItemStack iron = new ItemStack(Material.IRON_INGOT, 64);
                ItemStack stone = new ItemStack(Material.COBBLESTONE, 576);
                ItemStack log = new ItemStack(Material.OAK_LOG, 64);
                ItemStack trident = new ItemStack(Material.TRIDENT);
                ItemMeta itemMeta3 = trident.getItemMeta();
                itemMeta.setDisplayName(ChatColor.BLUE + "Riptide");
                trident.setItemMeta(itemMeta);
                trident.addUnsafeEnchantment(Enchantment.RIPTIDE, 3);
                ItemStack tridentt = new ItemStack(Material.TRIDENT);
                ItemMeta itemMeta4 = trident.getItemMeta();
                itemMeta.setDisplayName(ChatColor.BLUE + "Loyalty");
                tridentt.setItemMeta(itemMeta);
                tridentt.addUnsafeEnchantment(Enchantment.LOYALTY, 3);
                player.getInventory().addItem(netheritePickaxe,cookedBeef,diamond,iron,stone,log,trident,tridentt);
            }


        }

        countdownTask = new BukkitRunnable() {
            @Override
            public void run() {
                live(plugin);

                if(time) {
                    seconds++;
                } else {
                    seconds--;
                }
                if(seconds == 0) {
                    time = true;
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        String title = ChatColor.translateAlternateColorCodes('&', MessagesConfig.get().getString("lava-starting-title"));
                        String subtitle = ChatColor.translateAlternateColorCodes('&', MessagesConfig.get().getString("lava-starting-sub"));
                        player.sendTitle(title,subtitle);
                        lavarising = true;

                    }
                    upLava(plugin);
                }
            }
        }.runTaskTimer(plugin, 20L, 20L);
    }

    public static void live(Plugin plugin) {
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
                    Firework firework = player.getWorld().spawn(player.getLocation(),Firework.class);
                    FireworkMeta meta = (FireworkMeta) firework.getFireworkMeta();
                    meta.addEffect(FireworkEffect.builder().flicker(true).trail(true).withColor(Color.RED).withFade(Color.ORANGE).build());
                    meta.setPower(5);
                    firework.setFireworkMeta(meta);
                }
                String title = ChatColor.translateAlternateColorCodes('&', MessagesConfig.get().getString("finish-game-title").replace("%winner%",winner));
                String subtitle = ChatColor.translateAlternateColorCodes('&', MessagesConfig.get().getString("finish-game-sub").replace("%winner%",winner));

                int fadeIn = 1;
                int stay = 200;
                int fadeOut = 1;
                player.sendTitle(title, subtitle, fadeIn, stay, fadeOut);


                if(plugin.getConfig().getBoolean("server-stop")) {
                    BukkitScheduler scheduler = Bukkit.getScheduler();
                    scheduler.runTaskLater(plugin, () -> {
                        plugin.getServer().shutdown();
                    }, 200L);
                }
            }

        }
    }

    public static void upLava(Plugin plugin) {
        World world = Bukkit.getWorld("world");
        WorldBorder worldBorder = world.getWorldBorder();
        int minX = (int) (worldBorder.getCenter().getX() - worldBorder.getSize() / 2);
        int minZ = (int) (worldBorder.getCenter().getZ() - worldBorder.getSize() / 2);
        int maxX = minX + (int) worldBorder.getSize();
        int maxZ = minZ + (int) worldBorder.getSize();



        BukkitTask countdownTask = new BukkitRunnable() {

            @Override
            public void run() {

                for (int x = minX; x <= maxX; x++) {
                    for (int z = minZ; z <= maxZ; z++) {
                        Location location = new Location(world, x, lava, z);
                        Block block = location.getBlock();
                        if(block.getType() == Material.AIR || block.getType() == Material.WATER) {
                            block.setType(Material.LAVA);
                        }

                    }
                }
                if(lava == plugin.getConfig().getInt("pvp-allow")) {
                    if(PvP == false) {
                        PvP = true;
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            String title = ChatColor.translateAlternateColorCodes('&', MessagesConfig.get().getString("pvp-allow-title"));
                            String subtitle = ChatColor.translateAlternateColorCodes('&', MessagesConfig.get().getString("pvp-allow-sub"));
                            player.sendTitle(title, subtitle);
                        }
                    }
                }
                if(lava == 255) {
                    worldBorder.setSize(3,180);
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        String title = ChatColor.translateAlternateColorCodes('&', MessagesConfig.get().getString("worldborder-shrink-title"));
                        String subtitle = ChatColor.translateAlternateColorCodes('&', MessagesConfig.get().getString("worldborder-shrink-sub"));
                        player.sendTitle(title, subtitle);
                    }
                }

                if (lava < 256) {
                    lava++;
                }


            }
        }.runTaskTimer(plugin, 0L, 20 * plugin.getConfig().getInt("lava-delay"));
    }
}
