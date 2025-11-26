package com.kteproject.kterising.game;

import com.kteproject.kterising.KteRising;
import com.kteproject.kterising.managers.RewardsManager;
import com.kteproject.kterising.managers.gamemodes.ModeItem;
import com.kteproject.kterising.managers.gamemodes.ModeManager;
import com.kteproject.kterising.stats.PlayerStats;
import com.kteproject.kterising.stats.StatsCache;
import com.kteproject.kterising.utils.ChatUtil;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;
import java.util.Map;

public class Game {
    public static boolean match = false;
    public static int seconds = 0;
    public static String mode = null;
    public static boolean lavarising = false;
    public static int lava = 0;
    public static int lives;
    public static boolean pvp = false;
    public static World world;
    public static boolean teamMode = false;
    public static boolean time;
    private static int countdown;
    public static String placeholderlabel;
    private static boolean end = false;
    private static String winner = null;
    public static boolean lavaFrozen = false;

    public static int minX;
    public static int minZ;
    public static int maxX;
    public static int maxZ;

    public static void init() {

        String worldName = KteRising.getConfiguration().getString("world-configurations.world-name");
        world = Bukkit.getWorld(worldName);

        if (world == null) {
            Bukkit.getLogger().warning("[KteRising] World '" + worldName + "' not found. Using default world.");
            world = Bukkit.getWorlds().get(0);
        }

        Location safe = KteRising.getSpawnLocation();

        WorldBorder wb = world.getWorldBorder();
        double size = KteRising.getConfiguration().getDouble("world-configurations.world-border");

        wb.setCenter(safe.getX(), safe.getZ());
        wb.setSize(size);
        wb.setDamageAmount(5);
        wb.setDamageBuffer(2);

        match = false;
        seconds = 0;

        mode = ChatUtil.getText("placeholderapi.mode-not-selected");
        placeholderlabel = mode;

        lavarising = false;
        pvp = false;
        teamMode = false;
        time = false;

        lava = KteRising.getConfiguration().getInt("game-configurations.lava-start-height");
        lives = 0;

        double half = wb.getSize() / 2.0;

        minX = (int) Math.floor(wb.getCenter().getX() - half);
        maxX = (int) Math.ceil(wb.getCenter().getX() + half);

        minZ = (int) Math.floor(wb.getCenter().getZ() - half);
        maxZ = (int) Math.ceil(wb.getCenter().getZ() + half);

        Bukkit.getLogger().info(
                "[KteRising] WorldBorder initialized at safe biome location: "
                        + safe.getX() + ", " + safe.getZ()
                        + " | Size: " + size
        );
    }




    public static void checkLive() {
        lives = 0;
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getGameMode() == GameMode.SURVIVAL) {
                lives++;
            }
        }
        checkWin();
    }

    public static void checkWin() {
        if (!teamMode) {
            if (lives == 1) {
                if (!end) {
                    winner = getWinner();
                    if (winner == null) winner = "Unknown";
                    end = true;
                }
                for (Player player : Bukkit.getOnlinePlayers()) {
                    ChatUtil.sendTitle(
                            player,
                            "titles.finish-game.title",
                            "titles.finish-game.subtitle",
                            5, 200, 5,
                            Map.of("winner", winner)
                    );

                    KteRising.getInstance().getServer().getGlobalRegionScheduler().runDelayed(
                            KteRising.getInstance(),
                            (ScheduledTask task) -> {
                                KteRising.getInstance().getServer().shutdown();
                            },
                            200L
                    );
                }
            }
        }
    }

    public static String getWinner() {
        if (!teamMode) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.getGameMode() == GameMode.SURVIVAL) {

                    PlayerStats stats = StatsCache.get(player.getUniqueId());
                    if (stats != null) {
                        stats.wins++;
                    }

                    RewardsManager.winPlayer(player);
                    return player.getName();
                }
            }
        }
        return null;
    }



    public static void startGame() {
        if (match) return;
        match = true;
        mode = KteRising.getInstance().getVoteManager().getWinningMode();
        checkLive();

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.getInventory().clear();
            player.setGameMode(GameMode.SURVIVAL);
            player.setHealth(20);
            player.setFoodLevel(20);
            player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0, true, false));

            PlayerStats stats = StatsCache.get(player.getUniqueId());
            if (stats != null) {
                stats.gamesPlayed++;
            }

            giveItems(player);

            ChatUtil.sendTitle(
                    player,
                    "titles.start-game.title",
                    "titles.start-game.subtitle",
                    5, 60, 5,
                    Map.of()
            );

        }

        ModeManager.getMode(mode).ifPresent(modeData -> {countdown = modeData.getCountdown(); placeholderlabel = modeData.getPlaceholderlabel();});

        LavaTask.startTime(countdown);
    }

    private static void giveItems(Player player) {
        ModeManager.getMode(mode).ifPresent(modeData -> {
            Registry<Enchantment> enchantmentRegistry = Bukkit.getRegistry(Enchantment.class);

            for (ModeItem mi : modeData.getItems()) {
                Material mat = mi.getMaterial();

                ItemStack item = new ItemStack(mat, mi.getAmount());
                ItemMeta meta = item.getItemMeta();

                if (meta != null) {
                    if (!mi.getName().isEmpty()) {
                        meta.displayName(MiniMessage.miniMessage().deserialize("<!i>" + mi.getName()));
                    }

                    meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                    meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

                    if (!mi.getLore().isEmpty()) {
                        List<Component> finalLore = mi.getLore().stream()
                                .map(line -> {
                                    String processedLine = "<!i>" + line;

                                    return MiniMessage.miniMessage().deserialize(processedLine);
                                })
                                .toList();

                        meta.lore(finalLore);
                    }

                    item.setItemMeta(meta);

                    for (Map.Entry<String, Integer> e : mi.getEnchantments().entrySet()) {
                        String enchantmentKey = e.getKey().toLowerCase();
                        Enchantment ench = enchantmentRegistry.get(NamespacedKey.minecraft(enchantmentKey));

                        if (ench != null) {
                            item.addUnsafeEnchantment(ench, e.getValue());
                        }
                    }
                }

                player.getInventory().addItem(item);
            }
        });
    }

    public static final NamespacedKey KEY(String id) {
        return new NamespacedKey(KteRising.getInstance(), id);
    }

    public static final PersistentDataType<String, String> STRING_TAG = PersistentDataType.STRING;

}


