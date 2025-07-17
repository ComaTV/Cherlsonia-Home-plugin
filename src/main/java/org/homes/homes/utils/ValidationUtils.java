package org.homes.homes.utils;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ValidationUtils {
    
    public static boolean isPlayer(CommandSender sender) {
        return sender instanceof Player;
    }
    
    public static boolean isOperator(CommandSender sender) {
        return sender.isOp();
    }
    
    public static boolean isValidMaterial(String materialName) {
        try {
            Material.valueOf(materialName.toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
    
    public static Material getMaterial(String materialName) {
        try {
            return Material.valueOf(materialName.toUpperCase());
        } catch (IllegalArgumentException e) {
            return Material.ENDER_PEARL; // Default fallback
        }
    }
    
    public static boolean isValidWaypointName(String name) {
        return name != null && !name.trim().isEmpty() && name.length() <= 32;
    }
    
    public static boolean isValidHomeName(String name) {
        return name != null && !name.trim().isEmpty() && name.length() <= 32 && name.matches("[a-zA-Z0-9_]+$");
    }
    
    public static String sanitizeWaypointName(String name) {
        if (name == null) return "";
        return name.trim().replaceAll("[^a-zA-Z0-9_]", "_").toLowerCase();
    }
} 