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
            MessageUtils.sendError(sender, "Doar jucătorii pot folosi această comandă.");
            return true;
        }
        Player player = (Player) sender;
        if (args.length != 1) {
            MessageUtils.sendInfo(sender, "/addhome <nume>");
            return true;
        }
        String name = args[0];
        if (!ValidationUtils.isValidHomeName(name)) {
            MessageUtils.sendError(sender, "Nume invalid! Folosește doar litere, cifre și underscore (max 32 caractere).");
            return true;
        }
        boolean ok = HomeManager.addHome(player, name);
        if (ok) {
            MessageUtils.sendSuccess(sender, "Home-ul '" + name + "' a fost adăugat!");
        } else {
            MessageUtils.sendError(sender, "Nu poți adăuga home-ul! Ai atins limita sau există deja un home cu acest nume.");
        }
        return true;
    }
    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return new ArrayList<>();
    }
} 