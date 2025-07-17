# Charless Plugin

A modular and extensible Minecraft plugin for Paper/Spigot servers.

## 🏗️ Project Structure

```
src/main/java/org/charless/charless/
├── Main.java                          # Main plugin class
├── commands/                          # Modular command system
│   ├── CommandManager.java           # Manager for all commands
│   ├── AddWaypointCommand.java       # /addwaypoint command
│   ├── DeleteWaypointCommand.java    # /delwaypoint command
│   └── WaypointsCommand.java         # /waypoints command
├── config/                           # Configuration system
│   └── ConfigManager.java            # Configuration manager
├── events/                           # Event system
│   ├── EventManager.java             # Event manager
│   └── GUIListener.java              # GUI interface listener
├── utils/                            # Common utilities
│   ├── MessageUtils.java             # Message utilities
│   └── ValidationUtils.java          # Validation utilities
└── waypoints/                        # Waypoint system
    ├── WaypointManager.java          # Waypoint manager
    └── WaypointMenuManager.java      # Menu manager
```

## 🚀 Features

### Waypoint System
- **Command `/addwaypoint <name> [item]`** - Create a waypoint with custom item
- **Command `/delwaypoint <name>`** - Delete a waypoint
- **Command `/waypoints`** - Open the waypoints menu
- **Tab Completion** - Intelligent suggestions for items
- **GUI Menus** - Visual interfaces for navigation

### Characteristics
- ✅ **Modular** - Easy to extend with new systems
- ✅ **Configurable** - Customizable messages and settings
- ✅ **Validation** - Security checks and validation
- ✅ **Messages** - Centralized message system
- ✅ **Utilities** - Reusable common functions

## 📋 Commands

| Command | Description | Permissions |
|---------|-------------|-------------|
| `/addwaypoint <name> [item]` | Create a waypoint with optional item | OP |
| `/delwaypoint <name>` | Delete a waypoint | OP |
| `/waypoints` | Open waypoints menu | All |

## ⚙️ Configuration

The `config.yml` file contains all plugin settings:

```yaml
# Waypoint System Configuration
waypoints:
  enabled: true
  max_per_player: 10
  default_item: ENDER_PEARL

# Message Configuration
messages:
  prefix: "&8[&bCharless&8] &r"
  waypoint_added: "&aWaypoint '%name%' added with item %item%!"
  waypoint_deleted: "&cWaypoint deleted!"
  # ... more messages
```

## 🔧 Extending the Plugin

### Adding a New Command

1. Create a new class in the `commands` package:

```java
package org.charless.charless.commands;

import org.bukkit.command.CommandSender;
import org.charless.charless.utils.MessageUtils;
import org.charless.charless.utils.ValidationUtils;

public class MyNewCommand implements CommandManager.CommandExecutor {
    
    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!ValidationUtils.isPlayer(sender)) {
            MessageUtils.sendError(sender, "Only players can use this command.");
            return true;
        }
        
        MessageUtils.sendSuccess(sender, "Command executed successfully!");
        return true;
    }
}
```

2. Register the command in `CommandManager.java`:

```java
private void registerCommands() {
    commands.put("addwaypoint", new AddWaypointCommand());
    commands.put("delwaypoint", new DeleteWaypointCommand());
    commands.put("waypoints", new WaypointsCommand());
    commands.put("mynewcommand", new MyNewCommand()); // Add here
}
```

3. Add the command to `plugin.yml`:

```yaml
commands:
  mynewcommand:
    description: My new command description
    usage: /mynewcommand
```

### Adding a New Listener

1. Create a new class in the `events` package:

```java
package org.charless.charless.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class MyNewListener implements Listener {
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // Event logic here
    }
}
```

2. Register the listener in `EventManager.java`:

```java
private void registerListeners() {
    listeners.add(new GUIListener());
    listeners.add(new MyNewListener()); // Add here
}
```

### Adding a New System

1. Create a new package for your system:

```
src/main/java/org/charless/charless/mysystem/
├── MySystemManager.java
└── MySystemCommand.java
```

2. Integrate the system in `Main.java`:

```java
public class Main extends JavaPlugin {
    private MySystemManager mySystemManager;
    
    @Override
    public void onEnable() {
        // ... existing code ...
        
        // Initialize new system
        mySystemManager = new MySystemManager(this);
        
        // ... existing code ...
    }
}
```

## 🎨 Customizing Messages

All messages can be customized in `config.yml`:

```yaml
messages:
  prefix: "&8[&bCharless&8] &r"
  waypoint_added: "&aWaypoint '%name%' added with item %item%!"
  waypoint_deleted: "&cWaypoint deleted!"
```

Use `%variable%` to replace dynamic values in messages.

## 🔒 Permissions

The plugin uses operator permissions (`isOp()`) for administrative commands. For a more advanced permission system, you can integrate Vault or LuckPerms.

## 📦 Building

```bash
mvn clean package
```

The compiled plugin will be in `target/charless-1.0-SNAPSHOT.jar`.