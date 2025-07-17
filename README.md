# Home System Plugin

## Features
- Players can create up to 10 homes, each with a custom name.
- Creating a home costs a configurable amount of money (see config.yml, `home.price`).
- Deleting a home refunds the same configurable amount.
- GUI menu for listing, teleporting to, and deleting homes.
- Admin GUI for OPs to view and delete homes of any player (with pagination).
- All messages, commands, and menus are in English.

## Commands
| Command         | Description                                      | Permission   |
|----------------|--------------------------------------------------|--------------|
| `/addhome <name>`   | Add a home at your current location (costs configurable money) | everyone     |
| `/delhome <name>`   | Delete a home by name and get a refund (or open delete menu) | everyone     |
| `/homes`            | Open the menu with your homes                 | everyone     |
| `/adminhome`        | Open the admin menu to manage all players' homes | OP only      |

## Permissions
- Only OPs can use `/adminhome` and access the admin GUI.
- All other commands are available to all players.

## Setup
1. **Installation:**
   - Place the plugin JAR in your server's `plugins` folder.
   - Restart or reload the server.

## Usage
- **Add a home:** `/addhome myhouse`
- **Teleport to a home:** Open `/homes` menu and click a home, or use the GUI.
- **Delete a home:** `/delhome myhouse` or use the delete menu.
- **Admin:** `/adminhome` to open the admin GUI, browse players, and delete any home.

## Configuration
- All messages and settings can be customized in `config.yml`.
- **Home price is configurable:**
  - In `config.yml`, set the price under:
    ```yaml
    home:
      price: 1000
    ```
  - You can change this value at any time. The plugin will use the new value for all future home creations and deletions (after a reload).
- Home data is stored in `homes.yml` in the plugin data folder.

## Notes
- Players must have at least the configured amount of money to create a home.
- Deleting a home always refunds the configured amount.
- The plugin is fully in English.

## License
MIT