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
            MessageUtils.sendError(sender, "Doar jucătorii pot folosi această comandă.");
            return true;
        }
        Player player = (Player) sender;
        if (args.length == 0) {
            HomeMenuManager.openDeleteHomeMenu(player);
            return true;
        }
        if (args.length != 1) {
            MessageUtils.sendInfo(sender, "/delhome <nume>");
            return true;
        }
        boolean ok = HomeManager.removeHome(player, args[0]);
        if (ok) {
            MessageUtils.sendSuccess(sender, "Home-ul a fost șters și banii returnați!");
        } else {
            MessageUtils.sendError(sender, "Nu există acest home!");
        }
        return true;
    }
} 