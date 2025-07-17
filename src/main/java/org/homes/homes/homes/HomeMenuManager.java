package org.homes.homes.homes;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.homes.homes.utils.MessageUtils;

import java.util.ArrayList;
import java.util.List;

public class HomeMenuManager {
    private static final int MENU_SIZE = 27;
    private static final int ADMIN_MENU_SIZE = 54;
    private static final int MAX_HOMES = 10;
    private static final int PER_PAGE = 36;

    public static void openWaypointsMenu(Player player, int page) {
        List<String> names = new ArrayList<>(HomesManager.getWaypointNames());
        int total = names.size();
        int maxPage = Math.max(1, (int) Math.ceil(total / (double) PER_PAGE));
        page = Math.max(1, Math.min(page, maxPage));
        Inventory inv = Bukkit.createInventory(null, MENU_SIZE, ChatColor.DARK_AQUA + "Waypoints " + ChatColor.GRAY + "(Page " + ChatColor.YELLOW + page + ChatColor.GRAY + ")");
        int start = (page - 1) * PER_PAGE;
        for (int i = 0; i < PER_PAGE && (start + i) < total; i++) {
            String name = names.get(start + i);
            Material itemMaterial = HomesManager.getWaypointItem(name);
            ItemStack item = new ItemStack(itemMaterial);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(ChatColor.AQUA + "» " + ChatColor.GOLD + name);
            meta.setLore(java.util.Collections.singletonList(ChatColor.GRAY + "Click to teleport"));
            item.setItemMeta(meta);
            inv.setItem(i, item);
        }
        // Navigation buttons
        ItemStack prev = new ItemStack(Material.ARROW);
        ItemMeta prevMeta = prev.getItemMeta();
        prevMeta.setDisplayName(ChatColor.YELLOW + "« Previous Page");
        prev.setItemMeta(prevMeta);
        inv.setItem(45, prev);
        ItemStack next = new ItemStack(Material.ARROW);
        ItemMeta nextMeta = next.getItemMeta();
        nextMeta.setDisplayName(ChatColor.YELLOW + "Next Page »");
        next.setItemMeta(nextMeta);
        inv.setItem(53, next);
        player.openInventory(inv);
    }

    public static void openWaypointsMenu(Player player) {
        openWaypointsMenu(player, 1);
    }

    public static void openDeleteWaypointMenu(Player player, int page) {
        List<String> names = new ArrayList<>(HomesManager.getWaypointNames());
        int total = names.size();
        int maxPage = Math.max(1, (int) Math.ceil(total / (double) PER_PAGE));
        page = Math.max(1, Math.min(page, maxPage));
        Inventory inv = Bukkit.createInventory(null, MENU_SIZE, ChatColor.DARK_RED + "Delete Waypoint " + ChatColor.GRAY + "(Page " + ChatColor.YELLOW + page + ChatColor.GRAY + ")");
        int start = (page - 1) * PER_PAGE;
        for (int i = 0; i < PER_PAGE && (start + i) < total; i++) {
            String name = names.get(start + i);
            Material itemMaterial = HomesManager.getWaypointItem(name);
            ItemStack item = new ItemStack(itemMaterial);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(ChatColor.RED + "✖ " + ChatColor.GOLD + name);
            meta.setLore(java.util.Collections.singletonList(ChatColor.GRAY + "Click to delete this waypoint"));
            item.setItemMeta(meta);
            inv.setItem(i, item);
        }
        // Navigation buttons
        ItemStack prev = new ItemStack(Material.ARROW);
        ItemMeta prevMeta = prev.getItemMeta();
        prevMeta.setDisplayName(ChatColor.YELLOW + "« Previous Page");
        prev.setItemMeta(prevMeta);
        inv.setItem(45, prev);
        ItemStack next = new ItemStack(Material.ARROW);
        ItemMeta nextMeta = next.getItemMeta();
        nextMeta.setDisplayName(ChatColor.YELLOW + "Next Page »");
        next.setItemMeta(nextMeta);
        inv.setItem(53, next);
        player.openInventory(inv);
    }

