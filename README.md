# 🏠 Home System Plugin - Complete Documentation

## 📋 Table of Contents
1. [Overview](#overview)
2. [Features](#features)
3. [Installation](#installation)
4. [Configuration](#configuration)
5. [Commands](#commands)
6. [Permissions](#permissions)
7. [Economy System](#economy-system)
8. [GUI Menus](#gui-menus)
9. [Data Storage](#data-storage)
10. [Technical Architecture](#technical-architecture)
11. [API Reference](#api-reference)
12. [Troubleshooting](#troubleshooting)

### Key Highlights:
- **Multi-home system** with configurable limits
- **Economy integration** with configurable costs
- **Advanced GUI menus** with pagination
- **Admin management tools** for server operators
- **Automatic data persistence** in YAML format

## ✨ Features

### 🏠 Player Features
- **Create homes** at current location with custom names
- **Teleport to homes** via GUI menu or commands
- **Delete homes** with automatic refunds
- **Extend home duration** to prevent expiration
- **View home information** including remaining duration
- **Pagination support** for multiple homes

### 👑 Admin Features
- **View all players' homes** with search functionality
- **Edit home durations** for any player
- **Delete homes** with confirmation dialogs
- **Set maximum home limits** per player
- **Bulk management** tools
- **Refund system** for deleted homes

### 💰 Economy Integration
- **Configurable home creation costs**
- **Automatic refunds** when deleting homes
- **Duration-based pricing** for extensions
- **Scoreboard-based economy** system

## 🚀 Installation

### Installation Steps
1. **Download** the plugin JAR file
2. **Place** the JAR in your server's `plugins/` folder
3. **Restart** your server
4. **Configure** the plugin via `config.yml`
5. **Reload** or restart to apply changes

### File Structure
```
plugins/
└── homes/
    ├── homes.jar
    ├── config.yml          # Main configuration
    ├── homes.yml           # Player home data
    └── plugin.yml          # Plugin metadata
```

## ⚙️ Configuration

### Main Configuration (`config.yml`)

```yaml
# Charless Plugin Configuration

# Message Configuration
messages:
  prefix: "&8[&bCharlesonia&8] &r"
  player_only: "&cOnly players can use this command."
  operator_only: "&cOnly operators can use this command."
  invalid_item: "&cInvalid item: %item%"
  invalid_name: "&cInvalid waypoint name! Use only letters, numbers, and underscores (max 32 characters)." 

# Home System Configuration
home:
  price: 100                    # Cost to create a home (in coins)
```

### Configuration Options

| Option | Type | Default | Description |
|--------|------|---------|-------------|
| `messages.prefix` | String | `"&8[&bCharlesonia&8] &r"` | Chat prefix for all plugin messages |
| `messages.player_only` | String | `"&cOnly players can use this command."` | Message shown when non-players use player-only commands |
| `messages.operator_only` | String | `"&cOnly operators can use this command."` | Message shown when non-ops use admin commands |
| `messages.invalid_item` | String | `"&cInvalid item: %item%"` | Message for invalid item selections |
| `messages.invalid_name` | String | `"&cInvalid waypoint name!..."` | Message for invalid home names |
| `home.price` | Integer | `100` | Cost in coins to create a new home |

### Color Codes
The plugin supports Minecraft color codes:
- `&a` - Green (success)
- `&c` - Red (error)
- `&e` - Yellow (warning/info)
- `&b` - Aqua (highlight)
- `&8` - Dark Gray (prefix)
- `&7` - Gray (secondary text)

## 📜 Commands

### Player Commands

| Command | Usage | Description | Permission |
|---------|-------|-------------|------------|
| `/addhome` | `/addhome <name>` | Create a home at current location | `homes.addhome` |
| `/delhome` | `/delhome [name]` | Delete a home or open delete menu | `homes.delhome` |
| `/homes` | `/homes` | Open homes management menu | `homes.homes` |

### Admin Commands

| Command | Usage | Description | Permission |
|---------|-------|-------------|------------|
| `/adminhome` | `/adminhome` | Open admin homes management | `homes.admin` or OP |

### Command Details

#### `/addhome <name>`
- **Purpose**: Create a new home at current location
- **Cost**: Configurable amount (see `home.price`)
- **Requirements**: 
  - Player must have sufficient coins
  - Home name must be valid (letters, numbers, underscores)
  - Player must be under home limit
- **Success**: Home created, coins deducted
- **Failure**: Insufficient funds, invalid name, or limit reached

#### `/delhome [name]`
- **Purpose**: Delete a home and receive refund
- **Refund**: Full cost refund for remaining duration
- **Usage**: 
  - With name: Delete specific home
  - Without name: Open delete menu
- **Requirements**: Home must exist and belong to player

#### `/homes`
- **Purpose**: Open homes management GUI
- **Features**: 
  - List all player homes
  - Teleport to homes
  - View home information
  - Pagination support

#### `/adminhome`
- **Purpose**: Open admin management GUI
- **Features**:
  - View all players' homes
  - Edit home durations
  - Delete homes with confirmation
  - Set player home limits
- **Requirements**: OP permission or `homes.admin`

## 🔐 Permissions

### Permission Nodes

| Permission | Description | Default |
|------------|-------------|---------|
| `homes.addhome` | Allow creating homes | `true` (everyone) |
| `homes.delhome` | Allow deleting homes | `true` (everyone) |
| `homes.homes` | Allow using homes menu | `true` (everyone) |
| `homes.admin` | Access admin features | `false` (OP only) |
| `homes.reload` | Reload plugin configuration | `false` (OP only) |

### Permission Groups
- **Players**: Basic home management (`homes.addhome`, `homes.delhome`, `homes.homes`)
- **Operators**: Full access including admin features
- **Server Admins**: All permissions including reload

## 💰 Economy System

### Overview
The plugin uses a **scoreboard-based economy system** with the objective name `"money"`.

### Economic Operations

#### Home Creation
- **Cost**: `home.price` coins
- **Deduction**: Automatic when creating home
- **Validation**: Player must have sufficient funds

#### Home Deletion
- **Refund**: `home.price × remaining_months`
- **Calculation**: Based on remaining duration
- **Distribution**: Automatic refund to player

#### Duration Extension
- **Cost**: `home.price × extension_months`
- **Deduction**: Automatic when extending
- **Validation**: Player must have sufficient funds

### Economy Integration
```java
// Example: Check if player has enough money
if (!EconomyUtils.hasMoney(player, homeCost)) {
    return false;
}

// Example: Remove money for home creation
EconomyUtils.removeMoney(player, homeCost);

// Example: Add refund money
EconomyUtils.addMoney(player, refundAmount);
```

## 🖥️ GUI Menus

### Player Menus

#### Homes Menu
- **Size**: 27 slots (3 rows)
- **Title**: `"Homes (page/total)"`
- **Items**: 
  - Oak Door: Home teleport
  - Arrow: Navigation
- **Features**: Pagination, teleportation

#### Delete Home Menu
- **Size**: 27 slots (3 rows)
- **Title**: `"Delete Home (page/total)"`
- **Items**:
  - Barrier: Home deletion
  - Arrow: Navigation
- **Features**: Confirmation, refund display

### Admin Menus

#### Admin Players Menu
- **Size**: 54 slots (6 rows)
- **Title**: `"Admin Players (page/total)"`
- **Items**:
  - Player Head: Player selection
  - Book: Edit max homes
  - Arrow: Navigation
- **Features**: Player search, bulk management

#### Admin Homes Menu
- **Size**: 54 slots (6 rows)
- **Title**: `"Admin Homes: PlayerName (page/total)"`
- **Items**:
  - Oak Door: Home selection
  - Book: Edit max homes
  - Arrow: Navigation
- **Features**: Home management, duration editing

#### Home Edit Menu
- **Size**: 27 slots (3 rows)
- **Title**: `"Edit Home: HomeName of PlayerName"`
- **Items**:
  - Clock: Edit duration
  - Barrier: Delete home
  - Arrow: Back
- **Features**: Duration editing, deletion

#### Duration Edit Menu
- **Size**: 27 slots (3 rows)
- **Title**: `"Set Duration for HomeName"`
- **Items**:
  - Paper/Emerald: Duration options (1-10 months)
  - Arrow: Back
- **Features**: Duration selection, current highlight

#### Delete Confirmation Menu
- **Size**: 27 slots (3 rows)
- **Title**: `"Confirm Deletion for HomeName of PlayerName"`
- **Items**:
  - Paper: Home information
  - Emerald: Confirm deletion
  - Barrier: Cancel
- **Features**: Confirmation dialog, refund information

## 💾 Data Storage

### Storage Format
All data is stored in YAML format in `homes.yml`:

```yaml
# Player UUID
player-uuid:
  maxHomes: 10  # Maximum homes allowed
  
  # Individual home data
  home-name:
    world: "world"
    x: 100.5
    y: 64.0
    z: 200.3
    yaw: 90.0
    pitch: 0.0
    durationMonths: 3
```

### Data Structure

#### Player Data
- **UUID**: Player's unique identifier
- **maxHomes**: Maximum homes allowed (configurable per player)
- **homes**: Map of home names to home data

#### Home Data
- **world**: World name
- **x, y, z**: Coordinates
- **yaw, pitch**: Orientation
- **durationMonths**: Remaining duration in months

### Data Persistence
- **Automatic saving** on home creation/deletion
- **World load events** trigger data loading
- **Server shutdown** triggers final save
- **Backup recommended** for production servers

## 🏗️ Technical Architecture

### Package Structure
```
org.homes.homes/
├── Main.java                    # Plugin main class
├── commands/                    # Command implementations
│   ├── CommandManager.java      # Command routing
│   ├── AddHomeCommand.java      # Home creation
│   ├── DeleteHomeCommand.java   # Home deletion
│   ├── HomesCommand.java        # Homes menu
│   ├── AdminHomeCommand.java    # Admin menu
│   └── ExtendHomeTimeCommand.java # Duration extension
├── homes/                       # Core home management
│   ├── HomeManager.java         # Home data management
│   ├── HomeMenuManager.java     # Player GUI management
│   └── AdminHomeMenuManager.java # Admin GUI management
├── utils/                       # Utility classes
│   ├── MessageUtils.java        # Message formatting
│   ├── EconomyUtils.java        # Economy operations
│   └── ValidationUtils.java     # Input validation
├── events/                      # Event handling
│   ├── EventManager.java        # Event registration
│   ├── GUIListener.java         # GUI event handling
│   └── WorldLoadListener.java   # World load events
└── config/                      # Configuration
    └── ConfigManager.java       # Configuration management
```

### Core Classes

#### HomeManager
- **Purpose**: Central home data management
- **Responsibilities**:
  - Home CRUD operations
  - Data persistence
  - Player limits management
  - Duration tracking

#### HomeMenuManager
- **Purpose**: Player GUI management
- **Features**:
  - Homes listing
  - Teleportation
  - Delete confirmation
  - Pagination

#### AdminHomeMenuManager
- **Purpose**: Admin GUI management
- **Features**:
  - Player management
  - Home editing
  - Bulk operations
  - Confirmation dialogs

#### ConfigManager
- **Purpose**: Configuration management
- **Features**:
  - YAML configuration loading
  - Default value management
  - Message formatting
  - Dynamic reloading

### Event System
- **GUIListener**: Handles all inventory click events
- **WorldLoadListener**: Loads home data on world load
- **EventManager**: Centralized event registration

## 🔧 API Reference

### Public Methods

#### HomeManager
```java
// Home management
public static boolean addHome(Player player, String name)
public static boolean removeHome(UUID uuid, String homeName)
public static Home getHome(Player player, String name)
public static List<Home> getHomes(Player player)
public static Map<String, Home> getHomes(UUID uuid)

// Player limits
public static int getMaxHomes(UUID uuid)
public static void setMaxHomes(UUID uuid, int max)

// Duration management
public static int getHomeDuration(UUID uuid, String homeName)
public static void setHomeDuration(UUID uuid, String homeName, int months)

// Data persistence
public static void saveHomes()
public static void loadHomes()
```

#### EconomyUtils
```java
public static int getMoney(Player player)
public static boolean hasMoney(Player player, int amount)
public static void addMoney(Player player, int amount)
public static boolean removeMoney(Player player, int amount)
```

#### MessageUtils
```java
public static void sendMessage(CommandSender sender, String message)
public static void sendSuccess(CommandSender sender, String message)
public static void sendError(CommandSender sender, String message)
public static void sendInfo(CommandSender sender, String message)
```

### Home Class
```java
public class Home {
    public String getName()
    public Location getLocation()
    public int getDurationMonths()
    public void setDurationMonths(int durationMonths)
}
```

## 🛠️ Troubleshooting

### Common Issues

#### Plugin Won't Load
- **Check**: Server version compatibility (1.21+)
- **Check**: Java version (8+)
- **Check**: Plugin JAR integrity
- **Solution**: Verify server logs for specific errors

#### Homes Not Saving
- **Check**: File permissions in plugins folder
- **Check**: Available disk space
- **Check**: Server shutdown process
- **Solution**: Ensure proper server shutdown

#### Economy Issues
- **Check**: Scoreboard objective "money" exists
- **Check**: Player has sufficient funds
- **Check**: Economy plugin compatibility
- **Solution**: Verify scoreboard setup

#### GUI Not Working
- **Check**: Player permissions
- **Check**: Inventory space
- **Check**: Other plugins interfering
- **Solution**: Test with clean server

### Debug Information
- **Logs**: Check server console for error messages
- **Permissions**: Use `/lp user <player> permission info homes.*`
- **Data**: Inspect `homes.yml` for data integrity
- **Config**: Verify `config.yml` syntax

### Performance Optimization
- **Large servers**: Consider database storage
- **Many homes**: Implement home limits
- **Frequent access**: Cache frequently accessed data
- **Memory usage**: Monitor plugin memory consumption

## 🤝 Support

For support, bug reports, or feature requests:
- **GitHub Issues**: Create an issue on the project repository
- **Documentation**: Refer to this README
- **Configuration**: Check `config.yml` examples