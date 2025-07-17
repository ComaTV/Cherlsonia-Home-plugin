package org.homes.homes.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.homes.homes.config.ConfigManager;
import org.homes.homes.homes.HomeMenuManager;
import org.homes.homes.utils.MessageUtils;
import org.homes.homes.utils.ValidationUtils;

public class HomesCommand implements CommandManager.CommandExecutor {
    private final ConfigManager configManager;

    public HomesCommand(ConfigManager configManager) {
        this.configManager = configManager;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            if (sender.hasPermission("homes.reload") || sender.isOp()) {
                configManager.reloadConfig();
                sender.sendMessage("§aConfig-ul a fost reîncărcat!");
            } else {
                sender.sendMessage("§cNu ai permisiunea să folosești această comandă.");
            }
            return true;
        }
        if (!ValidationUtils.isPlayer(sender)) {
            MessageUtils.sendError(sender, "Only players can use this command.");
            return true;
        }
        HomeMenuManager.openHomesMenu((Player) sender);
        return false;
    }
} 