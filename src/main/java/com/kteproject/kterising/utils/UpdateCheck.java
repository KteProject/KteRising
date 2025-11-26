package com.kteproject.kterising.utils;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;
import java.util.function.Consumer;

public class UpdateCheck {

    private final JavaPlugin plugin;
    private final int resourceId;

    public UpdateCheck(JavaPlugin plugin, int resourceId) {
        this.plugin = plugin;
        this.resourceId = resourceId;
    }

    public void isUpdateAvailable(final Consumer<Boolean> consumer) {
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
            try (InputStream is = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + this.resourceId).openStream();
                 Scanner scanner = new Scanner(is)) {

                if (scanner.hasNext()) {
                    String latestVersion = scanner.next();
                    String currentVersion = plugin.getDescription().getVersion();

                    boolean isUpdateAvailable = !currentVersion.equalsIgnoreCase(latestVersion);
                    consumer.accept(isUpdateAvailable);
                } else {
                    consumer.accept(false);
                }

            } catch (IOException e) {
                plugin.getLogger().info("Unable to check for updates: " + e.getMessage());
                consumer.accept(false);
            }
        });
    }
}
