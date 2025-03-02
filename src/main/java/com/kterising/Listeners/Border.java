package com.kterising.Listeners;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class Border implements Listener {

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (player.getGameMode() != GameMode.SPECTATOR) return;

        World world = player.getWorld();
        WorldBorder border = world.getWorldBorder();
        Location loc = player.getLocation();

        double borderSize = border.getSize() / 2;
        Location center = border.getCenter();

        double minX = center.getX() - borderSize;
        double maxX = center.getX() + borderSize;
        double minZ = center.getZ() - borderSize;
        double maxZ = center.getZ() + borderSize;

        double x = loc.getX();
        double z = loc.getZ();

        if (x < minX || x > maxX || z < minZ || z > maxZ) {
            double newX = Math.max(minX, Math.min(maxX, x));
            double newZ = Math.max(minZ, Math.min(maxZ, z));

            Location newLoc = new Location(world, newX, loc.getY(), newZ, loc.getYaw(), loc.getPitch());
            player.teleport(newLoc);
        }
    }
}
