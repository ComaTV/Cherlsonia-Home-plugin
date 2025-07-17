package org.homes.homes.homes;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class HomesManager {
    private static final Map<String, WaypointData> waypoints = new HashMap<>();
    private static File file;
    private static YamlConfiguration config;
    private static JavaPlugin plugin;

    public static void setPlugin(JavaPlugin pluginInstance) {
        plugin = pluginInstance;
    }

    public static class WaypointData {
        private final Location location;
        private final Material item;

        public WaypointData(Location location, Material item) {
            this.location = location;
            this.item = item;
        }

        public Location getLocation() {
            return location;
        }

        public Material getItem() {
            return item;
        }
    }

    public static void init(File dataFolder) {
        File waypointsFolder = new File(dataFolder, "waypoints");
        if (!waypointsFolder.exists()) {
            waypointsFolder.mkdirs();
        }
        file = new File(waypointsFolder, "waypoints.yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException ignored) {
            }
        }
        config = YamlConfiguration.loadConfiguration(file);
        loadWaypoints();
    }

    public static void loadWaypoints() {
        waypoints.clear();
        if (config == null)
            return;
        for (String key : config.getKeys(false)) {
            // Skip non-waypoint keys (like "version")
            String world = config.getString(key + ".world");
            if (world == null)
                continue;
            double x = config.getDouble(key + ".x");
            double y = config.getDouble(key + ".y");
            double z = config.getDouble(key + ".z");
            float yaw = (float) config.getDouble(key + ".yaw");
            float pitch = (float) config.getDouble(key + ".pitch");
            Location loc = null;
            org.bukkit.World bukkitWorld = Bukkit.getWorld(world);
            if (bukkitWorld != null) {
                loc = new Location(bukkitWorld, x, y, z, yaw, pitch);
            } else {
                // Loghează sau ignoră waypointul
                plugin.getLogger().warning("[Waypoints] World not found for waypoint: " + key + " (world: " + world + ")");
                continue;
            }
            
            // Load item material, default to ENDER_PEARL if not specified
            String itemName = config.getString(key + ".item", "ENDER_PEARL");
            Material item;
            try {
                item = Material.valueOf(itemName.toUpperCase());
            } catch (IllegalArgumentException e) {
                item = Material.ENDER_PEARL; // Default fallback
            }
            
            waypoints.put(key, new WaypointData(loc, item));
        }
    }

    public static void saveWaypoints() {
        if (config == null)
            return;
        config.set("version", 1);
        if (waypoints.isEmpty()) {
            config.set("waypoints", null);
            return;
        }
        for (Map.Entry<String, WaypointData> entry : waypoints.entrySet()) {
            WaypointData data = entry.getValue();
            Location loc = data.getLocation();
            String key = entry.getKey();
            config.set(key + ".world", loc.getWorld().getName());
            config.set(key + ".x", loc.getX());
            config.set(key + ".y", loc.getY());
            config.set(key + ".z", loc.getZ());
            config.set(key + ".yaw", loc.getYaw());
            config.set(key + ".pitch", loc.getPitch());
            config.set(key + ".item", data.getItem().name());
        }
        try {
            config.save(file);
        } catch (IOException ignored) {
        }
    }

    public static boolean addWaypoint(String name, Player player, Material item) {
        if (waypoints.containsKey(name))
            return false;
        Location loc = player.getLocation();
        waypoints.put(name, new WaypointData(loc, item));
        saveWaypoints();
        return true;
    }

    public static boolean addWaypoint(String name, Player player) {
        return addWaypoint(name, player, Material.ENDER_PEARL);
    }

    public static boolean removeWaypoint(String name) {
        if (!waypoints.containsKey(name))
            return false;
        waypoints.remove(name);
        config.set(name, null);
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    public static Set<String> getWaypointNames() {
        return waypoints.keySet();
    }

    public static Location getWaypoint(String name) {
        WaypointData data = waypoints.get(name);
        return data != null ? data.getLocation() : null;
    }

    public static Material getWaypointItem(String name) {
        WaypointData data = waypoints.get(name);
        return data != null ? data.getItem() : Material.ENDER_PEARL;
    }

    public static YamlConfiguration getConfig() {
        return config;
    }

    public static File getFile() {
        return file;
    }
}
