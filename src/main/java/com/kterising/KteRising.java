package com.kterising;

import com.kterising.Command.Command;
import com.kterising.Command.TabCompleters;
import com.kterising.Functions.*;
import com.kterising.Listeners.*;
import com.kterising.Team.TeamGUI;
import com.kterising.Team.TeamManager;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;
import java.util.Random;
import java.util.logging.Logger;

public final class KteRising extends JavaPlugin {
    private static KteRising instance;


    @Override
    public void onEnable() {
        instance = this;

        Logger logger = this.getLogger();

        new UpdateChecker(this, 112155).getVersion(version -> {
            String currentVersion = this.getDescription().getVersion();

            if (currentVersion.equals(version)) {
                logger.info(ChatColor.GREEN + "✔ The plugin is up-to-date! (Version: " + currentVersion + ")");
            } else {
                String updateMessage = ChatColor.DARK_RED + "" + ChatColor.RESET +
                        "\n" + ChatColor.RED + "✨ A New Update is Available! ✨" + ChatColor.RESET +
                        "\n" + ChatColor.YELLOW + "Your version: " + ChatColor.WHITE + currentVersion +
                        "\n" + ChatColor.YELLOW + "Latest version: " + ChatColor.WHITE + version +
                        "\n" + ChatColor.AQUA + "Download the latest update at:" +
                        "\n" + ChatColor.BLUE + "https://www.spigotmc.org/resources/112155/" + ChatColor.RESET +
                        "\n" + ChatColor.DARK_RED + "===============================";

                logger.warning(updateMessage);

                Bukkit.getPluginManager().registerEvents(new Listener() {
                    @EventHandler
                    public void onPlayerJoin(PlayerJoinEvent event) {
                        Player player = event.getPlayer();
                        if (player.isOp()) {
                            player.sendMessage(ChatColor.RED + "⚠ [Plugin Update Alert] ⚠");
                            player.sendMessage(updateMessage);
                        }
                    }
                }, this);
            }
        });

        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new Placeholder().register();
        }

        Random random = new Random();
        int randomsayi = random.nextInt(6) + 1;
        if (randomsayi == 1) {
            StartGame.mode = "Classic";
        } else if (randomsayi == 2) {
            StartGame.mode = "Elytra";
        } else if (randomsayi == 3) {
            StartGame.mode = "Trident";
        } else if (randomsayi == 4) {
            StartGame.mode = "OP";
        } else if (randomsayi == 5) {
            StartGame.mode = "ElytraOP";
        } else {
            StartGame.mode = "TridentOP";
        }

        StartGame.match = false;
        StartGame.PvP = false;
        StartGame.survivor = 0;
        StartGame.winner = null;
        StartGame.win = false;
        StartGame.leavedPlayers.clear();
        Command.selectedmode = false;

        String worldName = getConfig().getString("world-name", "world");
        World world = Bukkit.getWorld(worldName);

        if (world == null) {
            getLogger().severe(ChatColor.RED + "❌ It couldn't load the world. The server is shutting down...");
            Bukkit.shutdown();
            return;
        }

        WorldBorder worldBorder = world.getWorldBorder();
        worldBorder.setSize(getConfig().getInt("world-size"));
        worldBorder.setCenter(0, 0);
        worldBorder.setDamageAmount(5);
        worldBorder.setDamageBuffer(2);

        if (getConfig().getBoolean("spectators-generate-chunks")) {
            world.setGameRule(GameRule.SPECTATORS_GENERATE_CHUNKS, false);
        }

        int minX = (int) (worldBorder.getCenter().getX() - worldBorder.getSize() / 2);
        int minZ = (int) (worldBorder.getCenter().getZ() - worldBorder.getSize() / 2);
        int maxX = minX + (int) worldBorder.getSize();
        int maxZ = minZ + (int) worldBorder.getSize();

        if(getConfig().getBoolean("water-to-ice.enabled")) {
            for (int x = minX; x < maxX; x++) {
                for (int y = getConfig().getInt("water-to-ice.low-y"); y <= getConfig().getInt("water-to-ice.high-y"); y++) {
                    for (int z = minZ; z < maxZ; z++) {
                        if (world.getBlockAt(x, y, z).getType() == Material.WATER) {
                            world.getBlockAt(x, y, z).setType(Material.ICE);
                        }
                    }
                }
            }
        }

        getConfig().options().copyDefaults();
        saveDefaultConfig();

        getLogger().info(ChatColor.GOLD + "[KteRising]" + ChatColor.GREEN + " Plugin Enabled!");
        getLogger().info("Thanks for using our plugin!!");

        //Items.yml
        com.kterising.Functions.ItemsConfig.setup(this);

