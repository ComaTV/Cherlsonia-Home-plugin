package org.homes.homes.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;

public class ConfigManager {
    private final Plugin plugin;
    private final File configFile;
    private FileConfiguration config;

    public ConfigManager(Plugin plugin) {
        this.plugin = plugin;
        this.configFile = new File(plugin.getDataFolder(), "config.yml");
        loadConfig();
    }

    private void loadConfig() {
        if (!configFile.exists()) {
            plugin.saveResource("config.yml", false);
        }
        config = YamlConfiguration.loadConfiguration(configFile);
        setDefaults();
    }

    private void setDefaults() {
        config.addDefault("waypoints.enabled", true);
        config.addDefault("waypoints.max_per_player", 10);
        config.addDefault("waypoints.default_item", "ENDER_PEARL");
        config.addDefault("messages.prefix", "&8[&bCharless&8] &r");
        config.addDefault("messages.waypoint_added", "&aWaypoint '%name%' added with item %item%!");
        config.addDefault("messages.waypoint_deleted", "&cWaypoint deleted!");
        config.addDefault("messages.waypoint_exists", "&cA waypoint with this name already exists!");
        config.addDefault("messages.no_waypoint", "&cNo such waypoint!");
        config.addDefault("messages.teleported", "&aTeleported to waypoint: %name%");
        
        config.options().copyDefaults(true);
        saveConfig();
    }

    public void saveConfig() {
        try {
            config.save(configFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save config.yml: " + e.getMessage());
        }
    }

    public void reloadConfig() {
        config = YamlConfiguration.loadConfiguration(configFile);
        setDefaults();
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public String getMessage(String path) {
        return config.getString("messages." + path, "Message not found: " + path);
    }

    public String getMessage(String path, String... replacements) {
        String message = getMessage(path);
        for (int i = 0; i < replacements.length; i += 2) {
            if (i + 1 < replacements.length) {
                message = message.replace(replacements[i], replacements[i + 1]);
            }
        }
        return message;
    }
} 