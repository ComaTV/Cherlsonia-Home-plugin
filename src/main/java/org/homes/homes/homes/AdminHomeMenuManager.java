package org.homes.homes.homes;

import java.util.List;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.homes.homes.config.ConfigManager;

public class AdminHomeMenuManager {
    private static ConfigManager configManager;
    private static final int ADMIN_MENU_SIZE = 54;
    private static final int PER_PAGE = 36;

    public static void setConfigManager(ConfigManager configManager) {
        AdminHomeMenuManager.configManager = configManager;
    }

    public static void openAdminHomesMenu(Player admin, UUID targetUuid, int page) {
        java.util.Map<String, HomeManager.Home> homes = HomeManager.getHomes(targetUuid);
        int totalHomes = homes.size();
        int maxPage = Math.max(1, (int) Math.ceil(totalHomes / 36.0));
        page = Math.max(1, Math.min(page, maxPage));
        Inventory inv = Bukkit.createInventory(null, 54,
                ChatColor.DARK_AQUA + "Homes of "
                        + Bukkit.getOfflinePlayer(targetUuid).getName() + " (" + page + "/" + maxPage + ")");
        int start = (page - 1) * 36;
        java.util.List<HomeManager.Home> homeList = new java.util.ArrayList<>(homes.values());
        for (int i = 0; i < 36 && (start + i) < totalHomes; i++) {
            HomeManager.Home home = homeList.get(start + i);
            ItemStack item = new ItemStack(Material.OAK_DOOR);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(ChatColor.GOLD + home.getName());
            meta.setLore(java.util.Arrays.asList(
                    ChatColor.GRAY + "Duration: " + home.getDurationMonths() + " months",
                    ChatColor.GRAY + "Click to edit/delete"));
            item.setItemMeta(meta);
            inv.setItem(i, item);
        }
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
        ItemStack maxHomesBtn = new ItemStack(Material.BOOK);
        ItemMeta maxHomesMeta = maxHomesBtn.getItemMeta();
        maxHomesMeta.setDisplayName(ChatColor.GREEN + "Edit Max Homes");
        maxHomesMeta.setLore(java.util.Arrays.asList(
                ChatColor.GRAY + "Click to edit maximum",
                ChatColor.GRAY + "homes per player"));
        maxHomesBtn.setItemMeta(maxHomesMeta);
        inv.setItem(49, maxHomesBtn);
        admin.openInventory(inv);
    }

