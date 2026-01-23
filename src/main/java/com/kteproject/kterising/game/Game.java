package com.kteproject.kterising.game;

import com.kteproject.kterising.KteRising;
import com.kteproject.kterising.managers.LobbyItems;
import com.kteproject.kterising.managers.RewardsManager;
import com.kteproject.kterising.managers.SafeBiomeManager;
import com.kteproject.kterising.managers.gamemodes.ModeItem;
import com.kteproject.kterising.managers.gamemodes.ModeManager;
import com.kteproject.kterising.managers.vote.VoteManager;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Game {
    public static boolean match = false;
    public static int seconds = 0;
    public static String mode = null;
    public static boolean lavarising = false;
    public static int lava = 0;
    public static int lives;
    public static boolean pvp = false;
    public static World world;
    public static boolean time;
    private static int countdown;
    public static String placeholderlabel;
    public static boolean end = false;
    public static boolean firstend = false;
    private static String winner = null;
    public static boolean lavaFrozen = false;
    public static List<UUID> joinedBefore = new ArrayList<>();

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

        WorldBorder wb = world.getWorldBorder();
        double size = KteRising.getConfiguration().getDouble("world-configurations.world-border");

        if (end){
            int x = (int) wb.getCenter().getX()+1000;
            int z = (int) wb.getCenter().getZ()+1000;
            SafeBiomeManager.findSafeLocation(x,z);
            KteRising.setSpawnLocation(SafeBiomeManager.getSafeLocation());
            wb.setCenter(SafeBiomeManager.getSafeLocation());
            wb.setSize(size);
            VoteManager.resetVotes();
        } else {
            SafeBiomeManager.findSafeLocation(0,0);
            KteRising.setSpawnLocation(SafeBiomeManager.getSafeLocation());
            wb.setCenter(KteRising.getSpawnLocation().getX(), KteRising.getSpawnLocation().getZ());
            wb.setSize(size);
            wb.setDamageAmount(5);
            wb.setDamageBuffer(2);
        }

        match = false;
        seconds = 0;

        LavaTask.Mtime = KteRising.getConfiguration().getInt("game-configurations.deathmatch-duration");
        LavaTask.DM = false;
        mode = ChatUtil.getText("placeholderapi.mode-not-selected");
        placeholderlabel = mode;

        lavarising = false;
        pvp = false;
        time = false;
        firstend = false;

        lava = KteRising.getConfiguration().getInt("game-configurations.lava-start-height");
        lives = 0;

        double half = wb.getSize() / 2.0;

        minX = (int) Math.floor(wb.getCenter().getX() - half);
        maxX = (int) Math.ceil(wb.getCenter().getX() + half);

        minZ = (int) Math.floor(wb.getCenter().getZ() - half);
        maxZ = (int) Math.ceil(wb.getCenter().getZ() + half);

        joinedBefore.clear();

        LavaTask.resetState();

        if (end){
            KteRising.getInstance().getServer().getGlobalRegionScheduler().runDelayed(
                    KteRising.getInstance(),
                    (ScheduledTask task) -> {
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            LobbyItems.giveLobbyItem(player);
                            player.setGameMode(GameMode.SURVIVAL);
                            player.teleportAsync(KteRising.getSpawnLocation());
                        }
                        AutoStart.startCountdown();
                        end = false;
                    },
                    1L
            );
        }
    }

    public static void checkLive() {
        lives = 0;
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getGameMode() == GameMode.SURVIVAL) {
                lives++;
            }
        }
        if (lives != 1) return;
        checkWin();
    }

    public static void checkWin() {
        if (end || !match) return;
        end = true;
        if (LavaTask.Mtime <= 1) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                ChatUtil.sendTitle(
                        player,
                        "titles.deathmatch-finish.title",
                        "titles.deathmatch-finish.subtitle",
                        5, 200, 5,
                        Map.of()
                );
            }
        } else {
            winner = getWinner();
            if (winner == null) winner = "Unknown";
                for (Player player : Bukkit.getOnlinePlayers()) {
                    ChatUtil.sendTitle(
                            player,
                            "titles.finish-game.title",
                            "titles.finish-game.subtitle",
                            5, 200, 5,
                            Map.of("winner", winner)
                    );
                }
            }
        if (firstend) return;

        if (KteRising.getConfiguration().getBoolean("game-configurations.restart-server")) {
            KteRising.getInstance().getServer().getGlobalRegionScheduler().runDelayed(
                    KteRising.getInstance(),
                    (ScheduledTask task) -> KteRising.getInstance().getServer().shutdown(),
                    200L
            );
            return;
        }

        KteRising.getInstance().getServer().getGlobalRegionScheduler().runDelayed(
                KteRising.getInstance(),
                (ScheduledTask task) -> init(),
                200L
        );

        firstend = true;
    }

    private static String getWinner() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getGameMode() == GameMode.SURVIVAL) {
                PlayerStats stats = StatsCache.get(player.getUniqueId());
                if (stats != null) stats.wins++;

                RewardsManager.winPlayer(player);
                return player.getName();
            }
        }
        return null;
    }

    public static void startGame() {
        if (match) return;
        match = true;
        if (mode.equals(ChatUtil.getText("placeholderapi.mode-not-selected")) || mode == null) {
            mode = KteRising.getInstance().getVoteManager().getWinningMode();
        }
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

    public static void giveItems(Player player) {
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

                    meta.addItemFlags(
                            ItemFlag.HIDE_ENCHANTS,
                            ItemFlag.HIDE_ATTRIBUTES,
                            ItemFlag.HIDE_UNBREAKABLE,
                            ItemFlag.HIDE_DESTROYS,
                            ItemFlag.HIDE_PLACED_ON
                    );

                    if (!mi.getLore().isEmpty()) {
                        List<Component> finalLore = mi.getLore().stream()
                                .map(line -> MiniMessage.miniMessage().deserialize("<!i>" + line))
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
