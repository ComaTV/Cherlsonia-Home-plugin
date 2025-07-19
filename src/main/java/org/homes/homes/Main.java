package org.homes.homes;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.homes.homes.commands.CommandManager;
import org.homes.homes.config.ConfigManager;
import org.homes.homes.events.EventManager;
import org.homes.homes.homes.HomeManager;
import org.homes.homes.utils.MessageUtils;

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

        HomeManager.setPlugin(this);
        HomeManager.init(getDataFolder());
        HomeManager.setConfigManager(configManager);
        org.homes.homes.homes.HomeMenuManager.setConfigManager(configManager);
        org.homes.homes.homes.AdminHomeMenuManager.setConfigManager(configManager);

        commandManager = new CommandManager(configManager);
        getCommand("addhome").setTabCompleter(commandManager);
        getCommand("delhome").setTabCompleter(commandManager);
        getCommand("homes").setTabCompleter(commandManager);

        eventManager = new EventManager(this);
        eventManager.registerAllListeners();

        getServer().getScheduler().runTaskLater(this, () -> {
            org.homes.homes.homes.HomeManager.processMonthlyAccess(this);
        }, 20L * 10);
    }

    @Override
    public void onDisable() {
        HomeManager.saveHomes();

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

}
