package org.homes.homes.events;

import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

public class EventManager {
    private final List<Listener> listeners = new ArrayList<>();
    private final Plugin plugin;

    public EventManager(Plugin plugin) {
        this.plugin = plugin;
        registerListeners();
    }

    private void registerListeners() {
        listeners.add(new GUIListener());
        listeners.add(new WorldLoadListener());
    }

    public void registerAllListeners() {
        for (Listener listener : listeners) {
            plugin.getServer().getPluginManager().registerEvents(listener, plugin);
        }
    }

    public void addListener(Listener listener) {
        listeners.add(listener);
        plugin.getServer().getPluginManager().registerEvents(listener, plugin);
    }
} 