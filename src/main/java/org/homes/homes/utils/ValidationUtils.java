package org.homes.homes.utils;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ValidationUtils {
    
    public static boolean isPlayer(CommandSender sender) {
        return sender instanceof Player;
    }
    

    
    public static boolean isValidHomeName(String name) {
        return name != null && !name.trim().isEmpty() && name.length() <= 32 && name.matches("[a-zA-Z0-9_]+$");
    }
    

} 