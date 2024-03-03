package com.kterising;

import com.kterising.Placeholders.PlaceholderUtil;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class Placeholder extends PlaceholderExpansion {

    @Override
    public @NotNull String getIdentifier() {
        return "KteRising";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Ruby, Ben Lordlex, Kaan Flod";
    }

    @Override
    public @NotNull String getVersion() {
        return "2.0";
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String placeholder) {
        if (player == null) {
            return null;
        }

        String value = PlaceholderUtil.processPlaceholder(player, placeholder);
        return value;
    }
}