    public static void openDeleteWaypointMenu(Player player) {
        openDeleteWaypointMenu(player, 1);
    }

    public static boolean handleWaypointMenuClick(InventoryClickEvent event) {
        String title = event.getView().getTitle();
        if (!title.startsWith(ChatColor.DARK_AQUA + "Waypoints " + ChatColor.GRAY + "(Page ")) return false;
        // Anulează orice click în meniu
        event.setCancelled(true);
        // Permite click doar în top inventory (meniu)
        if (event.getClickedInventory() == null || !event.getClickedInventory().equals(event.getView().getTopInventory())) return true;
        Player player = (Player) event.getWhoClicked();
        int page = 1;
        try {
            String pageStr = title.replaceAll(".*\\(Page " + ChatColor.YELLOW, "").replaceAll(ChatColor.GRAY + "\\).*", "");
            page = Integer.parseInt(pageStr);
        } catch (Exception ignored) {}
        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || !clicked.hasItemMeta()) return true;
        
        // Check if it's a waypoint item (any item that's not an arrow)
        if (clicked.getType() != Material.ARROW) {
            String displayName = clicked.getItemMeta().getDisplayName();
            // Extrage numele waypoint-ului din displayName
            String prefix = ChatColor.AQUA + "» " + ChatColor.GOLD;
            if (displayName.startsWith(prefix)) {
                String name = displayName.substring(prefix.length());
                var loc = HomesManager.getWaypoint(name);
                if (loc != null) {
                    player.teleport(loc);
                    MessageUtils.sendMessage(player, "teleported", "%name%", name);
                    player.closeInventory();
                }
            }
        } else if (clicked.getType() == Material.ARROW) {
            String btn = clicked.getItemMeta().getDisplayName();
            int total = HomesManager.getWaypointNames().size();
            int maxPage = Math.max(1, (int) Math.ceil(total / (double) PER_PAGE));
            if (btn.equals(ChatColor.YELLOW + "« Previous Page") && page > 1) {
                openWaypointsMenu(player, page - 1);
            } else if (btn.equals(ChatColor.YELLOW + "Next Page »") && page < maxPage) {
                openWaypointsMenu(player, page + 1);
            }
        }
        return true;
    }

    public static boolean handleDeleteMenuClick(InventoryClickEvent event) {
        String title = event.getView().getTitle();
        if (!title.startsWith(ChatColor.DARK_RED + "Delete Waypoint " + ChatColor.GRAY + "(Page ")) return false;
        // Anulează orice click în meniu
        event.setCancelled(true);
        if (event.getClickedInventory() == null || !event.getClickedInventory().equals(event.getView().getTopInventory())) return true;
        Player player = (Player) event.getWhoClicked();
        int page = 1;
        try {
            String pageStr = title.replaceAll(".*\\(Page " + ChatColor.YELLOW, "").replaceAll(ChatColor.GRAY + "\\).*", "");
            page = Integer.parseInt(pageStr);
        } catch (Exception ignored) {}
        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || !clicked.hasItemMeta()) return true;
        
        // Check if it's a waypoint item (any item that's not an arrow)
        if (clicked.getType() != Material.ARROW) {
            String displayName = clicked.getItemMeta().getDisplayName();
            String prefix = ChatColor.RED + "✖ " + ChatColor.GOLD;
            if (displayName.startsWith(prefix)) {
                String name = displayName.substring(prefix.length());
                boolean ok = HomesManager.removeWaypoint(name);
                if (ok) {
                    MessageUtils.sendMessage(player, "waypoint_deleted");
                } else {
                    MessageUtils.sendMessage(player, "no_waypoint");
                }
                player.closeInventory();
            }
        } else if (clicked.getType() == Material.ARROW) {
            String btn = clicked.getItemMeta().getDisplayName();
            int total = HomesManager.getWaypointNames().size();
            int maxPage = Math.max(1, (int) Math.ceil(total / (double) PER_PAGE));
            if (btn.equals(ChatColor.YELLOW + "« Previous Page") && page > 1) {
                openDeleteWaypointMenu(player, page - 1);
            } else if (btn.equals(ChatColor.YELLOW + "Next Page »") && page < maxPage) {
                openDeleteWaypointMenu(player, page + 1);
            }
        }
        return true;
    }

    public static void openHomesMenu(org.bukkit.entity.Player player) {
        List<HomeManager.Home> homes = HomeManager.getHomes(player);
        org.bukkit.inventory.Inventory inv = org.bukkit.Bukkit.createInventory(null, MENU_SIZE, org.bukkit.ChatColor.DARK_AQUA + "Home-uri");
        for (int i = 0; i < homes.size() && i < MAX_HOMES; i++) {
            HomeManager.Home home = homes.get(i);
            org.bukkit.inventory.ItemStack item = new org.bukkit.inventory.ItemStack(org.bukkit.Material.OAK_DOOR);
            org.bukkit.inventory.meta.ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(org.bukkit.ChatColor.AQUA + "» " + org.bukkit.ChatColor.GOLD + home.getName());
            meta.setLore(java.util.Collections.singletonList(org.bukkit.ChatColor.GRAY + "Click pentru teleportare"));
            item.setItemMeta(meta);
            inv.setItem(i, item);
        }
        player.openInventory(inv);
    }

    public static void openDeleteHomeMenu(org.bukkit.entity.Player player) {
        List<HomeManager.Home> homes = HomeManager.getHomes(player);
        org.bukkit.inventory.Inventory inv = org.bukkit.Bukkit.createInventory(null, MENU_SIZE, org.bukkit.ChatColor.DARK_RED + "Sterge Home");
        for (int i = 0; i < homes.size() && i < MAX_HOMES; i++) {
            HomeManager.Home home = homes.get(i);
            org.bukkit.inventory.ItemStack item = new org.bukkit.inventory.ItemStack(org.bukkit.Material.BARRIER);
            org.bukkit.inventory.meta.ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(org.bukkit.ChatColor.RED + "✖ " + org.bukkit.ChatColor.GOLD + home.getName());
            meta.setLore(java.util.Collections.singletonList(org.bukkit.ChatColor.GRAY + "Click pentru stergere"));
            item.setItemMeta(meta);
            inv.setItem(i, item);
        }
        player.openInventory(inv);
    }

    public static boolean handleHomeMenuClick(org.bukkit.event.inventory.InventoryClickEvent event) {
        String title = event.getView().getTitle();
        if (!title.startsWith(org.bukkit.ChatColor.DARK_AQUA + "Home-uri")) return false;
        event.setCancelled(true);
        if (event.getClickedInventory() == null || !event.getClickedInventory().equals(event.getView().getTopInventory())) return true;
        org.bukkit.entity.Player player = (org.bukkit.entity.Player) event.getWhoClicked();
        org.bukkit.inventory.ItemStack clicked = event.getCurrentItem();
        if (clicked == null || !clicked.hasItemMeta()) return true;
        if (clicked.getType() != org.bukkit.Material.OAK_DOOR) return true;
        String displayName = clicked.getItemMeta().getDisplayName();
        String prefix = org.bukkit.ChatColor.AQUA + "» " + org.bukkit.ChatColor.GOLD;
        if (displayName.startsWith(prefix)) {
            String name = displayName.substring(prefix.length());
            HomeManager.Home home = HomeManager.getHome(player, name);
            if (home != null) {
                player.teleport(home.getLocation());
                org.homes.homes.utils.MessageUtils.sendSuccess(player, "Teleportat la home-ul '" + name + "'.");
                player.closeInventory();
            }
        }
        return true;
    }

    public static boolean handleDeleteHomeMenuClick(org.bukkit.event.inventory.InventoryClickEvent event) {
        String title = event.getView().getTitle();
        if (!title.startsWith(org.bukkit.ChatColor.DARK_RED + "Sterge Home")) return false;
        event.setCancelled(true);
        if (event.getClickedInventory() == null || !event.getClickedInventory().equals(event.getView().getTopInventory())) return true;
        org.bukkit.entity.Player player = (org.bukkit.entity.Player) event.getWhoClicked();
        org.bukkit.inventory.ItemStack clicked = event.getCurrentItem();
        if (clicked == null || !clicked.hasItemMeta()) return true;
        if (clicked.getType() != org.bukkit.Material.BARRIER) return true;
        String displayName = clicked.getItemMeta().getDisplayName();
        String prefix = org.bukkit.ChatColor.RED + "✖ " + org.bukkit.ChatColor.GOLD;
        if (displayName.startsWith(prefix)) {
            String name = displayName.substring(prefix.length());
            boolean ok = HomeManager.removeHome(player, name);
            if (ok) {
                org.homes.homes.utils.MessageUtils.sendSuccess(player, "Home-ul a fost șters și banii returnați!");
            } else {
                org.homes.homes.utils.MessageUtils.sendError(player, "Nu există acest home!");
            }
            player.closeInventory();
        }
        return true;
    }

    public static void openAdminPlayersMenu(org.bukkit.entity.Player admin, int page) {
        java.util.List<java.util.UUID> uuids = new java.util.ArrayList<>(HomeManager.getPlayersWithHomes());
        int total = uuids.size();
        int maxPage = Math.max(1, (int) Math.ceil(total / (double) PER_PAGE));
        page = Math.max(1, Math.min(page, maxPage));
        org.bukkit.inventory.Inventory inv = org.bukkit.Bukkit.createInventory(null, ADMIN_MENU_SIZE, org.bukkit.ChatColor.DARK_PURPLE + "Jucatori cu Home-uri (" + page + "/" + maxPage + ")");
        int start = (page - 1) * PER_PAGE;
        for (int i = 0; i < PER_PAGE && (start + i) < total; i++) {
            java.util.UUID uuid = uuids.get(start + i);
            org.bukkit.OfflinePlayer offlinePlayer = org.bukkit.Bukkit.getOfflinePlayer(uuid);
            org.bukkit.inventory.ItemStack item = new org.bukkit.inventory.ItemStack(org.bukkit.Material.PLAYER_HEAD);
            org.bukkit.inventory.meta.ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(org.bukkit.ChatColor.GOLD + offlinePlayer.getName());
            meta.setLore(java.util.Collections.singletonList(org.bukkit.ChatColor.GRAY + "Click pentru a vedea home-urile"));
            item.setItemMeta(meta);
            inv.setItem(i, item);
        }
        // Navigare pagini
        org.bukkit.inventory.ItemStack prev = new org.bukkit.inventory.ItemStack(org.bukkit.Material.ARROW);
        org.bukkit.inventory.meta.ItemMeta prevMeta = prev.getItemMeta();
        prevMeta.setDisplayName(org.bukkit.ChatColor.YELLOW + "« Pagina anterioara");
        prev.setItemMeta(prevMeta);
        inv.setItem(45, prev);
        org.bukkit.inventory.ItemStack next = new org.bukkit.inventory.ItemStack(org.bukkit.Material.ARROW);
        org.bukkit.inventory.meta.ItemMeta nextMeta = next.getItemMeta();
        nextMeta.setDisplayName(org.bukkit.ChatColor.YELLOW + "Pagina urmatoare »");
        next.setItemMeta(nextMeta);
        inv.setItem(53, next);
        admin.openInventory(inv);
    }

    public static void openAdminHomesMenu(org.bukkit.entity.Player admin, java.util.UUID targetUuid) {
        java.util.Map<String, HomeManager.Home> homes = HomeManager.getHomes(targetUuid);
        org.bukkit.inventory.Inventory inv = org.bukkit.Bukkit.createInventory(null, MENU_SIZE, org.bukkit.ChatColor.DARK_AQUA + "Home-urile lui " + org.bukkit.Bukkit.getOfflinePlayer(targetUuid).getName());
        int i = 0;
        for (HomeManager.Home home : homes.values()) {
            if (i >= MAX_HOMES) break;
            org.bukkit.inventory.ItemStack item = new org.bukkit.inventory.ItemStack(org.bukkit.Material.BARRIER);
            org.bukkit.inventory.meta.ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(org.bukkit.ChatColor.RED + "✖ " + org.bukkit.ChatColor.GOLD + home.getName());
            meta.setLore(java.util.Collections.singletonList(org.bukkit.ChatColor.GRAY + "Click pentru stergere"));
            item.setItemMeta(meta);
            inv.setItem(i++, item);
        }
        admin.openInventory(inv);
    }

    public static boolean handleAdminPlayersMenuClick(org.bukkit.event.inventory.InventoryClickEvent event) {
        String title = event.getView().getTitle();
        if (!title.startsWith(org.bukkit.ChatColor.DARK_PURPLE + "Jucatori cu Home-uri")) return false;
        event.setCancelled(true);
        if (event.getClickedInventory() == null || !event.getClickedInventory().equals(event.getView().getTopInventory())) return true;
        org.bukkit.entity.Player admin = (org.bukkit.entity.Player) event.getWhoClicked();
        org.bukkit.inventory.ItemStack clicked = event.getCurrentItem();
        if (clicked == null || !clicked.hasItemMeta()) return true;
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
            } catch (Exception ignored) {}
            int total = HomeManager.getPlayersWithHomes().size();
            int maxPage = Math.max(1, (int) Math.ceil(total / (double) PER_PAGE));
            if (btn.equals(org.bukkit.ChatColor.YELLOW + "« Pagina anterioara") && page > 1) {
                openAdminPlayersMenu(admin, page - 1);
            } else if (btn.equals(org.bukkit.ChatColor.YELLOW + "Pagina urmatoare »") && page < maxPage) {
                openAdminPlayersMenu(admin, page + 1);
            }
        }
        return true;
    }

    public static boolean handleAdminHomesMenuClick(org.bukkit.event.inventory.InventoryClickEvent event) {
        String title = event.getView().getTitle();
        if (!title.startsWith(org.bukkit.ChatColor.DARK_AQUA + "Home-urile lui ")) return false;
        event.setCancelled(true);
        if (event.getClickedInventory() == null || !event.getClickedInventory().equals(event.getView().getTopInventory())) return true;
        org.bukkit.entity.Player admin = (org.bukkit.entity.Player) event.getWhoClicked();
        org.bukkit.inventory.ItemStack clicked = event.getCurrentItem();
        if (clicked == null || !clicked.hasItemMeta()) return true;
        if (clicked.getType() == org.bukkit.Material.BARRIER) {
            String displayName = clicked.getItemMeta().getDisplayName();
            String prefix = org.bukkit.ChatColor.RED + "✖ " + org.bukkit.ChatColor.GOLD;
            if (displayName.startsWith(prefix)) {
                String homeName = displayName.substring(prefix.length());
                String playerName = title.replace(org.bukkit.ChatColor.DARK_AQUA + "Home-urile lui ", "");
                org.bukkit.OfflinePlayer target = org.bukkit.Bukkit.getOfflinePlayer(playerName);
                if (target != null && target.getUniqueId() != null) {
                    boolean ok = HomeManager.getHomes(target.getUniqueId()).remove(homeName) != null;
                    if (ok) {
                        org.homes.homes.utils.MessageUtils.sendSuccess(admin, "Home-ul '" + homeName + "' a fost șters pentru " + playerName + ".");
                        HomeManager.saveHomes();
                        openAdminHomesMenu(admin, target.getUniqueId());
                    } else {
                        org.homes.homes.utils.MessageUtils.sendError(admin, "Nu există acest home la jucător!");
                    }
                }
            }
        }
        return true;
    }
}
