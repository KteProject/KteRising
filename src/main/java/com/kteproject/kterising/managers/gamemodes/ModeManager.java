package com.kteproject.kterising.managers.gamemodes;
import com.kteproject.kterising.KteRising;
import org.bukkit.configuration.ConfigurationSection;
import java.util.*;

public class ModeManager {

    private static final List<GameModes> modes = new ArrayList<>();
    private static final List<String> modeNames = new ArrayList<>();

    public static void loadModes() {
        modes.clear();
        modeNames.clear();

        ConfigurationSection section = KteRising.getConfiguration().getConfigurationSection("modes-configuration");
        if (section == null) return;

        for (String key : section.getKeys(false)) {
            ConfigurationSection modeSec = section.getConfigurationSection(key);
            if (modeSec == null) continue;

            boolean enabled = modeSec.getBoolean("enabled", true);
            String label = safeString(modeSec.getString("label"), key);
            String description = safeString(modeSec.getString("description"), "");
            int guiSlot = modeSec.getInt("gui-slot", 0);

            List<ModeItem> items = new ArrayList<>();
            List<Map<?, ?>> rawItems = modeSec.getMapList("items");

            for (Map<?, ?> rawItem : rawItems) {
                String material = safeString(rawItem.get("material"), "STONE");
                int amount = safeInt(rawItem.get("amount"), 1);
                String name = safeString(rawItem.get("name"), "");
                List<String> lore = safeStringList(rawItem.get("lore"));
                Map<String, Integer> enchantments = parseEnchantments(rawItem.get("enchantments"));

                items.add(new ModeItem(material, amount, name, lore, enchantments));
            }

            int countdown = safeInt(modeSec.getInt("countdown", -1), -1);

            String placeholderlabel = safeString(modeSec.getString("placeholder-label"), "");

            GameModes mode = new GameModes(key, enabled, label, description, guiSlot, items, countdown, placeholderlabel);
            modes.add(mode);

            if (enabled) modeNames.add(key);
        }
    }

    public static List<GameModes> getModes() {
        return Collections.unmodifiableList(modes);
    }

    public static List<String> getModeNames() {
        return Collections.unmodifiableList(modeNames);
    }

    public static Optional<GameModes> getMode(String name) {
        return modes.stream().filter(m -> m.getName().equalsIgnoreCase(name)).findFirst();
    }

    private static String safeString(Object obj, String def) {
        return obj != null ? obj.toString() : def;
    }

    private static int safeInt(Object obj, int def) {
        if (obj instanceof Number n) return n.intValue();
        try {
            return Integer.parseInt(String.valueOf(obj));
        } catch (Exception ignored) {
            return def;
        }
    }

    private static List<String> safeStringList(Object obj) {
        List<String> result = new ArrayList<>();
        if (obj instanceof List<?> list) {
            for (Object o : list) result.add(String.valueOf(o));
        }
        return result;
    }

    private static Map<String, Integer> parseEnchantments(Object obj) {
        Map<String, Integer> map = new HashMap<>();
        List<String> enchList = safeStringList(obj);
        for (String e : enchList) {
            String[] parts = e.split(":");
            if (parts.length == 2) {
                try {
                    map.put(parts[0].trim(), Integer.parseInt(parts[1].trim()));
                } catch (NumberFormatException ignored) {}
            }
        }
        return map;
    }
}
