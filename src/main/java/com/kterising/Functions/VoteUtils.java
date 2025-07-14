package com.kterising.Functions;

import com.kterising.KteRising;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class VoteUtils {

    private static LuckPerms luckPerms;
    private static boolean checked = false;

    private static boolean isLuckPermsAvailable() {
        if (!checked) {
            try {
                luckPerms = LuckPermsProvider.get();
            } catch (Throwable t) {
                Bukkit.getLogger().warning("[KteRising] LuckPerms not found or failed to initialize. Vote multipliers are disabled.");
                luckPerms = null;
            }
            checked = true;
        }
        return luckPerms != null;
    }

    public static int getVoteMultiplier(Player player) {
        if (!isLuckPermsAvailable()) {
            return 1;
        }

        User user = luckPerms.getUserManager().getUser(player.getUniqueId());
        if (user == null) {
            Bukkit.getLogger().warning("LuckPerms: Failed to fetch user data for " + player.getName());
            return 1;
        }

        String groupName = user.getPrimaryGroup().toLowerCase();
        FileConfiguration config = JavaPlugin.getPlugin(KteRising.class).getConfig();
        return config.getInt("vote_multipliers." + groupName, 1);
    }
}
