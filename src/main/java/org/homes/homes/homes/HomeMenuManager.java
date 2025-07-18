package org.homes.homes.homes;

import java.util.List;
import org.homes.homes.config.ConfigManager;

public class HomeMenuManager {
    private static final int MENU_SIZE = 27;
    private static final int ADMIN_MENU_SIZE = 54;
    private static final int MAX_HOMES = 10;
    private static final int PER_PAGE = 36;
    private static ConfigManager configManager;

    public static void setConfigManager(ConfigManager configManager) {
        HomeMenuManager.configManager = configManager;
    }

    public static void openHomesMenu(org.bukkit.entity.Player player) {
        List<HomeManager.Home> homes = HomeManager.getHomes(player);
        org.bukkit.inventory.Inventory inv = org.bukkit.Bukkit.createInventory(null, MENU_SIZE,
                org.bukkit.ChatColor.DARK_AQUA + "Homes");
        int maxHomes = HomeManager.getMaxHomes(player.getUniqueId());
        for (int i = 0; i < homes.size() && i < maxHomes; i++) {
            HomeManager.Home home = homes.get(i);
            org.bukkit.inventory.ItemStack item = new org.bukkit.inventory.ItemStack(org.bukkit.Material.OAK_DOOR);
            org.bukkit.inventory.meta.ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(org.bukkit.ChatColor.AQUA + "» " + org.bukkit.ChatColor.GOLD + home.getName());
            meta.setLore(java.util.Collections.singletonList(org.bukkit.ChatColor.GRAY + "Click to teleport"));
            item.setItemMeta(meta);
            inv.setItem(i, item);
        }
        player.openInventory(inv);
    }

    public static boolean handleHomeMenuClick(org.bukkit.event.inventory.InventoryClickEvent event) {
        String title = net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.legacySection()
                .serialize(event.getView().title());
        if (!title.startsWith(org.bukkit.ChatColor.DARK_AQUA + "Homes"))
            return false;
        event.setCancelled(true);
        if (event.getClickedInventory() == null
                || !event.getClickedInventory().equals(event.getView().getTopInventory()))
            return true;
        org.bukkit.entity.Player player = (org.bukkit.entity.Player) event.getWhoClicked();
        org.bukkit.inventory.ItemStack clicked = event.getCurrentItem();
        if (clicked == null || !clicked.hasItemMeta())
            return true;
        if (clicked.getType() != org.bukkit.Material.OAK_DOOR)
            return true;
        String displayName = clicked.getItemMeta().getDisplayName();
        String prefix = org.bukkit.ChatColor.AQUA + "» " + org.bukkit.ChatColor.GOLD;
        if (displayName.startsWith(prefix)) {
            String name = displayName.substring(prefix.length());
            HomeManager.Home home = HomeManager.getHome(player, name);
            if (home != null) {
                player.teleport(home.getLocation());
                org.homes.homes.utils.MessageUtils.sendSuccess(player, "Teleported to home '" + name + "'.");
                player.closeInventory();
            }
        }
        return true;
    }

    // Create Delete Home Menu
    public static void openDeleteHomeMenu(org.bukkit.entity.Player player) {
        List<HomeManager.Home> homes = HomeManager.getHomes(player);
        org.bukkit.inventory.Inventory inv = org.bukkit.Bukkit.createInventory(null, MENU_SIZE,
                org.bukkit.ChatColor.DARK_RED + "Delete Home");
        int maxHomes = HomeManager.getMaxHomes(player.getUniqueId());
        for (int i = 0; i < homes.size() && i < maxHomes; i++) {
            HomeManager.Home home = homes.get(i);
            org.bukkit.inventory.ItemStack item = new org.bukkit.inventory.ItemStack(org.bukkit.Material.BARRIER);
            org.bukkit.inventory.meta.ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(org.bukkit.ChatColor.RED + "✖ " + org.bukkit.ChatColor.GOLD + home.getName());
            meta.setLore(java.util.Collections.singletonList(org.bukkit.ChatColor.GRAY + "Click to delete"));
            item.setItemMeta(meta);
            inv.setItem(i, item);
        }
        player.openInventory(inv);
    }

