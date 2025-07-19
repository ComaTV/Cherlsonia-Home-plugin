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
                sender.sendMessage("§aConfiguration has been reloaded!");
            } else {
                sender.sendMessage("§cYou don't have permission to use this command.");
            }
            return true;
        }
        if (!ValidationUtils.isPlayer(sender)) {
            MessageUtils.sendError(sender, "Only players can use this command.");
            return true;
        }
        Player player = (Player) sender;
        if (!player.hasPermission("home_acces")
                || !org.homes.homes.homes.HomeManager.hasHomeAccess(player.getUniqueId())) {
            MessageUtils.sendError(sender, "You do not have access to home commands!");
            return true;
        }
        HomeMenuManager.openHomesMenu((Player) sender, 1);
        return false;
    }
}