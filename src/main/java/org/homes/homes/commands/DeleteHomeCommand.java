package org.homes.homes.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.homes.homes.homes.HomeManager;
import org.homes.homes.homes.HomeMenuManager;
import org.homes.homes.utils.MessageUtils;
import org.homes.homes.utils.ValidationUtils;

public class DeleteHomeCommand implements CommandManager.CommandExecutor {
    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!ValidationUtils.isPlayer(sender)) {
            MessageUtils.sendError(sender, "Only players can use this command.");
            return true;
        }
        Player player = (Player) sender;
        if (args.length == 0) {
            HomeMenuManager.openDeleteHomeMenu(player);
            return true;
        }
        if (args.length != 1) {
            MessageUtils.sendInfo(sender, "/delhome <name>");
            return true;
        }
        boolean ok = HomeManager.removeHome(player, args[0]);
        if (ok) {
            MessageUtils.sendSuccess(sender, "Home has been deleted and money refunded!");
        } else {
            MessageUtils.sendError(sender, "This home does not exist!");
        }
        return true;
    }
} 