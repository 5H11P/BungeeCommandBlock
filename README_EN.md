# BungeeCommandBlock

A BungeeCord plugin for filtering player commands before they reach backend Spigot/Paper servers.

Based on `config.yml`, the plugin decides whether a command should be:

- forwarded to the backend server
- blocked on BungeeCord with a message

## Features

- Whitelist forwarding (`whitelist-commands`)
- Blacklist blocking (`blacklist-commands`)
- Extra staff whitelist (`staff-whitelist-commands`)
- Hot reload command from console (`/bcb reload`)
- Plugin commands are hidden from players (players receive `undefined-command-message`)
- `%command%` placeholder support (player input without leading `/`)
- Bypass permission nodes for admins

## Requirements

- Java 8+
- BungeeCord (compatible with `bungeecord-api 1.20-R0.2`)

## Installation

1. Build the plugin

```bash
mvn -DskipTests package
```

2. Put `target/BungeeCommandBlock-1.0.0.jar` into your BungeeCord `plugins` folder
3. Start or restart BungeeCord
4. Edit the generated config at `plugins/BungeeCommandBlock/config.yml`

## Configuration Overview

Main config keys:

- `whitelist-commands`: commands allowed for normal players (forwarded to backend)
- `blacklist-commands`: explicitly blocked commands (higher priority than whitelist)
- `staff-whitelist-commands`: extra commands allowed only for `bungeecommandblock.staff`
- `undefined-command-message`: message for non-configured commands
- `blacklist-command-message`: message for blacklisted commands

## Message Placeholder

- `%command%`: the exact command the player typed, including arguments, without the leading `/`

Example:

- Player enters `/plugins`
- `%command%` becomes `plugins`

## Permissions

- `bungeecommandblock.staff`: allows commands in `staff-whitelist-commands`
- `bungeecommandblock.bypass`: bypass all checks (allow all commands)
- `bungeecommandblock.*`: bypass all checks (allow all commands)

## Plugin Commands (Console Only)

- `bcb reload`: hot-reload `config.yml`
- `bcbreload reload`: alias form of `bcb`

Notes:

- Players cannot use these plugin commands
- Players will receive `undefined-command-message` instead of a plugin-specific response

## Processing Priority (Player Commands)

1. Bungee proxy commands: skipped (not intercepted)
2. Bypass permission present: allow
3. Blacklist match: block and send `blacklist-command-message`
4. Normal whitelist match: allow
5. Staff whitelist match + `bungeecommandblock.staff`: allow
6. Otherwise: block and send `undefined-command-message`

## Example Config

```yaml
whitelist-commands:
  - "/party"
  - "/friends"

blacklist-commands:
  - "/plugins"
  - "/op"

staff-whitelist-commands:
  - "/staffchat"
  - "/mod"

undefined-command-message: "&cUnknown command: %command%"
blacklist-command-message: "&cYou do not have permission to use this command: %command%"
```

## Notes

- Matching is based on the root command and is case-insensitive
- `/ban Steve` is matched as `/ban`
- Commands in config may be written as `/cmd` or `cmd` (they are normalized automatically)

