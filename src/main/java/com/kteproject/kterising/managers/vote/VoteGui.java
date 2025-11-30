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
import java.util.*;

public class VoteGui {

    private static class CachedGuiItem {
        final String modeName;
        final Component name;
        final List<String> rawDescription;
        final Material material;
        final int slot;

        CachedGuiItem(String modeName, Component name,
                      List<String> rawDescription,
                      Material material,
                      int slot) {
            this.modeName = modeName;
            this.name = name;
            this.rawDescription = rawDescription;
            this.material = material;
            this.slot = slot;
        }
    }

    private static final Map<String, CachedGuiItem> CACHED_ITEMS = new HashMap<>();

    private final JavaPlugin plugin;
    private final VoteManager voteManager;

    private static final Component TITLE = MiniMessage.miniMessage()
            .deserialize(KteRising.getConfiguration().getString("voting-menu-configuration.gui-title"));

    private static final int GUI_ROWS =
            KteRising.getConfiguration().getInt("voting-menu-configuration.gui-row", 3);


    public VoteGui(JavaPlugin plugin, VoteManager voteManager) {
        this.plugin = plugin;
        this.voteManager = voteManager;
    }

    public void init() {
        if (!CACHED_ITEMS.isEmpty()) return;

        ConfigurationSection modesSection =
                KteRising.getConfiguration().getConfigurationSection("modes-configuration");

        if (modesSection == null) {
            plugin.getLogger().warning("No voting modes found in configuration!");
            return;
        }

        buildCache(modesSection);
    }


    private void buildCache(ConfigurationSection section) {

        for (String modeName : section.getKeys(false)) {

            ConfigurationSection cfg = section.getConfigurationSection(modeName);
            if (cfg == null) continue;

            if (!cfg.getBoolean("enabled", false)) continue;

            String label = cfg.getString("label", "<red>ERROR");
            List<String> desc = cfg.getStringList("description");
            int slot = cfg.getInt("gui-slot", -1);
            Material m = Material.getMaterial(cfg.getString("gui-item", "BARRIER"));

            if (slot == -1 || m == null) {
                plugin.getLogger().warning("GUI item for mode " + modeName + " is invalid.");
                continue;
            }

            Component name = MiniMessage.miniMessage().deserialize("<!i>" + label);

            CACHED_ITEMS.put(modeName, new CachedGuiItem(
                    modeName,
                    name,
                    desc,
                    m,
                    slot
            ));
        }
    }


    public void open(Player player) {
        if(!KteRising.getConfiguration().getBoolean("voting-menu-configuration.enabled")){
            ChatUtil.sendMessage(player, "");
            if(!player.isOp())return;
        }
        Gui gui = Gui.gui()
                .title(TITLE)
                .rows(GUI_ROWS)
                .disableAllInteractions()
                .create();

        for (CachedGuiItem cached : CACHED_ITEMS.values()) {

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
