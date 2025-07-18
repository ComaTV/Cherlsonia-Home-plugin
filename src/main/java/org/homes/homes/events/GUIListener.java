package org.homes.homes.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.homes.homes.homes.HomeMenuManager;

public class GUIListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (HomeMenuManager.handleAdminHomesMenuClick(event))
            return;
        if (HomeMenuManager.handleHomeMenuClick(event))
            return;
        if (HomeMenuManager.handleDeleteHomeMenuClick(event))
            return;
        if (HomeMenuManager.handleAdminPlayersMenuClick(event))
            return;
        HomeMenuManager.handleAdminHomesMenuClick(event);
        org.homes.homes.homes.AdminHomeMenuManager.handleAdminEditHomesMenuClick(event);
        org.homes.homes.homes.AdminHomeMenuManager.handleEditHomeMenuClick(event);
        org.homes.homes.homes.AdminHomeMenuManager.handleSetMaxHomesMenuClick(event);
    }
}