package com.kterising;

import com.kterising.Command.Command;
import com.kterising.Command.TabCompleters;
import com.kterising.Functions.*;
import com.kterising.Listeners.*;
import org.bukkit.*;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;
import java.util.Random;

public final class KteRising extends JavaPlugin {

    @Override
    public void onEnable() {

        new UpdateChecker(this, 112155).getVersion(version -> {
            if (this.getDescription().getVersion().equals(version)) {
                getLogger().info("There is not a new update available.");
            } else {
                getLogger().info("There is a new update available.");
            }
        });
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new Placeholder().register();
        } else {
            getLogger().warning(ChatColor.RED + "PlaceholderAPI not found. Some features do not work!");
        }

        Objects.requireNonNull(getCommand("kterising")).setExecutor(new Command(this));
        Objects.requireNonNull(this.getCommand("kterising")).setTabCompleter(new TabCompleters());
        Bukkit.getPluginManager().registerEvents(new AutoStart(this), this);
        Bukkit.getPluginManager().registerEvents(new AutoMelt(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerDamage(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerDeath(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerJoin(this), this);
        Bukkit.getPluginManager().registerEvents(new NightVision(), this);
        Bukkit.getPluginManager().registerEvents(new AutoPickUp(this), this);
        getServer().getPluginManager().registerEvents(new CustomDeathMessage(this), this);
        Bukkit.getPluginManager().registerEvents(new CustomDeathMessage(this), this);
        ModVoteGUI modVoteGUI = new ModVoteGUI();
        getServer().getPluginManager().registerEvents(modVoteGUI, this);

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

        World world = Bukkit.getWorld("world");
        assert world != null;
        WorldBorder worldBorder = world.getWorldBorder();
        worldBorder.setSize(getConfig().getInt("world-size"));
        worldBorder.setCenter(0, 0);
        worldBorder.setDamageAmount(5);
        worldBorder.setDamageBuffer(2);

        if(getConfig().getBoolean("spectators-generate-chunks")) {
            Objects.requireNonNull(getServer().getWorld("world")).setGameRule(GameRule.SPECTATORS_GENERATE_CHUNKS, false);
        }

        int minX = (int) (worldBorder.getCenter().getX() - worldBorder.getSize() / 2);
        int minZ = (int) (worldBorder.getCenter().getZ() - worldBorder.getSize() / 2);
        int maxX = minX + (int) worldBorder.getSize();
        int maxZ = minZ + (int) worldBorder.getSize();

        if(getConfig().getBoolean("water-to-ice.enabled")) {
            for (int x = minX; x < maxX; x++) {
                for (int y = getConfig().getInt("water-to-ice.low-y"); y < getConfig().getInt("water-to-ice.high-y"); y++) {
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
        MessagesConfig.setup();

        MessagesConfig.get().addDefault("prefix","&6[KteRising] &r");
        MessagesConfig.get().addDefault("title.start-game.title","&6GAME STARTED");
        MessagesConfig.get().addDefault("title.start-game.sub","&aGood Luck");
        MessagesConfig.get().addDefault("title.lava-starting.title","&6Lava Rising");
        MessagesConfig.get().addDefault("title.lava-starting.sub","&cBeware of Lava");
        MessagesConfig.get().addDefault("title.finish-game.title","&6GAME OVER");
        MessagesConfig.get().addDefault("title.finish-game.sub","&aWinner: &f%winner%");
        MessagesConfig.get().addDefault("title.pvp-allow.title","&6PVP Allow!");
        MessagesConfig.get().addDefault("title.pvp-allow.sub","&cbe careful!");
        MessagesConfig.get().addDefault("title.player-died.title","&cYou Died");
        MessagesConfig.get().addDefault("title.player-died.sub","&aNext time!");
        MessagesConfig.get().addDefault("title.mode-refresh.title","&6Mode Changed!");
        MessagesConfig.get().addDefault("title.mode-refresh.sub","&aMode: &f%mode%");
        MessagesConfig.get().addDefault("title.skip.title","&6Time skipped");
        MessagesConfig.get().addDefault("title.skip.sub","&aWatch Out");
        MessagesConfig.get().addDefault("title.freeze.title","&6Lava Rising");
        MessagesConfig.get().addDefault("title.freeze.sub","&aFreezed");
        MessagesConfig.get().addDefault("title.freeze-started.sub","&aUnFreezed");
        MessagesConfig.get().addDefault("title.start-time", "&aStarting in &f%time%");
        MessagesConfig.get().addDefault("title.mod-winner.title", "&6Winner Mode is");
        MessagesConfig.get().addDefault("title.mod-winner.sub", "&a%mod%");
        MessagesConfig.get().addDefault("pvp-enabled","&aEnabled");
        MessagesConfig.get().addDefault("pvp-disabled","&cDisabled");
        MessagesConfig.get().addDefault("start-time","&aStarting in: &f");
        MessagesConfig.get().addDefault("start-second"," &aseconds!");
        MessagesConfig.get().addDefault("title.worldborder-shrink.title","&cAttention!");
        MessagesConfig.get().addDefault("title.worldborder-shrink.sub","&6Space is shrinking!");
        MessagesConfig.get().addDefault("title.eliminated.title","&cYou Are eliminated");
        MessagesConfig.get().addDefault("title.eliminated.sub","&aNext time!");
        MessagesConfig.get().addDefault("mod.voted", "&aYou have voted for the %mod% mod!");
        MessagesConfig.get().addDefault("mod.gui-title", "&aVote for a mod!");
        MessagesConfig.get().addDefault("mod.no_winner", "&cNo winning mod found in the vote.");
        MessagesConfig.get().addDefault("mod.votes", "&7Votes: &a%votes%");
        MessagesConfig.get().addDefault("mod.already-voted", "&aYou have already voted for this mod!");
        MessagesConfig.get().addDefault("message.command-title","&8&m-----------&6KteRising-----------");
        MessagesConfig.get().addDefault("message.command-mod","                 &6Mods");
        MessagesConfig.get().addDefault("message.command-classic","&6  Classic   &7- &fPlay the game with mining.");
        MessagesConfig.get().addDefault("message.command-op","&6  Op        &7- &fPlay the game without mining.");
        MessagesConfig.get().addDefault("message.command-ultraop","&6  UltraOP        &7- &fPlay with bow in Op mode.");
        MessagesConfig.get().addDefault("message.command-elytra","&6  Elytra    &7- &fPlay with Elytra in Classic mode.");
        MessagesConfig.get().addDefault("message.command-elytraop","&6  ElytraOP    &7- &fPlay with Elytra in Op mode.");
        MessagesConfig.get().addDefault("message.command-trident","&6  Trident &7- &fPlay with Trident in Classic mode.");
        MessagesConfig.get().addDefault("message.command-tridentop","&6  TridentOP &7- &fPlay with Trident in Op mode.");
        MessagesConfig.get().addDefault("message.command-start","&6  /kterising start &7- &fStart the Game");
        MessagesConfig.get().addDefault("message.command-mode","&6  /kterising mode &7- &fChange the mode.");
        MessagesConfig.get().addDefault("message.command-reload","&6  /kterising reload &7- &fReload the Plugin.");
        MessagesConfig.get().addDefault("message.command-skip","&6  /kterising skip &7- &fSkips the mining part of classic modes");
        MessagesConfig.get().addDefault("message.command-freeze","&6  /kterising freeze &7- &fFreezes the rise of lava");
        MessagesConfig.get().addDefault("message.command-vote", "&6  /kterising vote &7- &fOpens the Vote menu.");
        MessagesConfig.get().addDefault("message.dont-permission","&4You don't have permission.");
        MessagesConfig.get().addDefault("message.already-started","&4Game already started.");
        MessagesConfig.get().addDefault("message.not-started","&4The game hasn't started");
        MessagesConfig.get().addDefault("message.plugin-reload","&aPlugin Reloaded");
        MessagesConfig.get().addDefault("message.command-game-started","&aGame started.");

        MessagesConfig.get().options().copyDefaults(true);
        MessagesConfig.save();

    }

    @Override
    public void onDisable() {
        getLogger().info(ChatColor.GOLD + "[KteRising]" + ChatColor.RED + "Plugin Disabled!");
    }
}