    // Handle Delete Home Menu Click
    public static boolean handleDeleteHomeMenuClick(org.bukkit.event.inventory.InventoryClickEvent event) {
        String title = net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.legacySection()
                .serialize(event.getView().title());
        if (!title.startsWith(org.bukkit.ChatColor.DARK_RED + "Delete Home"))
            return false;
        event.setCancelled(true);
        if (event.getClickedInventory() == null
                || !event.getClickedInventory().equals(event.getView().getTopInventory()))
            return true;
        org.bukkit.entity.Player player = (org.bukkit.entity.Player) event.getWhoClicked();
        org.bukkit.inventory.ItemStack clicked = event.getCurrentItem();
        if (clicked == null || !clicked.hasItemMeta())
            return true;
        if (clicked.getType() != org.bukkit.Material.BARRIER)
            return true;
        String displayName = clicked.getItemMeta().getDisplayName();
        String prefix = org.bukkit.ChatColor.RED + "✖ " + org.bukkit.ChatColor.GOLD;
        if (displayName.startsWith(prefix)) {
            String name = displayName.substring(prefix.length());
            boolean ok = HomeManager.removeHome(player.getUniqueId(), name);
            if (ok) {
                org.homes.homes.utils.MessageUtils.sendSuccess(player, "Home has been deleted and money refunded!");
            } else {
                org.homes.homes.utils.MessageUtils.sendError(player, "This home does not exist!");
            }
            player.closeInventory();
        }
        return true;
    }

    // Homes edit
    public static void openAdminHomesMenu(org.bukkit.entity.Player admin, java.util.UUID targetUuid) {
        java.util.Map<String, HomeManager.Home> homes = HomeManager.getHomes(targetUuid);
        org.bukkit.inventory.Inventory inv = org.bukkit.Bukkit.createInventory(null, MENU_SIZE,
                org.bukkit.ChatColor.DARK_AQUA + "Homes of "
                        + org.bukkit.Bukkit.getOfflinePlayer(targetUuid).getName());
        int i = 0;
        for (HomeManager.Home home : homes.values()) {
            if (i >= MAX_HOMES)
                break;
            org.bukkit.inventory.ItemStack item = new org.bukkit.inventory.ItemStack(org.bukkit.Material.BARRIER);
            org.bukkit.inventory.meta.ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(org.bukkit.ChatColor.RED + "✖ " + org.bukkit.ChatColor.GOLD + home.getName());
            meta.setLore(java.util.Collections.singletonList(org.bukkit.ChatColor.GRAY + "Click to delete"));
            item.setItemMeta(meta);
            inv.setItem(i++, item);
        }
        admin.openInventory(inv);
        admin.sendMessage("DEBUG: Deschid inventar cu titlul: " + org.bukkit.ChatColor.DARK_AQUA + "Homes of "
                + org.bukkit.Bukkit.getOfflinePlayer(targetUuid).getName());
    }

    public static boolean handleAdminHomesMenuClick(org.bukkit.event.inventory.InventoryClickEvent event) {
        String title = net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.legacySection()
                .serialize(event.getView().title());
        if (!title.startsWith(org.bukkit.ChatColor.DARK_AQUA + "Homes of "))
            return false;
        event.setCancelled(true);
        if (event.getClickedInventory() == null
                || !event.getClickedInventory().equals(event.getView().getTopInventory()))
            return true;
        org.bukkit.entity.Player player = (org.bukkit.entity.Player) event.getWhoClicked();
        org.bukkit.inventory.ItemStack clicked = event.getCurrentItem();
        if (clicked == null || !clicked.hasItemMeta())
            return true;
        if (clicked.getType() != org.bukkit.Material.BARRIER)
            return true;
        String displayName = clicked.getItemMeta().getDisplayName();
        String prefix = org.bukkit.ChatColor.RED + "✖ " + org.bukkit.ChatColor.GOLD;
        if (displayName.startsWith(prefix)) {
            String name = displayName.substring(prefix.length());
            boolean ok = HomeManager.removeHome(player.getUniqueId(), name);
            if (ok) {
                org.homes.homes.utils.MessageUtils.sendSuccess(player, "Home has been deleted and money refunded!");
            } else {
                org.homes.homes.utils.MessageUtils.sendError(player, "This home does not exist!");
            }
            player.closeInventory();
        }
        return true;
    }

