package org.homes.homes.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.homes.homes.homes.HomeMenuManager;

public class GUIListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (HomeMenuManager.handleHomeMenuClick(event)) {
            return;
        }
        if (HomeMenuManager.handleDeleteHomeMenuClick(event)) {
            return;
        }
        if (org.homes.homes.homes.AdminHomeMenuManager.handleAdminHomesMenuClick(event)) {
            return;
        }
        if (org.homes.homes.homes.AdminHomeMenuManager.handleAdminPlayersMenuClick(event))
            return;
        if (org.homes.homes.homes.AdminHomeMenuManager.handleMaxHomesEditMenuClick(event))
            return;
        if (org.homes.homes.homes.AdminHomeMenuManager.handleAdminHomeDeleteConfirmMenuClick(event))
            return;
        if (org.homes.homes.homes.AdminHomeMenuManager.handleAdminEditHomesMenuClick(event))
            return;
        if (org.homes.homes.homes.AdminHomeMenuManager.handleSetMaxHomesMenuClick(event))
            return;
        if (org.homes.homes.homes.AdminHomeMenuManager.handleSetHomeAccessMonthsMenuClick(event))
            return;
    }
}