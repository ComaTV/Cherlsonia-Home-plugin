package org.homes.homes.commands;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.homes.homes.homes.HomesManager;
import org.homes.homes.utils.MessageUtils;
import org.homes.homes.utils.ValidationUtils;

import java.util.ArrayList;
import java.util.List;

public class AddHomesCommand implements CommandManager.CommandExecutor, CommandManager.TabCompletable {
    
    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!ValidationUtils.isPlayer(sender)) {
            MessageUtils.sendError(sender, "Only players can use this command.");
            return true;
        }
        
        if (!ValidationUtils.isOperator(sender)) {
            MessageUtils.sendError(sender, "Only operators can create waypoints!");
            return true;
        }
        
        if (args.length < 1 || args.length > 2) {
            MessageUtils.sendInfo(sender, "/addwaypoint <name> [item]");
            MessageUtils.sendInfo(sender, "Available items: ender_pearl, diamond, emerald, gold_ingot, iron_ingot, etc.");
            return true;
        }
        
        String name = args[0];
        if (!ValidationUtils.isValidWaypointName(name)) {
            MessageUtils.sendError(sender, "Invalid waypoint name! Use only letters, numbers, and underscores (max 32 characters).");
            return true;
        }
        
        Material item = Material.ENDER_PEARL; // Default item
        
        if (args.length == 2) {
            if (!ValidationUtils.isValidMaterial(args[1])) {
                MessageUtils.sendError(sender, "Invalid item: " + args[1]);
                MessageUtils.sendInfo(sender, "Use a valid Minecraft item name (e.g., ender_pearl, diamond, emerald)");
                return true;
            }
            item = ValidationUtils.getMaterial(args[1]);
        }
        
        boolean ok = HomesManager.addWaypoint(name, (Player) sender, item);
        if (ok) {
            MessageUtils.sendMessage(sender, "waypoint_added", "%name%", name, "%item%", item.name().toLowerCase());
        } else {
            MessageUtils.sendMessage(sender, "waypoint_exists");
        }
        return true;
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            // Tab completion for waypoint name (first argument)
            return new ArrayList<>(); // Empty list for name suggestions
        } else if (args.length == 2) {
            // Tab completion for item material (second argument)
            String partialItem = args[1].toLowerCase();
            List<String> suggestions = new ArrayList<>();
            
            // Popular items for waypoints
            String[] popularItems = {
                "ender_pearl", "diamond", "emerald", "gold_ingot", "iron_ingot", 
                "netherite_ingot", "emerald_block", "diamond_block", "gold_block",
                "iron_block", "netherite_block", "beacon", "compass", "clock",
                "book", "map", "chest", "ender_chest", "shulker_box", "barrel",
                "furnace", "crafting_table", "anvil", "enchanting_table", "bed",
                "torch", "lantern", "soul_lantern", "campfire", "soul_campfire",
                "flower_pot", "potted_plant", "sign", "banner", "shield"
            };
            
            for (String item : popularItems) {
                if (item.startsWith(partialItem)) {
                    suggestions.add(item);
                }
            }
            
            return suggestions;
        }
        
        return new ArrayList<>();
    }
} 