        //PlayerStats.yml
        PlayerStats.setup(this);

        //Messages.yml
        MessagesConfig.setup(this);

        MessagesConfig.get().addDefault("prefix", "&6[KteRising] &r");
        MessagesConfig.get().addDefault("title.start-game.title", "&6GAME STARTED");
        MessagesConfig.get().addDefault("title.start-game.sub", "&aGood Luck");
        MessagesConfig.get().addDefault("title.lava-starting.title", "&6Lava Rising");
        MessagesConfig.get().addDefault("title.lava-starting.sub", "&cBeware of Lava");
        MessagesConfig.get().addDefault("title.finish-game.title", "&6GAME OVER");
        MessagesConfig.get().addDefault("title.finish-game.sub", "&aWinner: &f%winner%");
        MessagesConfig.get().addDefault("title.pvp-allow.title", "&6PVP Allow!");
        MessagesConfig.get().addDefault("title.pvp-allow.sub", "&cbe careful!");
        MessagesConfig.get().addDefault("title.player-died.title", "&cYou Died");
        MessagesConfig.get().addDefault("title.player-died.sub", "&aNext time!");
        MessagesConfig.get().addDefault("title.mode-refresh.title", "&6Mode Changed!");
        MessagesConfig.get().addDefault("title.mode-refresh.sub", "&aMode: &f%mode%");
        MessagesConfig.get().addDefault("title.skip.title", "&6Time skipped");
        MessagesConfig.get().addDefault("title.skip.sub", "&aWatch Out");
        MessagesConfig.get().addDefault("title.freeze.title", "&6Lava Rising");
        MessagesConfig.get().addDefault("title.freeze.sub", "&aFreezed");
        MessagesConfig.get().addDefault("title.freeze-started.sub", "&aUnFreezed");
        MessagesConfig.get().addDefault("title.start-time", "&aStarting in &f%time%");
        MessagesConfig.get().addDefault("title.mod-winner.title", "&6Winner Mode is");
        MessagesConfig.get().addDefault("title.mod-winner.sub", "&a%mod%, %team%");
        MessagesConfig.get().addDefault("placeholder.pvp-enabled", "&aEnabled");
        MessagesConfig.get().addDefault("placeholder.pvp-disabled", "&cDisabled");
        MessagesConfig.get().addDefault("placeholder.match-started", "&aStarted");
        MessagesConfig.get().addDefault("placeholder.match-not-started", "&cNot Started");
        MessagesConfig.get().addDefault("start-time", "&aStarting in: &f");
        MessagesConfig.get().addDefault("start-second", " &aseconds!");
        MessagesConfig.get().addDefault("title.worldborder-shrink.title", "&cAttention!");
        MessagesConfig.get().addDefault("title.worldborder-shrink.sub", "&6Space is shrinking!");
        MessagesConfig.get().addDefault("title.eliminated.title", "&cYou Are eliminated");
        MessagesConfig.get().addDefault("title.eliminated.sub", "&aNext time!");
        MessagesConfig.get().addDefault("title.rejoined.title", "&aRejoin");
        MessagesConfig.get().addDefault("title.rejoined.sub", "&aYou're back in the game!");
        MessagesConfig.get().addDefault("mod.vote-gui", "&aVote for a mod!");
        MessagesConfig.get().addDefault("mod.voted", "&aYou have voted for the %mod% mod!");
        MessagesConfig.get().addDefault("mod.no_winner", "&cNo winning mod found in the vote.");
        MessagesConfig.get().addDefault("mod.votes", "&7Votes: &a%votes%");
        MessagesConfig.get().addDefault("mod.already-voted", "&aYou have already voted for this mod!");
        MessagesConfig.get().addDefault("vote.team", "With Team");
        MessagesConfig.get().addDefault("vote.no-team", "No Teams");
        MessagesConfig.get().addDefault("vote.wait", "Waiting");
        MessagesConfig.get().addDefault("item.vote-name", "&eVote");
        MessagesConfig.get().addDefault("item.team-name", "&aTeam");
        MessagesConfig.get().addDefault("messages.no-team-name", "&cNo Team");
        MessagesConfig.get().addDefault("messages.no-team", "&cYou don't have a team.");
        MessagesConfig.get().addDefault("messages.no-team-or-dead", "&cYou don't have a team or your teammate is dead.");
        MessagesConfig.get().addDefault("messages.closest-teammate", "&eYour teammate %player% is %distance%m away %direction%");
        MessagesConfig.get().addDefault("messages.team-joined", "&aYou joined the team: &b%team%");
        MessagesConfig.get().addDefault("messages.team-full", "&cThis team is full.");
        MessagesConfig.get().addDefault("messages.team-load-failed", "&cFailed to load teams.");
        MessagesConfig.get().addDefault("messages.team-gui", "&cSelect team.");
        MessagesConfig.get().addDefault("messages.team", " &6Team");
        MessagesConfig.get().addDefault("message.command-title", "&8&m-----------&6KteRising-----------");
        MessagesConfig.get().addDefault("message.command-mod", "                 &6Mods");
        MessagesConfig.get().addDefault("message.command-classic", "&6  Classic   &7- &fPlay the game with mining.");
        MessagesConfig.get().addDefault("message.command-op", "&6  Op        &7- &fPlay the game without mining.");
        MessagesConfig.get().addDefault("message.command-ultraop", "&6  UltraOP        &7- &fPlay with bow in Op mode.");
        MessagesConfig.get().addDefault("message.command-elytra", "&6  Elytra    &7- &fPlay with Elytra in Classic mode.");
        MessagesConfig.get().addDefault("message.command-elytraop", "&6  ElytraOP    &7- &fPlay with Elytra in Op mode.");
        MessagesConfig.get().addDefault("message.command-trident", "&6  Trident &7- &fPlay with Trident in Classic mode.");
        MessagesConfig.get().addDefault("message.command-tridentop", "&6  TridentOP &7- &fPlay with Trident in Op mode.");
        MessagesConfig.get().addDefault("message.command-start", "&6  /kterising start &7- &fStart the Game");
        MessagesConfig.get().addDefault("message.command-mode", "&6  /kterising mode &7- &fChange the mode.");
        MessagesConfig.get().addDefault("message.command-reload", "&6  /kterising reload &7- &fReload the Plugin.");
        MessagesConfig.get().addDefault("message.command-skip", "&6  /kterising skip &7- &fSkips the mining part of classic modes");
        MessagesConfig.get().addDefault("message.command-freeze", "&6  /kterising freeze &7- &fFreezes the rise of lava");
        MessagesConfig.get().addDefault("message.command-vote", "&6  /kterising vote &7- &fOpens the Vote menu.");
        MessagesConfig.get().addDefault("message.command-team", "&6  /kterising team &7- &fOpens the Team menu.");
        MessagesConfig.get().addDefault("message.dont-permission", "&4You don't have permission.");
        MessagesConfig.get().addDefault("message.already-started", "&4Game already started.");
        MessagesConfig.get().addDefault("message.not-started", "&4The game hasn't started");
        MessagesConfig.get().addDefault("message.plugin-reload", "&aPlugin Reloaded");
        MessagesConfig.get().addDefault("message.invalid-command", "&aYou have entered an invalid command.");
        MessagesConfig.get().addDefault("message.command-game-started", "&aGame started.");