    // Player select
    public static void openAdminPlayersMenu(org.bukkit.entity.Player admin, int page) {
        java.util.List<java.util.UUID> uuids = new java.util.ArrayList<>(HomeManager.getPlayersWithHomes());
        int total = uuids.size();
        int maxPage = Math.max(1, (int) Math.ceil(total / (double) PER_PAGE));
        page = Math.max(1, Math.min(page, maxPage));
        org.bukkit.inventory.Inventory inv = org.bukkit.Bukkit.createInventory(null, ADMIN_MENU_SIZE,
                org.bukkit.ChatColor.DARK_PURPLE + "Players with Homes (" + page + "/" + maxPage + ")");
        int start = (page - 1) * PER_PAGE;
        for (int i = 0; i < PER_PAGE && (start + i) < total; i++) {
            java.util.UUID uuid = uuids.get(start + i);
            org.bukkit.OfflinePlayer offlinePlayer = org.bukkit.Bukkit.getOfflinePlayer(uuid);
            org.bukkit.inventory.ItemStack item = new org.bukkit.inventory.ItemStack(org.bukkit.Material.PLAYER_HEAD);
            org.bukkit.inventory.meta.ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(org.bukkit.ChatColor.GOLD + offlinePlayer.getName());
            meta.setLore(java.util.Collections.singletonList(org.bukkit.ChatColor.GRAY + "Click to view homes"));
            item.setItemMeta(meta);
            inv.setItem(i, item);
        }
        // Navigation buttons
        org.bukkit.inventory.ItemStack prev = new org.bukkit.inventory.ItemStack(org.bukkit.Material.ARROW);
        org.bukkit.inventory.meta.ItemMeta prevMeta = prev.getItemMeta();
        prevMeta.setDisplayName(org.bukkit.ChatColor.YELLOW + "« Previous Page");
        prev.setItemMeta(prevMeta);
        inv.setItem(45, prev);
        org.bukkit.inventory.ItemStack next = new org.bukkit.inventory.ItemStack(org.bukkit.Material.ARROW);
        org.bukkit.inventory.meta.ItemMeta nextMeta = next.getItemMeta();
        nextMeta.setDisplayName(org.bukkit.ChatColor.YELLOW + "Next Page »");
        next.setItemMeta(nextMeta);
        inv.setItem(53, next);
        admin.openInventory(inv);
    }

    public static boolean handleAdminPlayersMenuClick(org.bukkit.event.inventory.InventoryClickEvent event) {
        String title = net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.legacySection()
                .serialize(event.getView().title());
        if (!title.startsWith(org.bukkit.ChatColor.DARK_PURPLE + "Players with Homes"))
            return false;
        event.setCancelled(true);
        if (event.getClickedInventory() == null
                || !event.getClickedInventory().equals(event.getView().getTopInventory()))
            return true;
        org.bukkit.entity.Player admin = (org.bukkit.entity.Player) event.getWhoClicked();
        org.bukkit.inventory.ItemStack clicked = event.getCurrentItem();
        if (clicked == null || !clicked.hasItemMeta())
            return true;
        if (clicked.getType() == org.bukkit.Material.PLAYER_HEAD) {
            String displayName = clicked.getItemMeta().getDisplayName();
            String playerName = org.bukkit.ChatColor.stripColor(displayName);
            org.bukkit.OfflinePlayer target = org.bukkit.Bukkit.getOfflinePlayer(playerName);
            if (target != null && target.getUniqueId() != null) {
                openAdminHomesMenu(admin, target.getUniqueId());
            }
        } else if (clicked.getType() == org.bukkit.Material.ARROW) {
            String btn = clicked.getItemMeta().getDisplayName();
            int page = 1;
            try {
                String pageStr = title.replaceAll(".*\\(", "").replaceAll("/.*", "");
                page = Integer.parseInt(pageStr);
            } catch (Exception ignored) {
            }
            int total = HomeManager.getPlayersWithHomes().size();
            int maxPage = Math.max(1, (int) Math.ceil(total / (double) PER_PAGE));
            if (btn.equals(org.bukkit.ChatColor.YELLOW + "« Previous Page") && page > 1) {
                openAdminPlayersMenu(admin, page - 1);
            } else if (btn.equals(org.bukkit.ChatColor.YELLOW + "Next Page »") && page < maxPage) {
                openAdminPlayersMenu(admin, page + 1);
            }
        }
        return true;
    }

}
