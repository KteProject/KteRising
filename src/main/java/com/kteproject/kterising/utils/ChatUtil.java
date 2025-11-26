package com.kteproject.kterising.utils;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class ChatUtil {

    private static BukkitAudiences audiences;
    private static final MiniMessage mini = MiniMessage.miniMessage();

    private ChatUtil() {}

    public static void init(Plugin plugin) {
        if (audiences != null) return;
        audiences = BukkitAudiences.create(plugin);
    }

    public static void shutdown() {
        if (audiences != null) {
            audiences.close();
            audiences = null;
        }
    }

    public static String getText(String keyOrRaw) {
        String message = MessagesConfig.getMessage(keyOrRaw);
        return (message != null) ? message : keyOrRaw;
    }

    public static Component parse(String text, TagResolver... resolvers) {
        return mini.deserialize(text, resolvers);
    }

    public static void sendMessage(CommandSender sender, String text, TagResolver... resolvers) {
        Objects.requireNonNull(sender);
        Audience audience = audiences.sender(sender);
        audience.sendMessage(parse(getText(text), resolvers));
    }

    public static void sendMessage(CommandSender sender, Component component) {
        Objects.requireNonNull(sender);
        Objects.requireNonNull(component);
        audiences.sender(sender).sendMessage(component);
    }

    public static void sendMessage(Player player, String text, TagResolver... resolvers) {
        sendMessage((CommandSender) player, text, resolvers);
    }

    public static void sendConsole(String text, TagResolver... resolvers) {
        audiences.console().sendMessage(parse(getText(text), resolvers));
    }

    public static void broadcast(String text, TagResolver... resolvers) {
        audiences.all().sendMessage(parse(getText(text), resolvers));
    }

    public static void broadcastPlayers(String text, TagResolver... resolvers) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            audiences.player(p).sendMessage(parse(getText(text), resolvers));
        }
    }

    public static void sendMessage(CommandSender sender, String text, Map<String, String> placeholders) {
        TagResolver.Builder builder = TagResolver.builder();
        if (placeholders != null) {
            placeholders.forEach((key, value) ->
                    builder.resolver(Placeholder.parsed(key, value)));
        }
        sendMessage(sender, text, builder.build());
    }

    public static void broadcast(String text, Map<String, String> placeholders) {
        TagResolver.Builder builder = TagResolver.builder();
        if (placeholders != null) {
            placeholders.forEach((key, value) ->
                    builder.resolver(Placeholder.parsed(key, value)));
        }
        broadcast(text, builder.build());
    }

    public static void sendTitle(Player player,
                                 String titleKey,
                                 String subtitleKey,
                                 int fadeInTicks,
                                 int stayTicks,
                                 int fadeOutTicks,
                                 Map<String, String> placeholders) {

        if (player == null || !player.isOnline()) return;

        TagResolver.Builder builder = TagResolver.builder();

        if (placeholders != null) {
            placeholders.forEach((k, v) -> {
                if (k != null && v != null) builder.resolver(Placeholder.parsed(k, v));
            });
        }

        TagResolver resolver = builder.build();

        String rawTitle = getText(titleKey);
        String rawSubtitle = getText(subtitleKey);

        if ((rawTitle == null || rawTitle.isEmpty()) && (rawSubtitle == null || rawSubtitle.isEmpty())) return;

        Component title = parse(rawTitle == null ? "" : rawTitle, resolver);
        Component subtitle = parse(rawSubtitle == null ? "" : rawSubtitle, resolver);

        long fadeInMillis = Math.max(0, fadeInTicks) * 50L;
        long stayMillis = Math.max(0, stayTicks) * 50L;
        long fadeOutMillis = Math.max(0, fadeOutTicks) * 50L;

        Title.Times times = Title.Times.times(
                java.time.Duration.ofMillis(fadeInMillis),
                java.time.Duration.ofMillis(stayMillis),
                java.time.Duration.ofMillis(fadeOutMillis)
        );

        audiences.player(player).showTitle(Title.title(title, subtitle, times));
    }

    public static void sendListMessage(CommandSender sender, String listKey, TagResolver... resolvers) {
        List<String> list = MessagesConfig.getConfig().getStringList(listKey);
        if (list == null || list.isEmpty()) return;
        Audience audience = audiences.sender(sender);
        for (String line : list) {
            audience.sendMessage(parse(line, resolvers));
        }
    }

    public static void sendListMessage(CommandSender sender, String listKey, Map<String, String> placeholders) {
        TagResolver.Builder builder = TagResolver.builder();
        if (placeholders != null) {
            placeholders.forEach((key, value) ->
                    builder.resolver(Placeholder.parsed(key, value)));
        }
        sendListMessage(sender, listKey, builder.build());
    }

    public static void sendActionBar(Player player, String messageKey, TagResolver... resolvers) {
        if (player == null || !player.isOnline()) return;

        String rawMessage = getText(messageKey);
        if (rawMessage == null || rawMessage.isEmpty()) return;

        Component component = parse(rawMessage, resolvers);
        audiences.player(player).sendActionBar(component);
    }

    public static void sendActionBar(Player player, String messageKey, Map<String, String> placeholders) {
        TagResolver.Builder builder = TagResolver.builder();

        if (placeholders != null) {
            placeholders.forEach((k, v) -> {
                if (k != null && v != null)
                    builder.resolver(Placeholder.parsed(k, v));
            });
        }

        sendActionBar(player, messageKey, builder.build());
    }

}
