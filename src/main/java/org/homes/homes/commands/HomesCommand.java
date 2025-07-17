package org.homes.homes.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.homes.homes.homes.HomeMenuManager;
import org.homes.homes.utils.MessageUtils;
import org.homes.homes.utils.ValidationUtils;

public class HomesCommand implements CommandManager.CommandExecutor {
    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!ValidationUtils.isPlayer(sender)) {
            MessageUtils.sendError(sender, "Doar jucătorii pot folosi această comandă.");
            return true;
        }
        HomeMenuManager.openHomesMenu((Player) sender);
        return true;
    }
} 