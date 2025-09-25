package com.example.skyblock;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SkyblockPlugin extends JavaPlugin {

    private final Map<UUID, Location> backLocations = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) return false;

        if (label.equalsIgnoreCase("skyblock")) {
            backLocations.put(player.getUniqueId(), player.getLocation());

            String worldName = "skyblock_" + player.getUniqueId();
            World playerWorld = Bukkit.getWorld(worldName);

            if (playerWorld == null) {
                File template = new File(Bukkit.getWorldContainer(), "world_skyblock");
                File newWorld = new File(Bukkit.getWorldContainer(), worldName);

                if (!newWorld.exists()) {
                    try {
                        copyWorld(template.toPath(), newWorld.toPath());
                    } catch (IOException e) {
                        player.sendMessage("§cChyba při kopírování světa!");
                        e.printStackTrace();
                        return true;
                    }
                }

                playerWorld = new WorldCreator(worldName).createWorld();
            }

            player.teleport(playerWorld.getSpawnLocation());
            player.sendMessage("§aByl jsi teleportován na svůj Skyblock!");
            return true;
        }

        if (label.equalsIgnoreCase("back")) {
            Location back = backLocations.get(player.getUniqueId());
            if (back != null) {
                player.teleport(back);
                player.sendMessage("§aByl jsi vrácen zpět!");
            } else {
                player.sendMessage("§cNemáš uloženou předchozí pozici!");
            }
            return true;
        }

        return false;
    }

    private void copyWorld(Path source, Path target) throws IOException {
        Files.walk(source).forEach(path -> {
            try {
                Path dest = target.resolve(source.relativize(path));
                if (Files.isDirectory(path)) {
                    if (!Files.exists(dest)) Files.createDirectories(dest);
                } else {
                    Files.copy(path, dest, StandardCopyOption.REPLACE_EXISTING);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
