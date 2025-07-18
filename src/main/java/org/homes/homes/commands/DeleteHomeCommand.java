package org.homes.homes.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.homes.homes.homes.HomeManager;
import org.homes.homes.homes.HomeMenuManager;
import org.homes.homes.utils.MessageUtils;
import org.homes.homes.utils.ValidationUtils;
import org.homes.homes.config.ConfigManager;
import org.homes.homes.utils.EconomyUtils;

public class DeleteHomeCommand implements CommandManager.CommandExecutor {
    @Override
    public boolean execute(CommandSender sender, String[] args) {
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
        if (args.length == 0) {
            HomeMenuManager.openDeleteHomeMenu(player, 1);
            return true;
        }
        if (args.length != 1) {
            MessageUtils.sendInfo(sender, "/delhome <name>");
            return true;
        }

        HomeManager.Home home = HomeManager.getHome(player, args[0]);
        if (home == null) {
            MessageUtils.sendError(sender, "This home does not exist!");
            return true;
        }
        boolean ok = HomeManager.removeHome(player.getUniqueId(), args[0]);
        if (ok) {
            MessageUtils.sendSuccess(sender, "Home has been deleted!");
        } else {
            MessageUtils.sendError(sender, "Failed to delete home!");
        }
        return true;
    }
}