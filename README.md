
## Features
- Players can create up to 10 homes, each with a custom name.
- Creating a home costs 1000 money (scoreboard objective: `money`).
- Deleting a home refunds 1000 money.
- GUI menu for listing, teleporting to, and deleting homes.
- Admin GUI for OPs to view and delete homes of any player (with pagination).
- All messages, commands, and menus are in English.

## Commands
| Command         | Description                                      | Permission   |
|----------------|--------------------------------------------------|--------------|
| `/addhome <name>`   | Add a home at your current location (costs 1000 money) | everyone     |
| `/delhome <name>`   | Delete a home by name and get a refund (or open delete menu) | everyone     |
| `/homes`            | Open the menu with your homes                 | everyone     |
| `/adminhome`        | Open the admin menu to manage all players' homes | OP only      |

## Permissions
- Only OPs can use `/adminhome` and access the admin GUI.
- All other commands are available to all players.

## Setup
2. **Scoreboard Setup:**
   - The plugin uses a scoreboard objective named `money` for the economy.
   - To create it, run in-game as OP:
     ```
     /scoreboard objectives add money dummy Money
     ```
   - Give players money as needed:
     ```
     /scoreboard players add <player> money <amount>
     ```
3. **Installation:**
   - Place the plugin JAR in your server's `plugins` folder.
   - Restart or reload the server.

## Usage
- **Add a home:** `/addhome myhouse`
- **Teleport to a home:** Open `/homes` menu and click a home, or use the GUI.
- **Delete a home:** `/delhome myhouse` or use the delete menu.
- **Admin:** `/adminhome` to open the admin GUI, browse players, and delete any home.

## Configuration
- All messages and settings can be customized in `config.yml`.
- Home data is stored in `homes.yml` in the plugin data folder.

## Notes
- Players must have at least 1000 money to create a home.
- Deleting a home always refunds 1000 money.
- The plugin is fully in English.

## License
MIT