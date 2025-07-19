package org.homes.homes.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.homes.homes.homes.HomeManager;
import org.homes.homes.utils.MessageUtils;
import org.homes.homes.utils.ValidationUtils;

import java.util.ArrayList;
import java.util.List;

public class AddHomeCommand implements CommandManager.CommandExecutor, CommandManager.TabCompletable {
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
        if (args.length != 1) {
            MessageUtils.sendInfo(sender, "/addhome <name>");
            return true;
        }
        String name = args[0];
        if (!ValidationUtils.isValidHomeName(name)) {
            MessageUtils.sendError(sender,
                    "Invalid name! Use only letters, numbers, and underscore (max 32 characters).");
            return true;
        }
        boolean ok = HomeManager.addHome(player, name);
        if (ok) {
            MessageUtils.sendSuccess(sender, "Home '" + name + "' has been added!");
        } else {
            MessageUtils.sendError(sender,
                    "Cannot add home! You reached the limit, already have a home with this name, or you don't have enough money.");
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return new ArrayList<>();
    }
}