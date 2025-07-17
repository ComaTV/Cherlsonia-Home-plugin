package org.homes.homes.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.homes.homes.homes.HomeMenuManager;
import org.homes.homes.utils.MessageUtils;
import org.homes.homes.utils.ValidationUtils;

public class AdminHomeCommand implements CommandManager.CommandExecutor {
    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!ValidationUtils.isPlayer(sender)) {
            MessageUtils.sendError(sender, "Only players can use this command.");
            return true;
        }
        if (!sender.isOp()) {
            MessageUtils.sendError(sender, "Only OPs can use this command.");
            return true;
        }
        Player player = (Player) sender;
        HomeMenuManager.openAdminPlayersMenu(player, 1);
        return true;
    }
} 