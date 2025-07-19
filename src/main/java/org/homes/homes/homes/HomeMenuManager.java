package org.homes.homes.homes;

import java.util.List;
import org.homes.homes.config.ConfigManager;

public class HomeMenuManager {
    private static final int MENU_SIZE = 27;
    private static final int MAX_HOMES = 10;
    private static ConfigManager configManager;

    public static void setConfigManager(ConfigManager configManager) {
        HomeMenuManager.configManager = configManager;
    }

    public static void openHomesMenu(org.bukkit.entity.Player player, int page) {
        List<HomeManager.Home> homes = HomeManager.getHomes(player);
        int totalHomes = homes.size();
        int maxPage = Math.max(1, (int) Math.ceil(totalHomes / 18.0));
        page = Math.max(1, Math.min(page, maxPage));
        org.bukkit.inventory.Inventory inv = org.bukkit.Bukkit.createInventory(null, 27,
                org.bukkit.ChatColor.DARK_AQUA + "Homes (" + page + "/" + maxPage + ")");
        int start = (page - 1) * 18;
        for (int i = 0; i < 18 && (start + i) < totalHomes; i++) {
            HomeManager.Home home = homes.get(start + i);
            org.bukkit.inventory.ItemStack item = new org.bukkit.inventory.ItemStack(org.bukkit.Material.OAK_DOOR);
            org.bukkit.inventory.meta.ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(org.bukkit.ChatColor.AQUA + "» " + org.bukkit.ChatColor.GOLD + home.getName());
            meta.setLore(java.util.Collections.singletonList(org.bukkit.ChatColor.GRAY + "Click to teleport"));
            item.setItemMeta(meta);
            inv.setItem(i, item);
        }
        if (maxPage > 1) {
            org.bukkit.inventory.ItemStack prev = new org.bukkit.inventory.ItemStack(org.bukkit.Material.ARROW);
            org.bukkit.inventory.meta.ItemMeta prevMeta = prev.getItemMeta();
            prevMeta.setDisplayName(org.bukkit.ChatColor.YELLOW + "« Previous Page");
            prev.setItemMeta(prevMeta);
            inv.setItem(18, prev);
            org.bukkit.inventory.ItemStack next = new org.bukkit.inventory.ItemStack(org.bukkit.Material.ARROW);
            org.bukkit.inventory.meta.ItemMeta nextMeta = next.getItemMeta();
            nextMeta.setDisplayName(org.bukkit.ChatColor.YELLOW + "Next Page »");
            next.setItemMeta(nextMeta);
            inv.setItem(26, next);
        }
        player.openInventory(inv);
    }

