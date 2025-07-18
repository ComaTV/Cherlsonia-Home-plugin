package org.homes.homes.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.homes.homes.config.ConfigManager;

public class CommandManager implements TabCompleter {
    private final Map<String, CommandExecutor> commands = new HashMap<>();
    private final ConfigManager configManager;

    public CommandManager(ConfigManager configManager) {
        this.configManager = configManager;
        registerCommands();
    }

    private void registerCommands() {
        commands.put("addhome", new AddHomeCommand());
        commands.put("delhome", new DeleteHomeCommand());
        commands.put("homes", new HomesCommand(configManager));
        commands.put("adminhome", new AdminHomeCommand());
        commands.put("extendhometime", new ExtendHomeTimeCommand());
    }

    public boolean executeCommand(CommandSender sender, Command command, String label, String[] args) {
        String commandName = command.getName().toLowerCase();
        CommandExecutor executor = commands.get(commandName);

        if (executor != null) {
            return executor.execute(sender, args);
        }

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        String commandName = command.getName().toLowerCase();
        CommandExecutor executor = commands.get(commandName);

        if (executor instanceof TabCompletable) {
            return ((TabCompletable) executor).onTabComplete(sender, args);
        }

        return new ArrayList<>();
    }

    public interface CommandExecutor {
        boolean execute(CommandSender sender, String[] args);
    }

    public interface TabCompletable {
        List<String> onTabComplete(CommandSender sender, String[] args);
    }
}