        MessagesConfig.get().options().copyDefaults(true);
        MessagesConfig.save();

        Objects.requireNonNull(getCommand("kterising")).setExecutor(new Command(this));
        Objects.requireNonNull(getCommand("kterising")).setTabCompleter(new TabCompleters());
        Bukkit.getPluginManager().registerEvents(new AutoStart(this), this);

        if(getConfig().getBoolean("automelt")) {
            Bukkit.getPluginManager().registerEvents(new AutoMelt(), this);
        }
        Bukkit.getPluginManager().registerEvents(new PlayerDamage(), this);
        if(getConfig().getBoolean("obsidian-fix")) {
            Bukkit.getPluginManager().registerEvents(new LavaWaterFix(this), this);
        }
        Bukkit.getPluginManager().registerEvents(new PlayerDeath(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerJoin(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerQuit(this), this);
        Bukkit.getPluginManager().registerEvents(new NightVision(), this);
        Bukkit.getPluginManager().registerEvents(new FoodLevelChange(), this);
        if(getConfig().getBoolean("autopickup")) {
            Bukkit.getPluginManager().registerEvents(new AutoPickUp(), this);
        }
        Bukkit.getPluginManager().registerEvents(new TeamGUI(), this);
        TeamManager.getInstance().initialize(this);
        Bukkit.getPluginManager().registerEvents(new SpecialItems(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerStats(), this);
        getServer().getPluginManager().registerEvents(new CustomDeathMessage(this), this);
        Bukkit.getPluginManager().registerEvents(new CustomDeathMessage(this), this);
        ModVoteGUI modVoteGUI = new ModVoteGUI();
        TeamGUI.startTeamInfoTask(this);
        getServer().getPluginManager().registerEvents(modVoteGUI, this);
    }

    @Override
    public void onDisable() {
        getLogger().info(ChatColor.GOLD + "[KteRising]" + ChatColor.RED + "Plugin Disabled!");
        instance = null;
    }
    public static KteRising getInstance() {
        return instance;
    }
}
