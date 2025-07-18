package org.homes.homes;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.homes.homes.commands.CommandManager;
import org.homes.homes.config.ConfigManager;
import org.homes.homes.events.EventManager;
import org.homes.homes.homes.HomeManager;
import org.homes.homes.utils.MessageUtils;
import org.homes.homes.commands.ExtendHomeTimeCommand;

public class Main extends JavaPlugin {
    private static Main instance;
    private ConfigManager configManager;
    private CommandManager commandManager;
    private EventManager eventManager;

    @Override
    public void onEnable() {
        instance = this;
        configManager = new ConfigManager(this);
        MessageUtils.setConfigManager(configManager);

        // Initialize home system
        HomeManager.setPlugin(this);
        HomeManager.init(getDataFolder());
        HomeManager.setConfigManager(configManager);

        // Initialize command system
        commandManager = new CommandManager(configManager);
        getCommand("addhome").setTabCompleter(commandManager);
        getCommand("delhome").setTabCompleter(commandManager);
        getCommand("homes").setTabCompleter(commandManager);
        getCommand("extendhometime").setTabCompleter(new ExtendHomeTimeCommand());

        // Initialize event system
        eventManager = new EventManager(this);
        eventManager.registerAllListeners();

        getLogger().info("Home plugin enabled successfully!");
        getLogger().info("Loaded " + HomeManager.getPlayersWithHomes().size() + " players with homes");
    }

    @Override
    public void onDisable() {
        HomeManager.saveHomes();
        getLogger().info("Home plugin disabled!");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return commandManager.executeCommand(sender, command, label, args);
    }

    public static Main getInstance() {
        return instance;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public EventManager getEventManager() {
        return eventManager;
    }
}
