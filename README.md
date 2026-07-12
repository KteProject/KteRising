[![GitHub release](https://img.shields.io/github/v/release/KteProject/KteRising?style=for-the-badge)](https://github.com/KteProject/KteRising/releases)
[![Minecraft Version](https://img.shields.io/badge/Minecraft-1.20.4+-blue?style=for-the-badge)](https://papermc.io/)
[![License](https://img.shields.io/badge/License-MIT-green?style=for-the-badge)](LICENSE)
[![Discord](https://img.shields.io/discord/1133365192756039782?style=for-the-badge)]([https://discord.gg/XXXXXX](https://discord.gg/saScDMCzts))

![About KteRising](https://cdn.modrinth.com/data/cached_images/93c6ee3bdf4bcff2fb08ee675d03cbaae897de10_0.webp)
**KteRising** is a professional Minecraft plugin that redefines the classic Lava Rising concept with a modern, performance-focused and highly customizable system. It transforms simple rising lava into a fast-paced, strategic and competitive gameplay experience.

Thanks to its optimized architecture, KteRising delivers smooth performance even on high-population servers. With advanced game modes, voting systems, stat tracking and deep configuration support, it adapts perfectly to any server style.

![## Features](https://cdn.modrinth.com/data/cached_images/668cadd3c6f16fbdfd59f5cd6eee30534898c26b.png)
### 🌋 **Lava Rising System**

Lava starts rising after a defined countdown and increases by one layer with each rise. The rising speed is fully configurable via the config. The lava level automatically determines when PvP is enabled and when the world border begins to shrink. Thanks to the chunk cache + pipeline method, the system runs smoothly and stably.

### 🎮 **Mod (GameModes) System**

Each game mode has its own item sets, enchantments, countdown duration, GUI slot, and a unique placeholder tag. Users can create unlimited modes via the config. The selected mode defines the game’s pace, equipment, and overall match duration.

### 🧭 **Voting Menu**

Players automatically receive a compass upon joining and can vote for a game mode through a modern MiniMessage-based GUI interface. Each mode displays its description and live vote count. If the votes are tied, the system automatically makes a random selection.

### 🗺️ **Safe Biome Detection**

The system never starts the game in unbalanced areas such as oceans, rivers, or frozen biomes. Using a spiral scanning algorithm, the most suitable biome center is found and the world border is set accordingly. No unnecessary chunk loading is performed.

### ⚔️ **PvP & World Border Management**

PvP is disabled at the start of the game and is automatically enabled once the lava reaches a defined height. The world border shrinks at critical moments during the match. If spectator players move outside the border, they are teleported back to a safe location.

### 📊 **SQL-Based Statistics System**

Works with SQLite or MySQL. Each player's kills, deaths, wins, and matches played are saved asynchronously. Thanks to its cache-based structure, it causes no lag and automatically synchronizes every 2 minutes.

### ⚡ **AutoStart System**

When the required player count is reached, an automatic countdown begins and all players are notified via the Action Bar. If the player count drops, the countdown is instantly cancelled.

### 🔨 **AutoPickup & AutoSmelt**

This config-controlled fast-loot system allows ores to be automatically smelted or collected in their raw form. Players can continue playing without slowing down.

### 🎨 **MiniMessage-Based Modern Notifications**

All titles, action bars, and GUI contents operate using the MiniMessage format. Templates are fully customizable, and every notification is specially designed for high visibility.

### 💰 **Reward System (Kill / Win / Death)**

KteRising includes a fully configurable reward system that allows server owners to execute custom commands based on player actions.

Rewards can be assigned for:
- ✅ Killing another player
- 🏆 Winning the match
- ☠️ Player death

### **👥 Team System (Coming Soon)**

A full team system is currently under development. Players will be able to create or join teams, cooperate strategically, and compete in organized group-based gameplay modes. This feature will integrate seamlessly with the existing game flow.

![## PlaceholderAPI](https://cdn.modrinth.com/data/cached_images/8e5ffd0e5e22ce9c997cc55753cbac038339f830.png)

KteRising provides a rich set of dynamic placeholders for use with PlaceholderAPI, scoreboards, tab lists, holograms, and custom messages.

| Placeholder              | Description             |
| ------------------------ | ----------------------- |
| `%kterising_kill%`       | Player kill count       |
| `%kterising_death%`      | Player death count      |
| `%kterising_win%`        | Total wins              |
| `%kterising_game%`       | Total games played      |
| `%kterising_time%`       | Current match duration  |
| `%kterising_lava%`       | Current lava height     |
| `%kterising_live%`       | Remaining alive players |
| `%kterising_lavarising%` | Lava rising status      |
| `%kterising_pvp%`        | PvP enabled status      |
| `%kterising_mode%`       | Active game mode        |
| `%kterising_match%`      | Current match state     |

![Commands and Permission](https://cdn.modrinth.com/data/cached_images/c144c49b4b8ca9dfbb81004cdbff8914c1f8a6a3.png)

Below is the full list of KteRising commands along with their required permissions.

| Command                     | Description                                  | Permission                 |
| --------------------------- | -------------------------------------------- | -------------------------- |
| `/kterising start`          | Instantly starts the game                    | `kterising.command.start`  |
| `/kterising skip`           | Skips the countdown and sets it to 3 seconds | `kterising.command.skip`   |
| `/kterising freeze`         | Pauses or resumes lava rising                | `kterising.command.freeze` |
| `/kterising mode <mode>`    | Manually sets the active game mode           | `kterising.command.mode`   |
| `/kterising vote`           | Opens the voting menu                        | `kterising.command.vote`   |
| `/kterising stats <player>` | Displays player statistics                   | `kterising.command.stats`  |
| `/kterising reload`         | Reloads configuration and message files      | `kterising.command.reload` |

![Config](https://cdn.modrinth.com/data/cached_images/3a8880c266a3f94c469540126726946eafadff85.png)

This section contains all configuration files used to fully customize KteRising’s gameplay, systems, and visual behavior.

## Config.yml
<details>
<summary>Spoiler: Config.yml</summary>

```
############################################################
#                                                          #
#                   KTE RISING CONFIGURATION               #
#      This file controls every system of the plugin.      #
#   Read the descriptions carefully before changing them.  #
#                                                          #
############################################################

plugin-configurations:
  # Checks for new plugin updates on startup.
  # Recommended: Keep enabled, lets you know when a new version is available.
  update-check: true

  # Enables anonymous performance analytics through bStats.
  # Does NOT collect personal information — safe to keep enabled.
  bstats-metrics: true


# ─────────────────────────────────────────────────────────────
#  📌 IMPORTANT — SQLITE USERS READ THIS
#
#  SQLite creates 3 files automatically:
#     • stats.db        → Main database
#     • stats.db-wal    → Write-Ahead-Log (performance booster)
#     • stats.db-shm    → Shared memory file
#
#  ⚠ DO NOT DELETE THESE FILES manually.
#  They are REQUIRED for the database to work safely and fast.
#  Deleting them may corrupt player statistics permanently.
# ─────────────────────────────────────────────────────────────
database:
  # Choose the storage engine:
  # SQLITE → Lightweight, recommended for small/medium servers.
  # MYSQL  → External database, recommended for large networks.
  type: SQLITE

  sqlite:
    # File name for local SQLite database.
    file: "stats.db"

  mysql:
    # MySQL connection settings (only used if type = MYSQL)
    host: "localhost"
    port: 3306
    database: "kterising"
    user: "root"
    password: "password"
    # Enables Secure Socket Layer encryption.
    ssl: false
    # Maximum size of the database connection pool.
    pool-size: 10


game-configurations:
  # Time (in seconds) before lava begins rising in CLASSIC mode.
  # Recommended: 600 (10 minutes) for balanced gameplay.
  classic-start-duration: 600

  # Time (in seconds) before lava begins rising in OP modes.
  # OP modes are faster-paced, so this value is lower.
  op-start-duration: 30

  # The Y-level where lava starts rising from.
  # Lower = longer game, Higher = shorter game.
  lava-start-height: 0

  # Maximum height lava can reach.
  # ⚠ This value MUST NOT exceed world-configurations.world-height.
  lava-finish-height: 180

  # Delay between each lava layer increase.
  # Example: 2 = Lava rises 1 block every 2 seconds.
  lava-delay: 2

  # The exact Y-level at which PvP becomes enabled.
  # Set below lava-finish-height or PvP may never enable.
  pvp-allow-height: 70

  # If enabled → All drops go directly into player inventory.
  # If disabled → Blocks drop items on the ground normally.
  auto-pickup: true

  # If enabled → Ores automatically smelt when mined.
  # Example: Iron Ore → Iron Ingot, Gold Ore → Gold Ingot.
  auto-smelt: true


world-configurations:
  # Name of the world the game will run in.
  # If renamed, update accordingly.
  world-name: "world"

  # WorldBorder radius used for the gameplay area.
  # Larger border = longer game duration.
  world-border: 100

  # Maximum build height allowed for players during the match.
  # Anything above this Y-level cannot be placed.
  world-height: 180


autostart-configuration:
  # If true → Game automatically starts when enough players join.
  enabled: true

  # Number of players required for autostart to activate.
  need-player-count: 5

  # Countdown (in seconds) after required players are online.
  autostart-countdown: 30


rewards-configuration:
  # Enables the reward system (commands executed on kill / win / death)
  enabled: true

  # Commands executed when a player kills another player.
  kill-player:
    - "eco give %player% 5"

  # Commands executed when a player wins the game.
  win-player:
    - "eco give %player% 3"

  # Commands executed when a player dies.
  death-player:
    - "eco give %player% -3"


voting-menu-configuration:
  # Enables /vote system
  enabled: true

  # Size of vote menu (rows)
  gui-row: 3

  # Title shown at the top of the GUI
  gui-title: "<green>Vote Menu"

############################################################
#                     MODE CONFIGURATION                   #
#                                                          #
# This section controls ALL available game modes.          #
# Each mode defines:                                       #
#   • How it appears in the voting menu                    #
#   • Which items players receive in that mode             #
#   • Countdown duration before lava starts                #
#   • Whether the mode is enabled or disabled              #
#                                                          #
# You can safely:                                          #
#   ✔ Add new modes                                        #
#   ✔ Disable existing ones                                #
#   ✔ Change GUI items & slots                             #
#   ✔ Modify items, enchantments, and labels               #
#                                                          #
# DO NOT rename the mode keys (classic, elytra, etc.)      #
# unless you fully understand how the plugin loads modes.  #
#                                                          #
# The structure of each mode:                              #
#   enabled: true/false         → Toggles the mode         #
#   label: "<color>Name"        → Text displayed in GUI    #
#   description:                → Lore shown under the item#
#   gui-slot: number            → Position inside menu     #
#   gui-item: MATERIAL          → Icon displayed in GUI    #
#   countdown: seconds          → Lava start timer         #
#   placeholder-label: string   → Used in PlaceholderAPI   #
#   items:                      → Items given to players   #
#                                                          #
# You are free to customize EVERYTHING inside "items".     #
# The plugin automatically builds mode items from this     #
# configuration. No Java edits needed.                     #
############################################################
modes-configuration:

  classic:
    enabled: true
    label: "<green>Classic"
    description:
      - "<green>Vote: <vote>"
    gui-slot: 10
    gui-item: NETHERITE_PICKAXE
    countdown: 600
    placeholder-label: "Classic"
    items:
      - material: NETHERITE_PICKAXE
        amount: 1
        name: "<gold><bold>LavaRising Pickaxe"
        enchantments:
          - "efficiency:5"
      - material: COOKED_BEEF
        amount: 16
      - material: OAK_LOG
        amount: 16

  op:
    enabled: true
    label: "<aqua>OP"
    description:
      - "<green>Vote: <vote>"
    gui-slot: 11
    gui-item: DIAMOND
    countdown: 30
    placeholder-label: "<aqua>OP"
    items:
      - material: NETHERITE_PICKAXE
        amount: 1
        name: "<gold><bold>LavaRising Pickaxe"
        enchantments:
          - "efficiency:5"
      - material: COOKED_BEEF
        amount: 64
      - material: DIAMOND
        amount: 64
      - material: IRON_INGOT
        amount: 64
      - material: COBBLESTONE
        amount: 576
      - material: OAK_LOG
        amount: 64

  elytra:
    enabled: true
    label: "<yellow>Elytra"
    description:
      - "<green>Vote: <vote>"
    gui-slot: 12
    gui-item: ELYTRA
    countdown: 600
    placeholder-label: "<yellow>Elytra"
    items:
      - material: NETHERITE_PICKAXE
        amount: 1
        name: "<gold><bold>LavaRising Pickaxe"
        enchantments:
          - "efficiency:5"
      - material: COOKED_BEEF
        amount: 16
      - material: OAK_LOG
        amount: 16
      - material: ELYTRA
        amount: 1
        name: "<gold><bold>LavaRising Elytra"
      - material: FIREWORK_ROCKET
        amount: 6

  elytraop:
    enabled: true
    label: "<red>ElytraOP"
    description:
      - "<green>Vote: <vote>"
    gui-slot: 13
    gui-item: ELYTRA
    countdown: 30
    placeholder-label: "<red>ElytraOP"
    items:
      - material: NETHERITE_PICKAXE
        amount: 1
        name: "<gold><bold>LavaRising Pickaxe"
        enchantments:
          - "efficiency:5"
      - material: COOKED_BEEF
        amount: 64
      - material: DIAMOND
        amount: 64
      - material: IRON_INGOT
        amount: 64
      - material: OAK_LOG
        amount: 64
      - material: FIREWORK_ROCKET
        amount: 16
      - material: ELYTRA
        amount: 1
        name: "<gold><bold>LavaRising Elytra"
      - material: COBBLESTONE
        amount: 576

  trident:
    enabled: true
    label: "<blue>Trident"
    description:
      - "<green>Vote: <vote>"
    gui-slot: 14
    gui-item: TRIDENT
    countdown: 600
    placeholder-label: "<blue>Trident"
    items:
      - material: NETHERITE_PICKAXE
        amount: 1
        name: "<gold><bold>LavaRising Pickaxe"
        enchantments:
          - "efficiency:5"
      - material: COOKED_BEEF
        amount: 16
      - material: OAK_LOG
        amount: 16
      - material: TRIDENT
        amount: 1
        name: "<aqua>Riptide Trident"
        enchantments:
          - "riptide:3"
      - material: TRIDENT
        amount: 1
        name: "<aqua>Loyalty Trident"
        enchantments:
          - "loyalty:3"

  tridentop:
    enabled: true
    label: "<dark_aqua>TridentOP"
    description:
      - "<green>Vote: <vote>"
    gui-slot: 15
    gui-item: TRIDENT
    countdown: 30
    placeholder-label: "<dark_aqua>TridentOP"
    items:
      - material: NETHERITE_PICKAXE
        amount: 1
        name: "<gold><bold>LavaRising Pickaxe"
        enchantments:
          - "efficiency:5"
      - material: COOKED_BEEF
        amount: 64
      - material: DIAMOND
        amount: 64
      - material: IRON_INGOT
        amount: 64
      - material: COBBLESTONE
        amount: 576
      - material: OAK_LOG
        amount: 64
      - material: TRIDENT
        amount: 1
        name: "<aqua>Riptide Trident"
        enchantments:
          - "riptide:3"
      - material: TRIDENT
        amount: 1
        name: "<aqua>Loyalty Trident"
        enchantments:
          - "loyalty:3"

  ultraop:
    enabled: true
    label: "<gold>UltraOP"
    description:
      - "<green>Vote: <vote>"
    gui-slot: 16
    gui-item: GOLDEN_APPLE
    countdown: 30
    placeholder-label: "<gold>UltraOP"
    items:
      - material: NETHERITE_PICKAXE
        amount: 1
        name: "<gold><bold>Lava Rising Pickaxe"
        enchantments:
          - "efficiency:10"
      - material: COOKED_BEEF
        amount: 64
      - material: DIAMOND
        amount: 64
      - material: IRON_INGOT
        amount: 64
      - material: COBBLESTONE
        amount: 576
      - material: OAK_LOG
        amount: 64
      - material: BOW
        amount: 1
        name: "<gold><bold>Lava Rising Bow"
        enchantments:
          - "power:5"
      - material: ARROW
        amount: 64
      - material: GOLDEN_APPLE
        amount: 6
      - material: GOLDEN_CARROT
        amount: 24
```


</details>


## Messages.yml

<details>
<summary>Spoiler: Message.yml</summary>

```
other:
  no-permission: "<red>⛔ You don't have permission to use this command."
  player-only: "<red>⛔ This command can only be used by players."

placeholderapi:
  pvp-enabled: "<green>Enabled"
  pvp-disabled: "<red>Disabled"
  mode-not-selected: "<gray>Not Selected"

command:
  help-command:
    - "<gold><bold>────────── KteRising Help ──────────"
    - "<gray>Available commands:"
    - "<gold>/kterising start <gray>- Start the game"
    - "<gold>/kterising mode <gray>- Change the game mode"
    - "<gold>/kterising skip <gray>- Skip time forward"
    - "<gold>/kterising stats <gray>- Shows the statistics of a player."
    - "<gold>/kterising reload <gray>- Reload configuration files"
    - "<gold>/kterising freeze <gray>- Freeze/unfreeze lava rising"
    - "<gold><bold>──────────────────────────────────────"
  start-command: "<green>✔ The game has started!"
  mode-command: "<green>✔ Game mode changed to <gold><mode><green>."
  mode-command-error: "<red>✖ This mode does not exist: <gold><mode>"
  reload-command: "<green>✔ Configuration files reloaded. <gray>(Some settings may require a restart.)"
  command-error: "<red>✖ This command can only be used during an active game."

titles:
  finish-game:
    title: "<gold><bold>GAME OVER!"
    subtitle: "<gray>Winner: <white><winner>"
  pvp-allow:
    title: "<red><bold>PVP ENABLED!"
    subtitle: "<gray>Be careful out there."
  worldborder-shrink:
    title: "<red><bold>WORLD BORDER!"
    subtitle: "<gold>The safe zone is shrinking!"
  start-game:
    title: "<green><bold>GAME STARTED!"
    subtitle: "<gray>Good luck and have fun!"
  lavarising:
    title: "<gold><bold>LAVA IS RISING!"
    subtitle: "<gray>Stay above the danger..."
  skip:
    title: "<yellow><bold>⏭ TIME SKIPPED!"
    subtitle: "<gray>Lava will rise in <white>3 seconds<gray>."
  freeze-on:
    title: "<red><bold>LAVA FROZEN!"
    subtitle: "<gray>The rising has been paused."
  freeze-off:
    title: "<green><bold>LAVA RESUMED!"
    subtitle: "<gray>The lava continues to rise."

action-bar:
  autostart-countdown: "<yellow>Starting in <white><seconds> <gray>seconds..."
  max-height: "<red>You cannot build at this height."

stats:
  header: "<gold><bold><player>'s Statistics"
  kill: "<gray>• <white>Kills: <green><kill>"
  death: "<gray>• <white>Deaths: <red><death>"
  win: "<gray>• <white>Wins: <blue><win>"
  not-found: "<red>This player has no recorded stats."

vote:
  vote-item: "<green>Vote"
  vote: "<green>You voted for the <gold><mode></gold> mode!"
  already-voted: "<red>You have already voted for this mode!"
```
</details>

## Start.bat


<details>
<summary>Spoiler</summary>


```
@echo off
title KteRising - KteProject
set fileName="server.jar"
set /A memory=2048
cls
:start

del /s /f /q world

java -Xms%memory%M -Xmx%memory%M -XX:+UseG1GC -XX:+ParallelRefProcEnabled -XX:MaxGCPauseMillis=200 -XX:+UnlockExperimentalVMOptions -XX:+DisableExplicitGC -XX:+AlwaysPreTouch -XX:G1HeapWastePercent=5 -XX:G1MixedGCCountTarget=4 -XX:InitiatingHeapOccupancyPercent=15 -XX:G1MixedGCLiveThresholdPercent=90 -XX:G1RSetUpdatingPauseTimePercent=5 -XX:SurvivorRatio=32 -XX:+PerfDisableSharedMem -XX:MaxTenuringThreshold=1 -Dusing.aikars.flags=https://mcflags.emc.gs -Daikars.new.flags=true -XX:G1NewSizePercent=30 -XX:G1MaxNewSizePercent=40 -XX:G1HeapRegionSize=8M -XX:G1ReservePercent=20 -jar %fileName% --nogui

echo Restarting in 5 seconds...
echo Press CTRL + C to cancel.
timeout 5
goto :start
```


</details>



![Discord](https://cdn.modrinth.com/data/cached_images/43b5b60ca950f66b92af57839b2ede848fdfe508.png)

For any plugin-related bugs, errors, or suggestions, please contact us via [Discord](https://discord.gg/M6V7wSaWz3).
