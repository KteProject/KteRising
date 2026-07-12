package com.kteproject.kterising.managers.vote;

import com.kteproject.kterising.KteRising;
import com.kteproject.kterising.utils.ChatUtil;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VoteGui {

    private static final class CachedGuiItem {
        final String modeName;
        final Component name;
        final List<String> rawDescription;
        final Material material;
        final int slot;

        CachedGuiItem(String modeName, Component name, List<String> rawDescription, Material material, int slot) {
            this.modeName = modeName;
            this.name = name;
            this.rawDescription = rawDescription;
            this.material = material;
            this.slot = slot;
        }
    }

    private final JavaPlugin plugin;
    private final VoteManager voteManager;
    private final Map<String, CachedGuiItem> cachedItems = new HashMap<>();
    private Component title;
    private int guiRows;

    public VoteGui(JavaPlugin plugin, VoteManager voteManager) {
        this.plugin = plugin;
        this.voteManager = voteManager;
    }

    public void reload() {
        cachedItems.clear();
        title = MiniMessage.miniMessage().deserialize(
                KteRising.getConfiguration().getString("voting-menu-configuration.gui-title", "<black>Vote")
        );
        guiRows = KteRising.getConfiguration().getInt("voting-menu-configuration.gui-row", 3);

        ConfigurationSection modesSection =
                KteRising.getConfiguration().getConfigurationSection("modes-configuration");
        if (modesSection == null) {
            plugin.getLogger().warning("No voting modes found in configuration!");
            return;
        }
        buildCache(modesSection);
    }

    public void init() {
        reload();
    }

    private void buildCache(ConfigurationSection section) {
        for (String modeName : section.getKeys(false)) {
            ConfigurationSection cfg = section.getConfigurationSection(modeName);
            if (cfg == null) continue;
            if (!cfg.getBoolean("enabled", false)) continue;

            String label = cfg.getString("label", "<red>ERROR");
            List<String> desc = cfg.getStringList("description");
            int slot = cfg.getInt("gui-slot", -1);
            Material material = Material.getMaterial(cfg.getString("gui-item", "BARRIER"));

            if (slot == -1 || material == null) {
                plugin.getLogger().warning("GUI item for mode " + modeName + " is invalid.");
                continue;
            }

            Component name = MiniMessage.miniMessage().deserialize("<!i>" + label);
            cachedItems.put(modeName, new CachedGuiItem(modeName, name, desc, material, slot));
        }
    }

    public void open(Player player) {
        if (!KteRising.getConfiguration().getBoolean("voting-menu-configuration.enabled") && !player.isOp()) {
            return;
        }

        Gui gui = Gui.gui()
                .title(title != null ? title : Component.text("Vote"))
                .rows(guiRows)
                .disableAllInteractions()
                .create();

        for (CachedGuiItem cached : cachedItems.values()) {
            int votes = voteManager.getVotesForMode(cached.modeName);
            List<Component> lore = new ArrayList<>();
            for (String s : cached.rawDescription) {
                lore.add(MiniMessage.miniMessage().deserialize(
                        "<!i>" + s.replace("<vote>", String.valueOf(votes))
                ));
            }

            GuiItem item = ItemBuilder.from(cached.material)
                    .name(cached.name)
                    .lore(lore)
                    .flags(
                            ItemFlag.HIDE_ENCHANTS,
                            ItemFlag.HIDE_ATTRIBUTES,
                            ItemFlag.HIDE_UNBREAKABLE,
                            ItemFlag.HIDE_DESTROYS,
                            ItemFlag.HIDE_PLACED_ON
                    )
                    .asGuiItem(event -> {
                        boolean voted = voteManager.vote(player.getUniqueId(), cached.modeName);
                        if (voted) {
                            ChatUtil.sendMessage(player, "vote.vote", Map.of("mode", cached.modeName));
                            open(player);
                        } else {
                            ChatUtil.sendMessage(player, "vote.already-voted");
                            gui.close(player);
                        }
                    });

            gui.setItem(cached.slot, item);
        }

        gui.open(player);
    }
}
