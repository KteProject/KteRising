package com.kteproject.kterising.managers.gamemodes;
import org.bukkit.Material;
import java.util.List;
import java.util.Map;

public class ModeItem {
    private final Material material;
    private final int amount;
    private final String name;
    private final List<String> lore;
    private final Map<String, Integer> enchantments;

    public ModeItem(String mat, int amount, String name, List<String> lore, Map<String, Integer> enchantments) {
        this.material = Material.getMaterial(mat.toUpperCase());
        this.amount = amount;
        this.name = name;
        this.lore = lore;
        this.enchantments = enchantments;
    }

    public Material getMaterial() { return material; }
    public int getAmount() { return amount; }
    public String getName() { return name; }
    public List<String> getLore() { return lore; }
    public Map<String, Integer> getEnchantments() { return enchantments; }
}