    public static boolean handleAdminHomesMenuClick(InventoryClickEvent event) {
        String title = net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.legacySection()
                .serialize(event.getView().title());
        String titleNoColors = ChatColor.stripColor(title);
        if (!titleNoColors.startsWith("Homes of "))
            return false;
        event.setCancelled(true);
        if (event.getClickedInventory() == null
                || !event.getClickedInventory().equals(event.getView().getTopInventory()))
            return true;
        Player player = (Player) event.getWhoClicked();
        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || !clicked.hasItemMeta())
            return true;
        int slot = event.getSlot();
        if (clicked.getType() == Material.ARROW) {
            String titleNoColorsArrow = ChatColor.stripColor(title);
            int page = 1;
            try {
                String pageStr = titleNoColorsArrow.replaceAll(".*\\(", "").replaceAll("/.*", "");
                page = Integer.parseInt(pageStr);
            } catch (Exception ignored) {
            }
            String namePart = titleNoColorsArrow.replaceFirst("Homes of ", "");
            String playerName = namePart.split(" \\(")[0];
            org.bukkit.OfflinePlayer target = Bukkit.getOfflinePlayer(playerName);
            if (target != null && target.getUniqueId() != null) {
                int total = HomeManager.getHomes(target.getUniqueId()).size();
                int maxPage = Math.max(1, (int) Math.ceil(total / 36.0));
                if (slot == 45 && page > 1) {
                    openAdminHomesMenu(player, target.getUniqueId(), page - 1);
                } else if (slot == 53 && page < maxPage) {
                    openAdminHomesMenu(player, target.getUniqueId(), page + 1);
                }
            }
            return true;
        }
        if (clicked.getType() == Material.BOOK) {
            String displayName = clicked.getItemMeta().getDisplayName();
            if (displayName.equals(ChatColor.GREEN + "Edit Max Homes")) {
                String titleNoColorsBook = ChatColor.stripColor(title);
                String namePart = titleNoColorsBook.replaceFirst("Homes of ", "");
                String playerName = namePart.split(" \\(")[0];
                org.bukkit.OfflinePlayer target = Bukkit.getOfflinePlayer(playerName);
                if (target != null && target.getUniqueId() != null) {
                    openMaxHomesEditMenu(player, target.getUniqueId());
                }
                return true;
            }
        }
        if (clicked.getType() == Material.OAK_DOOR) {
            String displayName = clicked.getItemMeta().getDisplayName();
            String homeName = ChatColor.stripColor(displayName);
            String titleNoColorsDoor = ChatColor.stripColor(title);
            String namePart = titleNoColorsDoor.replaceFirst("Homes of ", "");
            String playerName = namePart.split(" \\(")[0];
            org.bukkit.OfflinePlayer target = Bukkit.getOfflinePlayer(playerName);
            if (target != null && target.getUniqueId() != null) {
                openAdminHomeEditMenu(player, target.getUniqueId(), homeName);
            }
            return true;
        }
        return true;

    }

    public static void openAdminHomeEditMenu(Player admin, UUID targetUuid, String homeName) {
        String playerName = Bukkit.getOfflinePlayer(targetUuid).getName();
        int currentDuration = HomeManager.getHomeDuration(targetUuid, homeName);
        Inventory inv = Bukkit.createInventory(null, 27,
                ChatColor.DARK_AQUA + "Edit Home: " + homeName + " of " + playerName);

        ItemStack editBtn = new ItemStack(Material.CLOCK);
        ItemMeta editMeta = editBtn.getItemMeta();
        editMeta.setDisplayName(ChatColor.YELLOW + "Edit Duration");
        editMeta.setLore(java.util.Arrays.asList(
                ChatColor.GRAY + "Current: " + currentDuration + " months",
                ChatColor.GRAY + "Click to change duration"));
        editBtn.setItemMeta(editMeta);
        inv.setItem(11, editBtn);

        ItemStack deleteBtn = new ItemStack(Material.BARRIER);
        ItemMeta deleteMeta = deleteBtn.getItemMeta();
        deleteMeta.setDisplayName(ChatColor.RED + "Delete Home");
        deleteMeta.setLore(java.util.Arrays.asList(
                ChatColor.GRAY + "Click to delete this home",
                ChatColor.GRAY + "Player will be refunded"));
        deleteBtn.setItemMeta(deleteMeta);
        inv.setItem(15, deleteBtn);

        ItemStack back = new ItemStack(Material.ARROW);
        ItemMeta backMeta = back.getItemMeta();
        backMeta.setDisplayName(ChatColor.YELLOW + "Back to Homes");
        back.setItemMeta(backMeta);
        inv.setItem(22, back);

        admin.openInventory(inv);
    }

    public static boolean handleAdminHomeEditMenuClick(InventoryClickEvent event) {
        String title = net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.legacySection()
                .serialize(event.getView().title());
        if (!title.startsWith(ChatColor.DARK_AQUA + "Edit Home: "))
            return false;
        event.setCancelled(true);
        if (event.getClickedInventory() == null
                || !event.getClickedInventory().equals(event.getView().getTopInventory()))
            return true;
        Player admin = (Player) event.getWhoClicked();
        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || !clicked.hasItemMeta())
            return true;

        String titleNoColors = ChatColor.stripColor(title);
        String rest = titleNoColors.replaceFirst("Edit Home: ", "");
        int idx = rest.lastIndexOf(" of ");
        String homeName = rest.substring(0, idx);
        String playerName = rest.substring(idx + 4);
        org.bukkit.OfflinePlayer target = Bukkit.getOfflinePlayer(playerName);
        if (target == null || target.getUniqueId() == null)
            return true;

        if (clicked.getType() == Material.ARROW) {
            String displayName = clicked.getItemMeta().getDisplayName();
            if (displayName.equals(ChatColor.YELLOW + "Back to Homes")) {
                openAdminHomesMenu(admin, target.getUniqueId(), 1);
                return true;
            }
        }

        if (clicked.getType() == Material.CLOCK) {
            String displayName = clicked.getItemMeta().getDisplayName();
            if (displayName.equals(ChatColor.YELLOW + "Edit Duration")) {
                openAdminHomeDurationMenu(admin, target.getUniqueId(), homeName);
                return true;
            }
        }

        if (clicked.getType() == Material.BARRIER) {
            String displayName = clicked.getItemMeta().getDisplayName();
            if (displayName.equals(ChatColor.RED + "Delete Home")) {
                openAdminHomeDeleteConfirmMenu(admin, target.getUniqueId(), homeName);
                return true;
            }
        }

        return true;
    }

    public static void openAdminHomeDurationMenu(Player admin, UUID targetUuid, String homeName) {
        int currentDuration = HomeManager.getHomeDuration(targetUuid, homeName);
        Inventory inv = Bukkit.createInventory(null, 27,
                ChatColor.BLUE + "Set Duration for " + homeName + " (Months: " + currentDuration
                        + ")");

        for (int i = 0; i < 10; i++) {
            ItemStack item = new ItemStack(Material.CLOCK);
            ItemMeta meta = item.getItemMeta();
            int value = i + 1;
            if (value == currentDuration) {
                meta.setDisplayName(ChatColor.GREEN + "✓ " + ChatColor.GOLD + value + " Months");
                item.setType(Material.EMERALD);
            } else {
                meta.setDisplayName(ChatColor.YELLOW + "Set to " + ChatColor.GOLD + value + " Months");
            }
            meta.setLore(java.util.Collections
                    .singletonList(ChatColor.GRAY + "Click to set duration to " + value + " months"));
            item.setItemMeta(meta);
            inv.setItem(i, item);
        }

        ItemStack back = new ItemStack(Material.ARROW);
        ItemMeta backMeta = back.getItemMeta();
        backMeta.setDisplayName(ChatColor.YELLOW + "Back");
        back.setItemMeta(backMeta);
        inv.setItem(18, back);

        admin.openInventory(inv);
    }

    public static boolean handleAdminHomeDurationMenuClick(InventoryClickEvent event) {
        String title = net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.legacySection()
                .serialize(event.getView().title());
        if (!title.startsWith(ChatColor.BLUE + "Set Duration for "))
            return false;
        event.setCancelled(true);
        if (event.getClickedInventory() == null
                || !event.getClickedInventory().equals(event.getView().getTopInventory()))
            return true;
        Player admin = (Player) event.getWhoClicked();
        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || !clicked.hasItemMeta())
            return true;

        String titleNoColors = ChatColor.stripColor(title);
        String rest = titleNoColors.replaceFirst("Set Duration for ", "");
        int idx = rest.lastIndexOf(" of ");
        String homeName = rest.substring(0, idx);
        String playerName = rest.substring(idx + 4).split(" \\(Current:")[0];
        org.bukkit.OfflinePlayer target = Bukkit.getOfflinePlayer(playerName);
        if (target == null || target.getUniqueId() == null)
            return true;

        if (clicked.getType() == Material.ARROW) {
            String displayName = clicked.getItemMeta().getDisplayName();
            if (displayName.equals(ChatColor.YELLOW + "Back")) {
                openAdminHomeEditMenu(admin, target.getUniqueId(), homeName);
                return true;
            }
        }

        if (clicked.getType() == Material.CLOCK || clicked.getType() == Material.EMERALD) {
            int slot = event.getSlot();
            if (slot >= 0 && slot < 10) {
                int newDuration = slot + 1;
                HomeManager.setHomeDuration(target.getUniqueId(), homeName, newDuration);
                org.homes.homes.utils.MessageUtils.sendSuccess(admin,
                        "Duration for home '" + homeName + "' of " + playerName + " set to " + newDuration
                                + " months!");
                openAdminHomeDurationMenu(admin, target.getUniqueId(), homeName);
                return true;
            }
        }

        return true;
    }

    public static void openAdminPlayersMenu(Player admin, int page) {
        java.util.List<UUID> uuids = new java.util.ArrayList<>(HomeManager.getPlayersWithHomes());
        int total = uuids.size();
        int maxPage = Math.max(1, (int) Math.ceil(total / (double) PER_PAGE));
        page = Math.max(1, Math.min(page, maxPage));
        Inventory inv = Bukkit.createInventory(null, ADMIN_MENU_SIZE,
                ChatColor.DARK_PURPLE + "Players with Homes (" + page + "/" + maxPage + ")");
        int start = (page - 1) * PER_PAGE;
        for (int i = 0; i < PER_PAGE && (start + i) < total; i++) {
            UUID uuid = uuids.get(start + i);
            org.bukkit.OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
            ItemStack item = new ItemStack(Material.PLAYER_HEAD);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(ChatColor.GOLD + offlinePlayer.getName());
            meta.setLore(java.util.Collections.singletonList(ChatColor.GRAY + "Click to view homes"));
            item.setItemMeta(meta);
            inv.setItem(i, item);
        }
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
        admin.openInventory(inv);
    }

    public static boolean handleAdminPlayersMenuClick(InventoryClickEvent event) {
        String title = net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.legacySection()
                .serialize(event.getView().title());
        if (!title.startsWith(ChatColor.DARK_PURPLE + "Players with Homes"))
            return false;
        event.setCancelled(true);
        if (event.getClickedInventory() == null
                || !event.getClickedInventory().equals(event.getView().getTopInventory()))
            return true;
        Player admin = (Player) event.getWhoClicked();
        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || !clicked.hasItemMeta())
            return true;
        if (clicked.getType() == Material.PLAYER_HEAD) {
            String displayName = clicked.getItemMeta().getDisplayName();
            String playerName = ChatColor.stripColor(displayName);
            org.bukkit.OfflinePlayer target = Bukkit.getOfflinePlayer(playerName);
            if (target != null && target.getUniqueId() != null) {
                openAdminHomesMenu(admin, target.getUniqueId(), 1);
            }
        } else if (clicked.getType() == Material.ARROW) {
            String btn = clicked.getItemMeta().getDisplayName();
            int page = 1;
            try {
                String pageStr = title.replaceAll(".*\\(", "").replaceAll("/.*", "");
                page = Integer.parseInt(pageStr);
            } catch (Exception ignored) {
            }
            int total = HomeManager.getPlayersWithHomes().size();
            int maxPage = Math.max(1, (int) Math.ceil(total / (double) PER_PAGE));
            if (btn.equals(ChatColor.YELLOW + "« Previous Page") && page > 1) {
                openAdminPlayersMenu(admin, page - 1);
            } else if (btn.equals(ChatColor.YELLOW + "Next Page »") && page < maxPage) {
                openAdminPlayersMenu(admin, page + 1);
            }
        }
        return true;
    }

    public static void openMaxHomesEditMenu(Player admin, UUID targetUuid) {
        int currentMax = HomeManager.getMaxHomes(targetUuid);
        String playerName = Bukkit.getOfflinePlayer(targetUuid).getName();
        Inventory inv = Bukkit.createInventory(null, 27,
                ChatColor.DARK_GREEN + "Edit Max Homes for (Current: " + currentMax + ")");

        for (int i = 0; i < 10; i++) {
            ItemStack item = new ItemStack(Material.PAPER);
            ItemMeta meta = item.getItemMeta();
            int value = i + 1;
            if (value == currentMax) {
                meta.setDisplayName(ChatColor.GREEN + "✓ " + ChatColor.GOLD + value + " Homes");
                item.setType(Material.EMERALD);
            } else {
                meta.setDisplayName(
                        ChatColor.YELLOW + "Set to " + ChatColor.GOLD + value + " Homes");
            }
            meta.setLore(java.util.Collections
                    .singletonList(ChatColor.GRAY + "Click to set max homes to " + value));
            item.setItemMeta(meta);
            inv.setItem(i, item);
        }

        ItemStack back = new ItemStack(Material.ARROW);
        ItemMeta backMeta = back.getItemMeta();
        backMeta.setDisplayName(ChatColor.YELLOW + "Back to Admin Menu");
        back.setItemMeta(backMeta);
        inv.setItem(18, back);

        admin.openInventory(inv);
    }

    public static boolean handleMaxHomesEditMenuClick(InventoryClickEvent event) {
        String title = net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.legacySection()
                .serialize(event.getView().title());
        if (!title.startsWith(ChatColor.DARK_GREEN + "Edit Max Homes for "))
            return false;
        event.setCancelled(true);
        if (event.getClickedInventory() == null
                || !event.getClickedInventory().equals(event.getView().getTopInventory()))
            return true;
        Player player = (Player) event.getWhoClicked();
        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || !clicked.hasItemMeta())
            return true;

        String titleNoColors = ChatColor.stripColor(title);
        String playerName = titleNoColors.replaceFirst("Edit Max Homes for ", "").split(" \\(Current:")[0];
        org.bukkit.OfflinePlayer target = Bukkit.getOfflinePlayer(playerName);
        if (target == null || target.getUniqueId() == null)
            return true;

        if (clicked.getType() == Material.ARROW) {
            String displayName = clicked.getItemMeta().getDisplayName();
            if (displayName.equals(ChatColor.YELLOW + "Back to Admin Menu")) {
                openAdminPlayersMenu(player, 1);
                return true;
            }
        }

        if (clicked.getType() == Material.PAPER || clicked.getType() == Material.EMERALD) {
            String displayName = clicked.getItemMeta().getDisplayName();
            int slot = event.getSlot();
            if (slot >= 0 && slot < 10) {
                int newMax = slot + 1;
                HomeManager.setMaxHomes(target.getUniqueId(), newMax);
                org.homes.homes.utils.MessageUtils.sendSuccess(player,
                        "Maximum homes for " + playerName + " set to " + newMax + "!");
                openMaxHomesEditMenu(player, target.getUniqueId());
                return true;
            }
        }

        return true;
    }

    public static void openAdminEditHomesMenu(Player admin, UUID targetUuid, int page) {
        java.util.Map<String, HomeManager.Home> homes = HomeManager.getHomes(targetUuid);
        int totalHomes = homes.size();
        int maxPage = Math.max(1, (int) Math.ceil(totalHomes / 36.0));
        page = Math.max(1, Math.min(page, maxPage));
        String playerName = Bukkit.getOfflinePlayer(targetUuid).getName();
        Inventory inv = Bukkit.createInventory(null, 54,
                ChatColor.BLUE + "Edit Homes of " + playerName + " (" + page + "/" + maxPage + ")");
        int start = (page - 1) * 36;
        java.util.List<HomeManager.Home> homeList = new java.util.ArrayList<>(homes.values());
        for (int i = 0; i < 36 && (start + i) < totalHomes; i++) {
            HomeManager.Home home = homeList.get(start + i);
            ItemStack item = new ItemStack(Material.OAK_DOOR);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(ChatColor.GOLD + home.getName());
            meta.setLore(java.util.Arrays.asList(
                    ChatColor.GRAY + "Duration: " + home.getDurationMonths() + " months",
                    ChatColor.GRAY + "Click to edit duration"));
            item.setItemMeta(meta);
            inv.setItem(i, item);
        }
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
        ItemStack maxHomesBtn = new ItemStack(Material.BOOK);
        ItemMeta maxHomesMeta = maxHomesBtn.getItemMeta();
        maxHomesMeta.setDisplayName(ChatColor.GREEN + "Set Max Homes");
        maxHomesMeta.setLore(java.util.Arrays.asList(
                ChatColor.GRAY + "Click to set maximum",
                ChatColor.GRAY + "homes for this player"));
        maxHomesBtn.setItemMeta(maxHomesMeta);
        inv.setItem(49, maxHomesBtn);
        admin.openInventory(inv);
    }

    public static boolean handleAdminEditHomesMenuClick(InventoryClickEvent event) {
        String title = net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.legacySection()
                .serialize(event.getView().title());
        if (!title.startsWith(ChatColor.BLUE + "Edit Homes of "))
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
        if (clicked.getType() == Material.ARROW) {
            String titleNoColors = ChatColor.stripColor(title);
            int page = 1;
            try {
                String pageStr = titleNoColors.replaceAll(".*\\(", "").replaceAll("/.*", "");
                page = Integer.parseInt(pageStr);
            } catch (Exception ignored) {
            }
            String namePart = titleNoColors.replaceFirst("Edit Homes of ", "");
            String playerName = namePart.split(" \\(")[0];
            org.bukkit.OfflinePlayer target = Bukkit.getOfflinePlayer(playerName);
            if (target != null && target.getUniqueId() != null) {
                int total = HomeManager.getHomes(target.getUniqueId()).size();
                int maxPage = Math.max(1, (int) Math.ceil(total / 36.0));
                if (slot == 45 && page > 1) {
                    openAdminEditHomesMenu(admin, target.getUniqueId(), page - 1);
                } else if (slot == 53 && page < maxPage) {
                    openAdminEditHomesMenu(admin, target.getUniqueId(), page + 1);
                }
            }
            return true;
        }
        if (clicked.getType() == Material.BOOK) {
            String displayName = clicked.getItemMeta().getDisplayName();
            if (displayName.equals(ChatColor.GREEN + "Set Max Homes")) {
                String titleNoColors = ChatColor.stripColor(title);
                String namePart = titleNoColors.replaceFirst("Edit Homes of ", "");
                String playerName = namePart.split(" \\(")[0];
                org.bukkit.OfflinePlayer target = Bukkit.getOfflinePlayer(playerName);
                if (target != null && target.getUniqueId() != null) {
                    openSetMaxHomesMenu(admin, target.getUniqueId());
                }
                return true;
            }
        }
        if (clicked.getType() == Material.OAK_DOOR) {
            String displayName = clicked.getItemMeta().getDisplayName();
            String homeName = ChatColor.stripColor(displayName);
            String titleNoColors = ChatColor.stripColor(title);
            String namePart = titleNoColors.replaceFirst("Edit Homes of ", "");
            String playerName = namePart.split(" \\(")[0];
            org.bukkit.OfflinePlayer target = Bukkit.getOfflinePlayer(playerName);
            if (target != null && target.getUniqueId() != null) {
                openAdminHomeEditMenu(admin, target.getUniqueId(), homeName);
            }
            return true;
        }
        return true;
    }

    public static void openEditHomeMenu(Player admin, UUID targetUuid, String homeName) {
        String playerName = Bukkit.getOfflinePlayer(targetUuid).getName();
        int currentDuration = HomeManager.getHomeDuration(targetUuid, homeName);
        Inventory inv = Bukkit.createInventory(null, 27,
                ChatColor.BLUE + "Edit Home: " + homeName + " of " + playerName);
        for (int i = 0; i < 10; i++) {
            ItemStack item = new ItemStack(Material.CLOCK);
            ItemMeta meta = item.getItemMeta();
            int value = i + 1;
            if (value == currentDuration) {
                meta.setDisplayName(ChatColor.GREEN + "✓ " + ChatColor.GOLD + value + " Months");
                item.setType(Material.EMERALD);
            } else {
                meta.setDisplayName(ChatColor.YELLOW + "Set to " + ChatColor.GOLD + value + " Months");
            }
            meta.setLore(java.util.Collections
                    .singletonList(ChatColor.GRAY + "Click to set duration to " + value + " months"));
            item.setItemMeta(meta);
            inv.setItem(i, item);
        }
        ItemStack back = new ItemStack(Material.ARROW);
        ItemMeta backMeta = back.getItemMeta();
        backMeta.setDisplayName(ChatColor.YELLOW + "Back");
        back.setItemMeta(backMeta);
        inv.setItem(18, back);
        ItemStack del = new ItemStack(Material.BARRIER);
        ItemMeta delMeta = del.getItemMeta();
        delMeta.setDisplayName(ChatColor.RED + "Delete Home");
        del.setItemMeta(delMeta);
        inv.setItem(26, del);
        admin.openInventory(inv);
    }

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
        String titleNoColors = ChatColor.stripColor(title);
        String rest = titleNoColors.replaceFirst("Edit Home: ", "");
        int idx = rest.lastIndexOf(" of ");
        String homeName = rest.substring(0, idx);
        String playerName = rest.substring(idx + 4);
        org.bukkit.OfflinePlayer target = Bukkit.getOfflinePlayer(playerName);
        if (target == null || target.getUniqueId() == null)
            return true;
        if (clicked.getType() == Material.ARROW) {
            String displayName = clicked.getItemMeta().getDisplayName();
            if (displayName.equals(ChatColor.YELLOW + "Back")) {
                openAdminEditHomesMenu(admin, target.getUniqueId(), 1);
                return true;
            }
        }
        if (clicked.getType() == Material.BARRIER) {
            String displayName = clicked.getItemMeta().getDisplayName();
            if (displayName.equals(ChatColor.RED + "Delete Home")) {
                boolean ok = HomeManager.removeHome(target.getUniqueId(), homeName);
                if (ok) {
                    admin.sendMessage("Duration for home '" + homeName + "' of player '" + playerName
                            + "' has been deleted.");
                    openAdminEditHomesMenu(admin, target.getUniqueId(), 1);
                } else {
                    org.homes.homes.utils.MessageUtils.sendError(admin, "This home does not exist!");
                }
                return true;
            }
        }
        if (clicked.getType() == Material.CLOCK || clicked.getType() == Material.EMERALD) {
            if (slot >= 0 && slot < 10) {
                int newDuration = slot + 1;
                HomeManager.setHomeDuration(target.getUniqueId(), homeName, newDuration);
                admin.sendMessage("Duration for home '" + homeName + "' of player '" + playerName
                        + "' has been set to " + newDuration + " months.");
                openEditHomeMenu(admin, target.getUniqueId(), homeName);
                return true;
            }
        }
        return true;
    }

    public static void openSetMaxHomesMenu(Player admin, UUID targetUuid) {
        String playerName = Bukkit.getOfflinePlayer(targetUuid).getName();
        int currentMax = HomeManager.getMaxHomes(targetUuid);
        Inventory inv = Bukkit.createInventory(null, 27,
                ChatColor.DARK_GREEN + "Set Max Homes for " + playerName + " (Current: " + currentMax + ")");
        for (int i = 0; i < 10; i++) {
            ItemStack item = new ItemStack(Material.PAPER);
            ItemMeta meta = item.getItemMeta();
            int value = i + 1;
            if (value == currentMax) {
                meta.setDisplayName(ChatColor.GREEN + "✓ " + ChatColor.GOLD + value + " Homes");
                item.setType(Material.EMERALD);
            } else {
                meta.setDisplayName(ChatColor.YELLOW + "Set to " + ChatColor.GOLD + value + " Homes");
            }
            meta.setLore(java.util.Collections.singletonList(ChatColor.GRAY + "Click to set max homes to " + value));
            item.setItemMeta(meta);
            inv.setItem(i, item);
        }
        ItemStack back = new ItemStack(Material.ARROW);
        ItemMeta backMeta = back.getItemMeta();
        backMeta.setDisplayName(ChatColor.YELLOW + "Back");
        back.setItemMeta(backMeta);
        inv.setItem(18, back);
        admin.openInventory(inv);
    }

    public static boolean handleSetMaxHomesMenuClick(InventoryClickEvent event) {
        String title = net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.legacySection()
                .serialize(event.getView().title());
        if (!title.startsWith(ChatColor.DARK_GREEN + "Set Max Homes for "))
            return false;
        event.setCancelled(true);
        if (event.getClickedInventory() == null
                || !event.getClickedInventory().equals(event.getView().getTopInventory()))
            return true;
        Player admin = (Player) event.getWhoClicked();
        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || !clicked.hasItemMeta())
            return true;
        String titleNoColors = ChatColor.stripColor(title);
        String playerName = titleNoColors.replaceFirst("Set Max Homes for ", "").split(" \\(Current:")[0];
        org.bukkit.OfflinePlayer target = Bukkit.getOfflinePlayer(playerName);
        if (target == null || target.getUniqueId() == null)
            return true;
        if (clicked.getType() == Material.ARROW) {
            String displayName = clicked.getItemMeta().getDisplayName();
            if (displayName.equals(ChatColor.YELLOW + "Back")) {
                openAdminEditHomesMenu(admin, target.getUniqueId(), 1);
                return true;
            }
        }
        if (clicked.getType() == Material.PAPER || clicked.getType() == Material.EMERALD) {
            int slot = event.getSlot();
            if (slot >= 0 && slot < 10) {
                int maxHomes = slot + 1;
                HomeManager.setMaxHomes(target.getUniqueId(), maxHomes);
                admin.sendMessage("Home limit for '" + playerName + "' has been set to " + maxHomes + ".");
                openSetMaxHomesMenu(admin, target.getUniqueId());
                return true;
            }
        }
        return true;
    }

    public static void openAdminHomeDeleteConfirmMenu(Player admin, UUID targetUuid, String homeName) {
        String playerName = Bukkit.getOfflinePlayer(targetUuid).getName();
        int currentDuration = HomeManager.getHomeDuration(targetUuid, homeName);
        int pricePerMonth = configManager.getHomePrice();
        int refundAmount = currentDuration * pricePerMonth;
        Inventory inv = Bukkit.createInventory(null, 27,
                ChatColor.RED + "Are you sure you want to delete" + homeName + " ?");

        ItemStack info = new ItemStack(Material.PAPER);
        ItemMeta infoMeta = info.getItemMeta();
        infoMeta.setDisplayName(ChatColor.YELLOW + "Home Information");
        infoMeta.setLore(java.util.Arrays.asList(
                ChatColor.GRAY + "Home: " + ChatColor.WHITE + homeName,
                ChatColor.GRAY + "Player: " + ChatColor.WHITE + playerName,
                ChatColor.GRAY + "Duration: " + ChatColor.WHITE + currentDuration + " months",
                ChatColor.GRAY + "Refund: " + ChatColor.GREEN + refundAmount + " coins"));
        info.setItemMeta(infoMeta);
        inv.setItem(4, info);

        ItemStack confirm = new ItemStack(Material.EMERALD);
        ItemMeta confirmMeta = confirm.getItemMeta();
        confirmMeta.setDisplayName(ChatColor.GREEN + "Confirm Deletion");
        confirmMeta.setLore(java.util.Arrays.asList(
                ChatColor.GRAY + "Click to delete this home",
                ChatColor.GRAY + "Player will be refunded " + refundAmount + " coins"));
        confirm.setItemMeta(confirmMeta);
        inv.setItem(11, confirm);

        ItemStack cancel = new ItemStack(Material.BARRIER);
        ItemMeta cancelMeta = cancel.getItemMeta();
        cancelMeta.setDisplayName(ChatColor.YELLOW + "Cancel");
        cancelMeta.setLore(java.util.Collections.singletonList(ChatColor.GRAY + "Click to go back"));
        cancel.setItemMeta(cancelMeta);
        inv.setItem(15, cancel);

        admin.openInventory(inv);
    }

    public static boolean handleAdminHomeDeleteConfirmMenuClick(InventoryClickEvent event) {
        String title = net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.legacySection()
                .serialize(event.getView().title());
        if (!title.startsWith(ChatColor.RED + "Confirm Deletion for "))
            return false;
        event.setCancelled(true);
        if (event.getClickedInventory() == null
                || !event.getClickedInventory().equals(event.getView().getTopInventory()))
            return true;
        Player admin = (Player) event.getWhoClicked();
        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || !clicked.hasItemMeta())
            return true;
        if (clicked.getType() == Material.EMERALD) {
            String titleNoColors = ChatColor.stripColor(title);
            String rest = titleNoColors.replaceFirst("Confirm Deletion for ", "");
            int idx = rest.lastIndexOf(" of ");
            String homeName = rest.substring(0, idx);
            String playerName = rest.substring(idx + 4);
            org.bukkit.OfflinePlayer target = Bukkit.getOfflinePlayer(playerName);
            if (target == null || target.getUniqueId() == null)
                return true;
            HomeManager.Home home = HomeManager.getHomes(target.getUniqueId()).get(homeName);
            if (home == null) {
                org.homes.homes.utils.MessageUtils.sendError(admin, "This home does not exist!");
                return true;
            }
            int pricePerMonth = configManager.getHomePrice();
            int remainingMonths = home.getDurationMonths();
            int refundAmount = remainingMonths * pricePerMonth;
            boolean ok = HomeManager.removeHome(target.getUniqueId(), homeName);
            if (ok) {
                org.bukkit.entity.Player targetPlayer = Bukkit.getPlayer(target.getUniqueId());
                if (targetPlayer != null && targetPlayer.isOnline()) {
                    org.homes.homes.utils.EconomyUtils.addMoney(targetPlayer, refundAmount);
                }
                org.homes.homes.utils.MessageUtils.sendSuccess(admin, "Home has been deleted and " + refundAmount
                        + " coins refunded for " + remainingMonths + " remaining months!");
                openAdminHomesMenu(admin, target.getUniqueId(), 1);
            } else {
                org.homes.homes.utils.MessageUtils.sendError(admin, "This home does not exist!");
            }
            return true;
        } else if (clicked.getType() == Material.BARRIER) {
            String displayName = clicked.getItemMeta().getDisplayName();
            if (displayName.equals(ChatColor.YELLOW + "Cancel")) {
                String titleNoColors = ChatColor.stripColor(title);
                String rest = titleNoColors.replaceFirst("Confirm Deletion for ", "");
                int idx = rest.lastIndexOf(" of ");
                String homeName = rest.substring(0, idx);
                String playerName = rest.substring(idx + 4);
                org.bukkit.OfflinePlayer target = Bukkit.getOfflinePlayer(playerName);
                if (target != null && target.getUniqueId() != null) {
                    openAdminHomeEditMenu(admin, target.getUniqueId(), homeName);
                }
                return true;
            }
        }
        return true;
    }
}