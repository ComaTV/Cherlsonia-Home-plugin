package org.homes.homes.homes;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.*;
import org.homes.homes.utils.EconomyUtils;
import org.homes.homes.config.ConfigManager;

public class HomeManager {
    private static final int MAX_HOMES = 10;
    private static final String HOMES_FILE = "homes.yml";
    private static Map<UUID, Map<String, Home>> playerHomes = new HashMap<>();
    private static File file;
    private static YamlConfiguration config;
    private static JavaPlugin plugin;
    private static ConfigManager configManager;

    public static void setPlugin(JavaPlugin pluginInstance) {
        plugin = pluginInstance;
    }

    public static void setConfigManager(ConfigManager manager) {
        configManager = manager;
    }

    public static class Home {
        private final String name;
        private final Location location;

        public Home(String name, Location location) {
            this.name = name;
            this.location = location;
        }

        public String getName() {
            return name;
        }

        public Location getLocation() {
            return location;
        }
    }

    public static void init(File dataFolder) {
        file = new File(dataFolder, HOMES_FILE);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException ignored) {}
        }
        config = YamlConfiguration.loadConfiguration(file);
        loadHomes();
    }

    public static void loadHomes() {
        playerHomes.clear();
        if (config == null) return;
        for (String uuidStr : config.getKeys(false)) {
            UUID uuid;
            try {
                uuid = UUID.fromString(uuidStr);
            } catch (IllegalArgumentException e) {
                continue;
            }
            Map<String, Home> homes = new HashMap<>();
            for (String homeName : config.getConfigurationSection(uuidStr).getKeys(false)) {
                String path = uuidStr + "." + homeName;
                String world = config.getString(path + ".world");
                if (world == null) continue;
                double x = config.getDouble(path + ".x");
                double y = config.getDouble(path + ".y");
                double z = config.getDouble(path + ".z");
                float yaw = (float) config.getDouble(path + ".yaw");
                float pitch = (float) config.getDouble(path + ".pitch");
                Location loc = null;
                org.bukkit.World bukkitWorld = Bukkit.getWorld(world);
                if (bukkitWorld != null) {
                    loc = new Location(bukkitWorld, x, y, z, yaw, pitch);
                } else {
                    continue;
                }
                homes.put(homeName, new Home(homeName, loc));
            }
            playerHomes.put(uuid, homes);
        }
    }

    public static void saveHomes() {
        if (config == null) return;
        // Șterge toate cheile vechi
        for (String key : new HashSet<>(config.getKeys(false))) {
            config.set(key, null);
        }
        for (Map.Entry<UUID, Map<String, Home>> entry : playerHomes.entrySet()) {
            String uuidStr = entry.getKey().toString();
            for (Map.Entry<String, Home> homeEntry : entry.getValue().entrySet()) {
                Home home = homeEntry.getValue();
                Location loc = home.getLocation();
                String path = uuidStr + "." + home.getName();
                config.set(path + ".world", loc.getWorld().getName());
                config.set(path + ".x", loc.getX());
                config.set(path + ".y", loc.getY());
                config.set(path + ".z", loc.getZ());
                config.set(path + ".yaw", loc.getYaw());
                config.set(path + ".pitch", loc.getPitch());
            }
        }
        try {
            config.save(file);
        } catch (IOException ignored) {}
    }

    public static boolean addHome(Player player, String name) {
        UUID uuid = player.getUniqueId();
        Map<String, Home> homes = playerHomes.computeIfAbsent(uuid, k -> new HashMap<>());
        if (homes.size() >= MAX_HOMES) return false;
        if (homes.containsKey(name)) return false;
        int homeCost = configManager != null ? configManager.getHomePrice() : 1000;
        if (!EconomyUtils.hasMoney(player, homeCost)) return false;
        if (!EconomyUtils.removeMoney(player, homeCost)) return false;
        homes.put(name, new Home(name, player.getLocation()));
        saveHomes();
        return true;
    }

    public static boolean removeHome(UUID uuid, String homeName) {
        Map<String, Home> homes = playerHomes.get(uuid);
        if (homes != null && homes.remove(homeName) != null) {
            saveHomes();
            return true;
        }
        return false;
    }

    public static Home getHome(Player player, String name) {
        Map<String, Home> homes = playerHomes.get(player.getUniqueId());
        if (homes == null) return null;
        return homes.get(name);
    }

    public static List<Home> getHomes(Player player) {
        Map<String, Home> homes = playerHomes.get(player.getUniqueId());
        if (homes == null) return Collections.emptyList();
        return new ArrayList<>(homes.values());
    }

    public static Set<UUID> getPlayersWithHomes() {
        return playerHomes.keySet();
    }

    public static Map<String, Home> getHomes(UUID uuid) {
        return playerHomes.getOrDefault(uuid, Collections.emptyMap());
    }
} 