package com.kteproject.kterising.game;

import com.kteproject.kterising.KteRising;
import com.kteproject.kterising.managers.LobbyItems;
import com.kteproject.kterising.managers.RewardsManager;
import com.kteproject.kterising.managers.SafeBiomeManager;
import com.kteproject.kterising.managers.gamemodes.ModeItem;
import com.kteproject.kterising.managers.gamemodes.ModeManager;
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
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class MatchSession {

    private final KteRising plugin;
    private final LavaController lavaController;

    private MatchPhase phase = MatchPhase.LOBBY;
    private int seconds;
    private String mode;
    private int lavaY;
    private int lives;
    private World world;
    private boolean pvpAllowed;
    private boolean countingUp;
    private boolean lavaFrozen;
    private boolean endHandled;
    private String placeholderLabel;
    private int minX;
    private int minZ;
    private int maxX;
    private int maxZ;
    private final Set<UUID> joinedBefore = ConcurrentHashMap.newKeySet();

    public MatchSession(KteRising plugin) {
        this.plugin = plugin;
        this.lavaController = new LavaController(plugin, this);
    }

    public MatchPhase getPhase() {
        return phase;
    }

    public boolean isMatch() {
        return phase.isActiveMatch();
    }

    public boolean isLavaRising() {
        return phase.isLavaRising();
    }

    public boolean isPvpAllowed() {
        return pvpAllowed;
    }

    public void setPvpAllowed(boolean pvpAllowed) {
        this.pvpAllowed = pvpAllowed;
    }

    public int getSeconds() {
        return seconds;
    }

    public void setSeconds(int seconds) {
        this.seconds = seconds;
    }

    public void decrementSeconds() {
        seconds--;
    }

    public void incrementSeconds() {
        seconds++;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public int getLavaY() {
        return lavaY;
    }

    public void setLavaY(int lavaY) {
        this.lavaY = lavaY;
    }

    public void incrementLavaY() {
        lavaY++;
    }

    public int getLives() {
        return lives;
    }

    public World getWorld() {
        return world;
    }

    public boolean isCountingUp() {
        return countingUp;
    }

    public void setCountingUp(boolean countingUp) {
        this.countingUp = countingUp;
    }

    public boolean isLavaFrozen() {
        return lavaFrozen;
    }

    public void setLavaFrozen(boolean lavaFrozen) {
        this.lavaFrozen = lavaFrozen;
    }

    public boolean toggleLavaFrozen() {
        lavaFrozen = !lavaFrozen;
        return lavaFrozen;
    }

    public String getPlaceholderLabel() {
        return placeholderLabel;
    }

    public int getMinX() {
        return minX;
    }

    public int getMinZ() {
        return minZ;
    }

    public int getMaxX() {
        return maxX;
    }

    public int getMaxZ() {
        return maxZ;
    }

    public Set<UUID> getJoinedBefore() {
        return joinedBefore;
    }

    public LavaController getLavaController() {
        return lavaController;
    }

    public void transitionTo(MatchPhase next) {
        this.phase = next;
        if (next == MatchPhase.LAVA || next == MatchPhase.DEATHMATCH) {
            countingUp = true;
        }
        if (next == MatchPhase.LOBBY) {
            pvpAllowed = false;
            countingUp = false;
            lavaFrozen = false;
            endHandled = false;
        }
        if (next == MatchPhase.ENDED) {
            pvpAllowed = false;
        }
    }

    public void init(boolean afterMatch) {
        lavaController.cancelAll();

        world = plugin.getArenaWorld();
        WorldBorder wb = world.getWorldBorder();
        double size = plugin.getConfig().getDouble("world-configurations.world-border");

        if (afterMatch) {
            int x = (int) wb.getCenter().getX() + 1000;
            int z = (int) wb.getCenter().getZ() + 1000;
            Location safe = SafeBiomeManager.findSafeLocation(world, x, z);
            plugin.setSpawnLocation(safe);
            wb.setCenter(safe);
            wb.setSize(size);
            plugin.getVoteManager().resetVotes();
        } else {
            Location safe = SafeBiomeManager.findSafeLocation(world, 0, 0);
            plugin.setSpawnLocation(safe);
            wb.setCenter(safe.getX(), safe.getZ());
            wb.setSize(size);
            wb.setDamageAmount(5);
            wb.setDamageBuffer(2);
        }

        transitionTo(MatchPhase.LOBBY);
        seconds = 0;
        mode = ChatUtil.getText("placeholderapi.mode-not-selected");
        placeholderLabel = mode;
        lavaY = plugin.getConfig().getInt("game-configurations.lava-start-height");
        lives = 0;
        lavaController.resetDeathmatch();

        double half = wb.getSize() / 2.0;
        minX = (int) Math.floor(wb.getCenter().getX() - half);
        maxX = (int) Math.ceil(wb.getCenter().getX() + half);
        minZ = (int) Math.floor(wb.getCenter().getZ() - half);
        maxZ = (int) Math.ceil(wb.getCenter().getZ() + half);

        joinedBefore.clear();

        if (afterMatch) {
            plugin.getServer().getGlobalRegionScheduler().runDelayed(plugin, (ScheduledTask task) -> {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (plugin.getConfig().getBoolean("voting-menu-configuration.enabled")) {
                        LobbyItems.giveLobbyItem(player);
                    } else {
                        player.getInventory().clear();
                    }
                    player.setGameMode(GameMode.SURVIVAL);
                    player.teleportAsync(plugin.getSpawnLocation());
                }
                AutoStart.startCountdown();
            }, 1L);
        }
    }

    public void checkLive() {
        if (!isMatch()) return;
        lives = 0;
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getGameMode() == GameMode.SURVIVAL) {
                lives++;
            }
        }
        if (lives == 1) {
            checkWin();
        }
    }

    public void checkWin() {
        if (phase == MatchPhase.ENDED || !isMatch()) return;
        transitionTo(MatchPhase.ENDED);

        if (lavaController.getDeathmatchRemaining() <= 1 && lavaController.isDeathmatchActive()) {
            broadcastTitle("titles.deathmatch-finish.title", "titles.deathmatch-finish.subtitle", 5, 200, 5, Map.of());
        } else {
            String winner = resolveWinner();
            if (winner == null) winner = "Unknown";
            broadcastTitle("titles.finish-game.title", "titles.finish-game.subtitle", 5, 200, 5, Map.of("winner", winner));
        }

        if (endHandled) return;
        endHandled = true;

        lavaController.cancelAll();

        if (plugin.getConfig().getBoolean("game-configurations.restart-server")) {
            plugin.getServer().getGlobalRegionScheduler().runDelayed(
                    plugin,
                    (ScheduledTask task) -> plugin.getServer().shutdown(),
                    200L
            );
            return;
        }

        plugin.getServer().getGlobalRegionScheduler().runDelayed(
                plugin,
                (ScheduledTask task) -> init(true),
                200L
        );
    }

    private String resolveWinner() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getGameMode() == GameMode.SURVIVAL) {
                StatsCache.recordWin(player.getUniqueId());
                RewardsManager.winPlayer(player);
                return player.getName();
            }
        }
        return null;
    }

    public void startGame() {
        if (isMatch()) return;
        transitionTo(MatchPhase.GRACE);

        if (mode == null || mode.equals(ChatUtil.getText("placeholderapi.mode-not-selected"))) {
            String winning = plugin.getVoteManager().getWinningMode();
            if (winning != null) {
                mode = winning;
            }
        }

        checkLive();

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.getInventory().clear();
            player.setGameMode(GameMode.SURVIVAL);
            player.setHealth(20);
            player.setFoodLevel(20);
            player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0, true, false));
            StatsCache.recordGamePlayed(player.getUniqueId());
            giveItems(player);
            ChatUtil.sendTitle(player, "titles.start-game.title", "titles.start-game.subtitle", 5, 60, 5, Map.of());
        }

        int countdown = 0;
        var modeData = ModeManager.getMode(mode);
        if (modeData.isPresent()) {
            countdown = modeData.get().getCountdown();
            placeholderLabel = modeData.get().getPlaceholderlabel();
        }

        lavaController.startGraceCountdown(countdown);
    }

    public void giveItems(Player player) {
        ModeManager.getMode(mode).ifPresent(modeData -> {
            Registry<Enchantment> enchantmentRegistry = Bukkit.getRegistry(Enchantment.class);

            for (ModeItem mi : modeData.getItems()) {
                Material mat = mi.getMaterial();
                if (mat == null) continue;
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

                    if (enchantmentRegistry != null) {
                        for (Map.Entry<String, Integer> e : mi.getEnchantments().entrySet()) {
                            Enchantment ench = enchantmentRegistry.get(NamespacedKey.minecraft(e.getKey().toLowerCase()));
                            if (ench != null) {
                                item.addUnsafeEnchantment(ench, e.getValue());
                            }
                        }
                    }
                }

                player.getInventory().addItem(item);
            }
        });
    }

    public void broadcastTitle(String titleKey, String subtitleKey, int fadeIn, int stay, int fadeOut, Map<String, String> placeholders) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            ChatUtil.sendTitle(player, titleKey, subtitleKey, fadeIn, stay, fadeOut, placeholders);
        }
    }

    public void shutdown() {
        lavaController.cancelAll();
        AutoStart.stopCountdown();
    }
}