    public static boolean handleHomeMenuClick(org.bukkit.event.inventory.InventoryClickEvent event) {
        String title = net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.legacySection()
                .serialize(event.getView().title());
        if (!title.startsWith(org.bukkit.ChatColor.DARK_AQUA + "Homes ("))
            return false;
        event.setCancelled(true);
        if (event.getClickedInventory() == null
                || !event.getClickedInventory().equals(event.getView().getTopInventory()))
            return true;
        org.bukkit.entity.Player player = (org.bukkit.entity.Player) event.getWhoClicked();
        org.bukkit.inventory.ItemStack clicked = event.getCurrentItem();
        if (clicked == null || !clicked.hasItemMeta())
            return true;
        int slot = event.getSlot();
        if (clicked.getType() == org.bukkit.Material.ARROW) {
            String titleNoColors = org.bukkit.ChatColor.stripColor(title);
            int page = 1;
            try {
                String pageStr = titleNoColors.replaceAll(".*\\(", "").replaceAll("/.*", "");
                page = Integer.parseInt(pageStr);
            } catch (Exception ignored) {
            }
            int total = HomeManager.getHomes(player).size();
            int maxPage = Math.max(1, (int) Math.ceil(total / 18.0));
            if (slot == 18 && page > 1) {
                openHomesMenu(player, page - 1);
            } else if (slot == 26 && page < maxPage) {
                openHomesMenu(player, page + 1);
            }
            return true;
        }
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

    public static void openDeleteHomeMenu(org.bukkit.entity.Player player, int page) {
        List<HomeManager.Home> homes = HomeManager.getHomes(player);
        int totalHomes = homes.size();
        int maxPage = Math.max(1, (int) Math.ceil(totalHomes / 18.0));
        page = Math.max(1, Math.min(page, maxPage));
        org.bukkit.inventory.Inventory inv = org.bukkit.Bukkit.createInventory(null, 27,
                org.bukkit.ChatColor.DARK_RED + "Delete Home (" + page + "/" + maxPage + ")");
        int start = (page - 1) * 18;
        for (int i = 0; i < 18 && (start + i) < totalHomes; i++) {
            HomeManager.Home home = homes.get(start + i);
            org.bukkit.inventory.ItemStack item = new org.bukkit.inventory.ItemStack(org.bukkit.Material.BARRIER);
            org.bukkit.inventory.meta.ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(org.bukkit.ChatColor.RED + "✖ " + org.bukkit.ChatColor.GOLD + home.getName());
            meta.setLore(java.util.Collections.singletonList(org.bukkit.ChatColor.GRAY + "Click to delete"));
            item.setItemMeta(meta);
            inv.setItem(i, item);
        }
        if (maxPage > 1) {
            org.bukkit.inventory.ItemStack prev = new org.bukkit.inventory.ItemStack(org.bukkit.Material.ARROW);
            org.bukkit.inventory.meta.ItemMeta prevMeta = prev.getItemMeta();
            prevMeta.setDisplayName(org.bukkit.ChatColor.YELLOW + "« Previous Page");
            prev.setItemMeta(prevMeta);
            inv.setItem(18, prev);
            org.bukkit.inventory.ItemStack next = new org.bukkit.inventory.ItemStack(org.bukkit.Material.ARROW);
            org.bukkit.inventory.meta.ItemMeta nextMeta = next.getItemMeta();
            nextMeta.setDisplayName(org.bukkit.ChatColor.YELLOW + "Next Page »");
            next.setItemMeta(nextMeta);
            inv.setItem(26, next);
        }
        player.openInventory(inv);
    }

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
        int slot = event.getSlot();
        if (clicked.getType() == org.bukkit.Material.ARROW) {
            String titleNoColors = org.bukkit.ChatColor.stripColor(title);
            int page = 1;
            try {
                String pageStr = titleNoColors.replaceAll(".*\\(", "").replaceAll("/.*", "");
                page = Integer.parseInt(pageStr);
            } catch (Exception ignored) {
            }
            int total = HomeManager.getHomes(player).size();
            int maxPage = Math.max(1, (int) Math.ceil(total / 18.0));
            if (slot == 18 && page > 1) {
                openDeleteHomeMenu(player, page - 1);
            } else if (slot == 26 && page < maxPage) {
                openDeleteHomeMenu(player, page + 1);
            }
            return true;
        }
        if (clicked.getType() != org.bukkit.Material.BARRIER)
            return true;
        String displayName = clicked.getItemMeta().getDisplayName();
        String prefix = org.bukkit.ChatColor.RED + "✖ " + org.bukkit.ChatColor.GOLD;
        if (displayName.startsWith(prefix)) {
            String name = displayName.substring(prefix.length());
            HomeManager.Home home = HomeManager.getHome(player, name);
            if (home == null) {
                org.homes.homes.utils.MessageUtils.sendError(player, "This home does not exist!");
                return true;
            }
            boolean ok = HomeManager.removeHome(player.getUniqueId(), name);
            if (ok) {
                org.homes.homes.utils.MessageUtils.sendSuccess(player, "Home has been deleted!");
            } else {
                org.homes.homes.utils.MessageUtils.sendError(player, "Failed to delete home!");
            }
            player.closeInventory();
        }
        return true;
    }

}
