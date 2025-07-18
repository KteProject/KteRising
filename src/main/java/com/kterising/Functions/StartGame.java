package com.kterising.Functions;

import com.kterising.Command.Command;
import com.kterising.Listeners.AutoStart;
import com.kterising.Team.Team;
import com.kterising.Team.TeamManager;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.stream.Collectors;

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
    private static BukkitRunnable task2;
    private static BukkitRunnable task3;
    private static BukkitRunnable task4;


    public static HashMap<UUID, Location> leavedPlayers = new HashMap<UUID, Location>();


    public static Sound getSoundFromConfig(Plugin plugin, String path) {
        return Sound.valueOf(plugin.getConfig().getString(path));
    }

    private static World getConfiguredWorld(Plugin plugin) {
        String worldName = plugin.getConfig().getString("world-name", "world");
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            plugin.getLogger().warning(ChatColor.RED + "❌ World '" + worldName + "' not found. Please check your config.yml!");
        }
        return world;
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

    public static void giveItemsFromConfig(Player player) {
        List<Map<?, ?>> items = (List<Map<?, ?>>) ItemsConfig.getConfig().get("modes." + mode + ".items");

        for (Map<?, ?> itemData : items) {
            try {
                Material material = Material.valueOf((String) itemData.get("material"));
                int amount = itemData.containsKey("amount") ? (int) itemData.get("amount") : 1;

                ItemStack itemStack = new ItemStack(material, amount);

                if (itemData.containsKey("name")) {
                    ItemMeta meta = itemStack.getItemMeta();
                    if (meta != null) {
                        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', (String) itemData.get("name")));
                        itemStack.setItemMeta(meta);
                    }
                }

                if (itemData.containsKey("enchantments")) {
                    Map<String, Integer> enchantments = (Map<String, Integer>) itemData.get("enchantments");
                    for (String key : enchantments.keySet()) {
                        Enchantment enchantment = Enchantment.getByName(key);
                        if (enchantment != null) {
                            itemStack.addUnsafeEnchantment(enchantment, enchantments.get(key));
                        }
                    }
                }

                player.getInventory().addItem(itemStack);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void start(final Plugin plugin) {
        if (AutoStart.autostartTask != null) {
            AutoStart.autostartTask.cancel();
            AutoStart.autostartTask = null;
        }

        if (match)
            return;

        lavarising = false;
        time = false;
        match = true;
        lava = plugin.getConfig().getInt("lava-start-block");
        if ("Classic".equals(mode) || "Elytra".equals(mode) || "Trident".equals(mode)) {
            seconds = plugin.getConfig().getInt("classic-start-time");
        } else if ("OP".equals(mode)|| "UltraOP".equals(mode) || "ElytraOP".equals(mode) || "TridentOP".equals(mode)) {
            seconds = plugin.getConfig().getInt("op-start-time");
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.getInventory().clear();
            player.setHealth(20.0D);
            player.setFoodLevel(20);
            player.setGameMode(GameMode.SURVIVAL);
            giveItemsFromConfig(player);

            String title = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("title.start-game.title")));
            String subtitle = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("title.start-game.sub")));
            player.sendTitle(title, subtitle);
            player.getWorld().playSound(player.getLocation(), getSoundFromConfig(plugin, "sound.start-sound"), 1.0F, 1.0F);
            PvP = false;
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

        if (survivor == 1 || (survivor >= 2 && winnerTeam != null)) {
            String winner;
            if (survivor == 1) {
                assert winnerPlayer != null;
                winner = winnerPlayer.getName();
                if (!win) {
                    PlayerStats.addWin(winnerPlayer);
                    win = true;
                }
            } else {
                assert winnerTeam != null;
                winner = winnerTeam.getPlayers().stream()
                        .map(Player::getName)
                        .collect(Collectors.joining(", "));

                if (!win) {
                    for (Player player : winnerTeam.getPlayers()) {
                        PlayerStats.addWin(player);
                        Rewards.winPlayer(player);
                    }
                    win = true;
                }
            }

            for (Player player : Bukkit.getOnlinePlayers()) {
                String title = ChatColor.translateAlternateColorCodes('&',
                        Objects.requireNonNull(MessagesConfig.get().getString("title.finish-game.title")));
                title = title.replace("%winner%", winner);
                String subtitle = ChatColor.translateAlternateColorCodes('&',
                        Objects.requireNonNull(MessagesConfig.get().getString("title.finish-game.sub")).replace("%winner%", winner));

                player.sendTitle(title, subtitle);

                player.getWorld().playSound(player.getLocation(),
                        getSoundFromConfig(plugin, "sound.winner-sound"), 1.0f, 1.0f);
            }

            if (!end) {
                Bukkit.getScheduler().runTaskLater(plugin, () -> resetWorldBorder(plugin), 100L);
                end = true;
            }
        }

        if (survivor == 0) {
            if (!end) {
                Bukkit.getScheduler().runTaskLater(plugin, () -> resetWorldBorder(plugin), 100L);
                end = true;
            }
        }
    }


    public static int centerX = 0;
    public static int centerZ = 0;

    public static void resetWorldBorder(Plugin plugin) {
        if (task != null) { task.cancel(); task = null; }
        if (task2 != null) { task2.cancel(); task2 = null; }
        if (task3 != null) { task3.cancel(); task3 = null; }
        if (task4 != null) { task4.cancel(); task4 = null; }

        AutoStart.stopCountdown();
        lavarising = false;
        match = false;
        PvP = false;
        survivor = 0;
        seconds = 0;
        winner = null;
        win = false;
        end = false;
        leavedPlayers.clear();
        lava = plugin.getConfig().getInt("lava-start-block");
        Command.selectedmode = false;
        modManager.resetVotes();

        centerX += 2000;
        centerZ += 2000;

        World world = getConfiguredWorld(plugin);
        if (world == null) return;

        WorldBorder border = world.getWorldBorder();
        border.setSize(border.getSize(), 0L);
        border.setCenter(centerX, centerZ);
        world.setSpawnLocation(centerX, 100, centerZ);

        task3 = new BukkitRunnable() {
            @Override
            public void run() {
                border.setSize(plugin.getConfig().getInt("world-size"));
                border.setDamageAmount(5);
                border.setDamageBuffer(2);
            }
        };
        task3.runTaskLater(plugin, 20L);

        Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : onlinePlayers) {
                    player.getInventory().clear();
                    player.setGameMode(GameMode.ADVENTURE);
                    player.setHealth(20);
                    player.setFoodLevel(20);
                    player.setSaturation(20f);
                    player.updateInventory();
                    TeamManager.removePlayerFromTeam(player);
                }
            }
        }.runTaskLater(plugin, 20L);

        task4 = new BukkitRunnable() {
            @Override
            public void run() {
                Chunk centerChunk = world.getChunkAt(centerX >> 4, centerZ >> 4);
                if (!centerChunk.isLoaded()) centerChunk.load(true);

                for (Player player : onlinePlayers) {
                    Location safeSpawn = findSafeLocation(world, centerX, centerZ);
                    player.teleport(safeSpawn);
                    player.setBedSpawnLocation(safeSpawn, true);

                    SpecialItems.giveSpecialItems(player);
                    TeamManager.assignPlayerToTeam(player);
                }
            }
        };
        task4.runTaskLater(plugin, 40L);

        if (plugin.getConfig().getBoolean("water-to-ice.enabled")) {
            transformWaterToIce(plugin, world, centerX, centerZ);
        }

        if (plugin.getConfig().getBoolean("player-start")) {
            if (onlinePlayers.size() >= plugin.getConfig().getInt("player-start-count")) {
                if (AutoStart.autostartTask == null) {
                    AutoStart.startCountdown(plugin);
                }
            }
        }
    }

    private static Location findSafeLocation(World world, int centerX, int centerZ) {
        for (int y = 60; y < 100; y++) {
            Location loc = new Location(world, centerX + 0.5, y, centerZ + 0.5);
            if (world.getBlockAt(loc).getType() == Material.AIR &&
                    world.getBlockAt(loc.clone().add(0, 1, 0)).getType() == Material.AIR) {
                return loc;
            }
        }
        return new Location(world, centerX + 0.5, 100, centerZ + 0.5);
    }

    private static void transformWaterToIce(Plugin plugin, World world, int centerX, int centerZ) {
        int size = plugin.getConfig().getInt("world-size");
        int minX = centerX - size / 2;
        int minZ = centerZ - size / 2;
        int maxX = centerX + size / 2;
        int maxZ = centerZ + size / 2;
        int lowY = plugin.getConfig().getInt("water-to-ice.low-y");
        int highY = plugin.getConfig().getInt("water-to-ice.high-y");

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            Set<Chunk> loadedChunks = new HashSet<>();
            for (int x = minX; x < maxX; x++) {
                for (int z = minZ; z < maxZ; z++) {
                    Chunk chunk = world.getChunkAt(x >> 4, z >> 4);
                    if (!chunk.isLoaded()) {
                        chunk.load();
                        loadedChunks.add(chunk);
                    }

                    for (int y = lowY; y <= highY; y++) {
                        Block block = world.getBlockAt(x, y, z);
                        if (block.getType() == Material.WATER) {
                            block.setType(Material.ICE);
                        }
                    }
                }
            }

            for (Chunk chunk : loadedChunks) {
                if (chunk.isLoaded()) chunk.unload(true);
            }
        }, 40L);
    }



    public static void upLava(final Plugin plugin) {
        if (task2 != null && !task2.isCancelled()) {
            task2.cancel();
        }

        World world = getConfiguredWorld(plugin);
        if (world == null) return;

        final WorldBorder worldBorder = world.getWorldBorder();
        final int minX = (int) (worldBorder.getCenter().getX() - worldBorder.getSize() / 2.0D);
        final int minZ = (int) (worldBorder.getCenter().getZ() - worldBorder.getSize() / 2.0D);
        final int maxX = minX + (int) worldBorder.getSize();
        final int maxZ = minZ + (int) worldBorder.getSize();

        task2 = new BukkitRunnable() {
            public void run() {
                if (!StartGame.lavarising) return;

                int currentLavaLevel = StartGame.lava;

                for (int cx = minX >> 4; cx <= maxX >> 4; cx++) {
                    for (int cz = minZ >> 4; cz <= maxZ >> 4; cz++) {
                        Chunk chunk = world.getChunkAt(cx, cz);
                        if (!chunk.isLoaded()) chunk.load();

                        for (int x = cx << 4; x < (cx << 4) + 16; x++) {
                            for (int z = cz << 4; z < (cz << 4) + 16; z++) {
                                for (int y = Math.max(0, currentLavaLevel - 1); y <= currentLavaLevel; y++) {
                                    Block block = world.getBlockAt(x, y, z);
                                    Material type = block.getType();

                                    if (type == Material.AIR || type == Material.WATER || type == Material.CAVE_AIR) {
                                        block.setType(Material.LAVA, false);
                                    }
                                }
                            }
                        }
                    }
                }

                if (StartGame.lava == plugin.getConfig().getInt("pvp-allow") && !StartGame.PvP) {
                    StartGame.PvP = true;
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        String title = ChatColor.translateAlternateColorCodes('&',
                                Objects.requireNonNull(MessagesConfig.get().getString("title.pvp-allow.title")));
                        String subtitle = ChatColor.translateAlternateColorCodes('&',
                                Objects.requireNonNull(MessagesConfig.get().getString("title.pvp-allow.sub")));
                        player.getWorld().playSound(player.getLocation(),
                                StartGame.getSoundFromConfig(plugin, "sound.pvp-sound"), 1.0F, 1.0F);
                        player.sendTitle(title, subtitle);
                    }
                }

                if (StartGame.lava == plugin.getConfig().getInt("lava-border")) {
                    int borderKillLevel = plugin.getConfig().getInt("border-kill");
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        if (player.getLocation().getY() <= borderKillLevel) {
                            player.setHealth(0.0D);
                        }
                    }

                    StartGame.live(plugin);
                    worldBorder.setSize(3.0D, 180L);

                    for (Player player : Bukkit.getOnlinePlayers()) {
                        String title = ChatColor.translateAlternateColorCodes('&',
                                Objects.requireNonNull(MessagesConfig.get().getString("title.worldborder-shrink.title")));
                        String subtitle = ChatColor.translateAlternateColorCodes('&',
                                Objects.requireNonNull(MessagesConfig.get().getString("title.worldborder-shrink.sub")));
                        player.getWorld().playSound(player.getLocation(),
                                StartGame.getSoundFromConfig(plugin, "sound.shrink-sound"), 1.0F, 1.0F);
                        player.sendTitle(title, subtitle);
                    }

                    this.cancel();
                }

                if (StartGame.lava < plugin.getConfig().getInt("lava-border")) {
                    StartGame.lava++;
                }
            }
        };task2.runTaskTimer(plugin, 0L, 20L * plugin.getConfig().getInt("lava-delay"));
    }
}
