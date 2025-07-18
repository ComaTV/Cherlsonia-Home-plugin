package org.homes.homes.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.homes.homes.homes.HomeManager;
import org.homes.homes.utils.MessageUtils;
import org.homes.homes.utils.ValidationUtils;
import org.homes.homes.config.ConfigManager;
import org.homes.homes.utils.EconomyUtils;

import java.util.List;
import java.util.ArrayList;

public class ExtendHomeTimeCommand
        implements CommandManager.CommandExecutor, CommandManager.TabCompletable, org.bukkit.command.TabCompleter {
    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!ValidationUtils.isPlayer(sender)) {
            MessageUtils.sendError(sender, "Only players can use this command.");
            return true;
        }
        Player player = (Player) sender;
        if (args.length != 2) {
            MessageUtils.sendInfo(sender, "/extendhometime <home_name> <months>");
            return true;
        }
        String homeName = args[0];
        int months;
        try {
            months = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            MessageUtils.sendError(sender, "The number of months must be an integer!");
            return true;
        }
        if (months < 1 || months > 10) {
            MessageUtils.sendError(sender, "You can only extend between 1 and 10 months at a time!");
            return true;
        }
        HomeManager.Home home = HomeManager.getHome(player, homeName);
        if (home == null) {
            MessageUtils.sendError(sender, "You don't have any home with this name!");
            return true;
        }
        // Cost logic
        ConfigManager configManager = org.homes.homes.Main.getInstance().getConfigManager();
        int pricePerMonth = configManager.getHomePrice();
        int totalCost = months * pricePerMonth;
        if (!EconomyUtils.hasMoney(player, totalCost)) {
            MessageUtils.sendError(sender, "You don't have enough money! You need " + totalCost + " coins.");
            return true;
        }
        EconomyUtils.removeMoney(player, totalCost);
        int current = home.getDurationMonths();
        home.setDurationMonths(current + months);
        MessageUtils.sendSuccess(sender, "The duration of home '" + homeName + "' has been extended by " + months
                + " months (total: " + home.getDurationMonths() + " months). You paid " + totalCost + " coins.");
        HomeManager.saveHomes();
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1 && sender instanceof Player) {
            Player player = (Player) sender;
            for (HomeManager.Home home : HomeManager.getHomes(player)) {
                completions.add(home.getName());
            }
        } else if (args.length == 2) {
            for (int i = 1; i <= 10; i++) {
                completions.add(String.valueOf(i));
            }
        }
        return completions;
    }

    @Override
    public java.util.List<String> onTabComplete(org.bukkit.command.CommandSender sender,
            org.bukkit.command.Command command, String alias, String[] args) {
        return onTabComplete(sender, args);
    }
}