package org.homes.homes.homes;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.Material;
import org.bukkit.ChatColor;
import org.bukkit.event.inventory.InventoryClickEvent;
import java.util.*;

public class AdminHomeMenuManager {
    private static final int ADMIN_MENU_SIZE = 54;
    private static final int PER_PAGE = 36;

    // Admin edit homes menu with pagination and max homes button
    public static void openAdminEditHomesMenu(Player admin, UUID targetUuid, int page) {
        Map<String, HomeManager.Home> homes = HomeManager.getHomes(targetUuid);
        int totalHomes = homes.size();
        int maxPage = Math.max(1, (int) Math.ceil(totalHomes / 36.0));
        page = Math.max(1, Math.min(page, maxPage));
        Inventory inv = Bukkit.createInventory(null, 54,
                ChatColor.DARK_AQUA + "Edit Homes of "
                        + Bukkit.getOfflinePlayer(targetUuid).getName() + " (" + page + "/" + maxPage + ")");
        // Home items (first 36 slots)
        int start = (page - 1) * 36;
        int i = 0;
        for (HomeManager.Home home : homes.values()) {
            if (i >= 36)
                break;
            if (i + start >= totalHomes)
                break;
            if (i + start < 0)
                continue;
            if (i + start >= homes.size())
                break;
            if (i + start < homes.size()) {
                HomeManager.Home h = (HomeManager.Home) homes.values().toArray()[i + start];
                ItemStack item = new ItemStack(Material.OAK_DOOR);
                ItemMeta meta = item.getItemMeta();
                meta.setDisplayName(ChatColor.AQUA + "» " + ChatColor.GOLD + h.getName());
                meta.setLore(Collections.singletonList(ChatColor.GRAY + "Click to edit home"));
                item.setItemMeta(meta);
                inv.setItem(i, item);
            }
            i++;
        }
        // Pagination buttons
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
        // Max homes button
        ItemStack maxHomes = new ItemStack(Material.BOOK);
        ItemMeta maxHomesMeta = maxHomes.getItemMeta();
        maxHomesMeta.setDisplayName(ChatColor.LIGHT_PURPLE + "Set Max Homes");
        maxHomesMeta.setLore(Collections.singletonList(ChatColor.GRAY + "Click to set max homes for this player"));
        maxHomes.setItemMeta(maxHomesMeta);
        inv.setItem(49, maxHomes); // Central slot on bottom row
        admin.openInventory(inv);
    }

