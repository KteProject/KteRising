package com.kteproject.kterising.managers;
import com.kteproject.kterising.commands.KteRisingCommand;
import com.kteproject.kterising.utils.ChatUtil;
import dev.triumphteam.cmd.bukkit.BukkitCommandManager;
import dev.triumphteam.cmd.bukkit.message.BukkitMessageKey;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

public class CommandManager {

    public static void init(Plugin plugin) {
        BukkitCommandManager<CommandSender> commandManager = BukkitCommandManager.create(plugin);

        commandManager.registerMessage(BukkitMessageKey.NO_PERMISSION, ((sender, context) -> {
            ChatUtil.sendMessage(sender, "other.no-permission");
        }));

        commandManager.registerMessage(BukkitMessageKey.PLAYER_ONLY, ((sender, context) -> {
            ChatUtil.sendMessage(sender, "other.no-permission");
        }));

        commandManager.registerCommand(new KteRisingCommand());
    }

}
