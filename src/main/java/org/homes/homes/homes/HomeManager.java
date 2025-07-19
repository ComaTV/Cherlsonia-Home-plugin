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
    private static Map<UUID, Integer> playerMaxHomes = new HashMap<>();
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
        private int durationMonths;

        public Home(String name, Location location) {
            this.name = name;
            this.location = location;
            this.durationMonths = 0;
        }

        public String getName() {
            return name;
        }

        public Location getLocation() {
            return location;
        }

        public int getDurationMonths() {
            return durationMonths;
        }

        public void setDurationMonths(int durationMonths) {
            this.durationMonths = durationMonths;
        }
    }

    public static void init(File dataFolder) {
        file = new File(dataFolder, HOMES_FILE);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException ignored) {
            }
        }
        config = YamlConfiguration.loadConfiguration(file);
        loadHomes();
    }

    public static void loadHomes() {
        playerHomes.clear();
        playerMaxHomes.clear();
        if (config == null)
            return;
        for (String uuidStr : config.getKeys(false)) {
            UUID uuid;
            try {
                uuid = UUID.fromString(uuidStr);
            } catch (IllegalArgumentException e) {
                continue;
            }
            Map<String, Home> homes = new HashMap<>();
            int maxHomes = config.getInt(uuidStr + ".maxHomes", MAX_HOMES);
            playerMaxHomes.put(uuid, maxHomes);
            for (String homeName : config.getConfigurationSection(uuidStr).getKeys(false)) {
                if (homeName.equals("maxHomes"))
                    continue;
                String path = uuidStr + "." + homeName;
                String world = config.getString(path + ".world");
                if (world == null)
                    continue;
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
                Home home = new Home(homeName, loc);
                home.setDurationMonths(config.getInt(path + ".durationMonths", 0));
                homes.put(homeName, home);
            }
            playerHomes.put(uuid, homes);
        }
    }

    public static void saveHomes() {
        if (config == null)
            return;

        for (String key : new HashSet<>(config.getKeys(false))) {
            config.set(key, null);
        }
        for (Map.Entry<UUID, Map<String, Home>> entry : playerHomes.entrySet()) {
            String uuidStr = entry.getKey().toString();

            int maxHomes = playerMaxHomes.getOrDefault(entry.getKey(), MAX_HOMES);
            config.set(uuidStr + ".maxHomes", maxHomes);
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
                config.set(path + ".durationMonths", home.getDurationMonths());
            }
        }
        try {
            config.save(file);
        } catch (IOException ignored) {
        }
    }

    public static boolean addHome(Player player, String name) {
        int homeCost = configManager != null ? configManager.getHomePrice() : 1000;
        if (!EconomyUtils.hasMoney(player, homeCost))
            return false;
        UUID uuid = player.getUniqueId();
        Map<String, Home> homes = playerHomes.computeIfAbsent(uuid, k -> new HashMap<>());
        if (homes.size() >= getMaxHomes(uuid)) {
            player.sendMessage("Home limit reached: " + getMaxHomes(uuid));
            return false;
        }
        if (homes.containsKey(name))
            return false;
        if (!EconomyUtils.removeMoney(player, homeCost))
            return false;
        Home home = new Home(name, player.getLocation());
        int defaultDuration = configManager != null ? configManager.getDefaultHomeDuration() : 1;
        home.setDurationMonths(defaultDuration);
        homes.put(name, home);
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
        if (homes == null)
            return null;
        return homes.get(name);
    }

    public static List<Home> getHomes(Player player) {
        Map<String, Home> homes = playerHomes.get(player.getUniqueId());
        if (homes == null)
            return Collections.emptyList();
        return new ArrayList<>(homes.values());
    }

    public static Set<UUID> getPlayersWithHomes() {
        return playerHomes.keySet();
    }

    public static Map<String, Home> getHomes(UUID uuid) {
        return playerHomes.getOrDefault(uuid, Collections.emptyMap());
    }

    public static int getMaxHomes(UUID uuid) {
        return playerMaxHomes.getOrDefault(uuid, MAX_HOMES);
    }

    public static void setMaxHomes(UUID uuid, int max) {
        playerMaxHomes.put(uuid, max);
        saveHomes();
    }

    public static int getHomeDuration(UUID uuid, String homeName) {
        Map<String, Home> homes = playerHomes.get(uuid);
        if (homes == null)
            return 0;
        Home home = homes.get(homeName);
        return home != null ? home.getDurationMonths() : 0;
    }

    public static void setHomeDuration(UUID uuid, String homeName, int months) {
        Map<String, Home> homes = playerHomes.computeIfAbsent(uuid, k -> new HashMap<>());
        Home home = homes.get(homeName);
        if (home != null) {
            home.setDurationMonths(months);
            saveHomes();
        }
    }

}