    // Handle clicks in the admin edit homes menu
    public static boolean handleAdminEditHomesMenuClick(InventoryClickEvent event) {
        String title = net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.legacySection()
                .serialize(event.getView().title());
        if (!title.startsWith(ChatColor.DARK_AQUA + "Edit Homes of "))
            return false;
        event.setCancelled(true);
        if (event.getClickedInventory() == null
                || !event.getClickedInventory().equals(event.getView().getTopInventory()))
            return true;
        Player admin = (Player) event.getWhoClicked();
        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || !clicked.hasItemMeta())
            return true;
        int slot = event.getSlot();
        // Extrage uuid-ul targetului din titlu
        String titleNoColors = ChatColor.stripColor(title);
        String namePart = titleNoColors.replaceFirst("Edit Homes of ", "");
        String playerName = namePart.split(" \\(")[0];
        org.bukkit.OfflinePlayer target = Bukkit.getOfflinePlayer(playerName);
        if (target == null || target.getUniqueId() == null)
            return true;
        int page = 1;
        try {
            String pageStr = titleNoColors.replaceAll(".*\\(", "").replaceAll("/.*", "");
            page = Integer.parseInt(pageStr);
        } catch (Exception ignored) {
        }
        if (clicked.getType() == Material.OAK_DOOR && slot < 36) {
            String displayName = clicked.getItemMeta().getDisplayName();
            String prefix = ChatColor.AQUA + "» " + ChatColor.GOLD;
            if (displayName.startsWith(prefix)) {
                String homeName = displayName.substring(prefix.length());
                openEditHomeMenu(admin, target.getUniqueId(), homeName);
            }
        } else if (clicked.getType() == Material.ARROW) {
            if (slot == 45 && page > 1) {
                openAdminEditHomesMenu(admin, target.getUniqueId(), page - 1);
            } else if (slot == 53) {
                int total = HomeManager.getHomes(target.getUniqueId()).size();
                int maxPage = Math.max(1, (int) Math.ceil(total / 36.0));
                if (page < maxPage) {
                    openAdminEditHomesMenu(admin, target.getUniqueId(), page + 1);
                }
            }
        } else if (clicked.getType() == Material.BOOK && slot == 49) {
            openSetMaxHomesMenu(admin, target.getUniqueId());
        }
        return true;
    }

    // Admin edit single home menu (set duration, delete, back)
    public static void openEditHomeMenu(Player admin, UUID targetUuid, String homeName) {
        String playerName = Bukkit.getOfflinePlayer(targetUuid).getName();
        int currentDuration = HomeManager.getHomeDuration(targetUuid, homeName);
        Inventory inv = Bukkit.createInventory(null, 27,
                ChatColor.BLUE + "Edit Home: " + homeName + " of " + playerName);
        // 10 sloturi pentru luni (1-10)
        for (int i = 0; i < 10; i++) {
            ItemStack item = new ItemStack(Material.CLOCK);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(ChatColor.GOLD + "Set duration: " + (i + 1) + " month" + ((i == 0) ? "" : "s"));
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + "Click to set duration to " + (i + 1) + " month" + ((i == 0) ? "" : "s"));
            if (i + 1 == currentDuration) {
                lore.add(ChatColor.GREEN + "Current value");
                meta.addEnchant(org.bukkit.enchantments.Enchantment.UNBREAKING, 1, true);
                meta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ENCHANTS);
            }
            meta.setLore(lore);
            item.setItemMeta(meta);
            inv.setItem(i, item);
        }
        // Buton de întoarcere
        ItemStack back = new ItemStack(Material.ARROW);
        ItemMeta backMeta = back.getItemMeta();
        backMeta.setDisplayName(ChatColor.YELLOW + "Back");
        back.setItemMeta(backMeta);
        inv.setItem(18, back);
        // Buton de ștergere
        ItemStack del = new ItemStack(Material.BARRIER);
        ItemMeta delMeta = del.getItemMeta();
        delMeta.setDisplayName(ChatColor.RED + "Delete Home");
        delMeta.setLore(Collections.singletonList(ChatColor.GRAY + "Click to delete this home permanently!"));
        del.setItemMeta(delMeta);
        inv.setItem(26, del);
        admin.openInventory(inv);
    }

    // Handle clicks in the edit home menu
    public static boolean handleEditHomeMenuClick(InventoryClickEvent event) {
        String title = net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.legacySection()
                .serialize(event.getView().title());
        if (!title.startsWith(ChatColor.BLUE + "Edit Home: "))
            return false;
        event.setCancelled(true);
        if (event.getClickedInventory() == null
                || !event.getClickedInventory().equals(event.getView().getTopInventory()))
            return true;
        Player admin = (Player) event.getWhoClicked();
        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || !clicked.hasItemMeta())
            return true;
        int slot = event.getSlot();
        // Extrage homeName și playerName din titlu
        String titleNoColors = ChatColor.stripColor(title);
        // Format: Edit Home: <homeName> of <playerName>
        String rest = titleNoColors.replaceFirst("Edit Home: ", "");
        int idx = rest.lastIndexOf(" of ");
        if (idx == -1)
            return true;
        String homeName = rest.substring(0, idx);
        String playerName = rest.substring(idx + 4);
        org.bukkit.OfflinePlayer target = Bukkit.getOfflinePlayer(playerName);
        if (target == null || target.getUniqueId() == null)
            return true;
        if (clicked.getType() == Material.CLOCK && slot < 10) {
            int months = slot + 1;
            HomeManager.setHomeDuration(target.getUniqueId(), homeName, months);
            admin.sendMessage("Durata pentru home '" + homeName + "' a jucătorului '" + playerName
                    + "' a fost setată la " + months + " luni.");
            openEditHomeMenu(admin, target.getUniqueId(), homeName);
        } else if (clicked.getType() == Material.ARROW && slot == 18) {
            openAdminEditHomesMenu(admin, target.getUniqueId(), 1);
        } else if (clicked.getType() == Material.BARRIER && slot == 26) {
            boolean ok = HomeManager.removeHome(target.getUniqueId(), homeName);
            if (ok) {
                org.homes.homes.utils.MessageUtils.sendSuccess(admin, "Home-ul a fost șters!");
            } else {
                org.homes.homes.utils.MessageUtils.sendError(admin, "Acest home nu există!");
            }
            openAdminEditHomesMenu(admin, target.getUniqueId(), 1);
        }
        return true;
    }

    // Admin set max homes menu
    public static void openSetMaxHomesMenu(Player admin, UUID targetUuid) {
        String playerName = Bukkit.getOfflinePlayer(targetUuid).getName();
        int currentMax = HomeManager.getMaxHomes(targetUuid);
        Inventory inv = Bukkit.createInventory(null, 18,
                ChatColor.LIGHT_PURPLE + "Set Max Homes for " + playerName);
        // 10 sloturi pentru valori 1-10
        for (int i = 0; i < 10; i++) {
            ItemStack item = new ItemStack(Material.PAPER);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(ChatColor.GOLD + "Set max homes: " + (i + 1));
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + "Click to set max homes to " + (i + 1));
            if (i + 1 == currentMax) {
                lore.add(ChatColor.GREEN + "Current value");
                meta.addEnchant(org.bukkit.enchantments.Enchantment.UNBREAKING, 1, true);
                meta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ENCHANTS);
            }
            meta.setLore(lore);
            item.setItemMeta(meta);
            inv.setItem(i, item);
        }
        // Buton de întoarcere
        ItemStack back = new ItemStack(Material.ARROW);
        ItemMeta backMeta = back.getItemMeta();
        backMeta.setDisplayName(ChatColor.YELLOW + "Back");
        back.setItemMeta(backMeta);
        inv.setItem(17, back);
        admin.openInventory(inv);
    }

    // Handle clicks in the set max homes menu
    public static boolean handleSetMaxHomesMenuClick(InventoryClickEvent event) {
        String title = net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.legacySection()
                .serialize(event.getView().title());
        if (!title.startsWith(ChatColor.LIGHT_PURPLE + "Set Max Homes for "))
            return false;
        event.setCancelled(true);
        if (event.getClickedInventory() == null
                || !event.getClickedInventory().equals(event.getView().getTopInventory()))
            return true;
        Player admin = (Player) event.getWhoClicked();
        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || !clicked.hasItemMeta())
            return true;
        int slot = event.getSlot();
        String titleNoColors = ChatColor.stripColor(title);
        // Format: Set Max Homes for <playerName>
        String playerName = titleNoColors.replaceFirst("Set Max Homes for ", "");
        org.bukkit.OfflinePlayer target = Bukkit.getOfflinePlayer(playerName);
        if (target == null || target.getUniqueId() == null)
            return true;
        if (clicked.getType() == Material.PAPER && slot < 10) {
            int maxHomes = slot + 1;
            HomeManager.setMaxHomes(target.getUniqueId(), maxHomes);
            admin.sendMessage("Limita de home-uri pentru '" + playerName + "' a fost setată la " + maxHomes + ".");
            openSetMaxHomesMenu(admin, target.getUniqueId());
        } else if (clicked.getType() == Material.ARROW && slot == 17) {
            openAdminEditHomesMenu(admin, target.getUniqueId(), 1);
        }
        return true;
    }
}