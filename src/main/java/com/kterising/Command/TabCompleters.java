package com.kterising.Command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TabCompleters implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (command.getName().equalsIgnoreCase("kterising")) {
            if (args.length == 1) {
                return Arrays.asList("start", "mode", "reload", "vote", "freeze", "skip");
            } else if (args.length == 2) {
                if (args[0].equalsIgnoreCase("mode")) {
                    return Arrays.asList("classic", "op", "elytra", "elytraop", "trident", "tridentop");
                }
            }
        }
        return Collections.emptyList();
    }
}
