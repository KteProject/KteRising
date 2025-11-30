package com.kteproject.kterising;
import com.kteproject.kterising.placeholders.PlaceholderUtil;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class Placeholder extends PlaceholderExpansion {

    @Override
    public @NotNull String getIdentifier() {
        return "kterising";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Ruby,Ben Lordlex";
    }

    @Override
    public @NotNull String getVersion() {
        return "2.1.0";
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String placeholder) {
        if (player == null) return null;

        return PlaceholderUtil.processPlaceholder(player, placeholder);
    }

}
