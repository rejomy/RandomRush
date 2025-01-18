package me.rejomy.randomrush;

import lombok.Getter;
import me.rejomy.randomrush.command.impl.RandomRushCommand;
import me.rejomy.randomrush.data.PlayerData;
import me.rejomy.randomrush.data.database.DataBase;
import me.rejomy.randomrush.data.database.SQLite;
import me.rejomy.randomrush.listener.BlockListener;
import me.rejomy.randomrush.listener.ConnectionListener;
import me.rejomy.randomrush.listener.DamageListener;
import me.rejomy.randomrush.listener.DeathListener;
import me.rejomy.randomrush.manager.*;
import me.rejomy.randomrush.util.expansion.Expansion;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;

@Getter
public enum RandomRushAPI {
    INSTANCE;

    JavaPlugin plugin;
    ConfigManager configManager;
    MatchManager matchManager;
    ArenaManager arenaManager;
    WorldManager worldManager;
    DataBase dataBase;
    DataManager dataManager = new DataManager();

    void load(JavaPlugin plugin) {
        this.plugin = plugin;

        worldManager = new WorldManager();
        arenaManager = new ArenaManager();
        matchManager = new MatchManager();
        configManager = new ConfigManager();

        configManager.load();
        arenaManager.load();

        // We clean all not deleted arenas from folders after arena and config managers initializing,
        // because we need to see arenas names.
        worldManager.cleanupOldMaps();

        if (RandomRushAPI.INSTANCE.getConfigManager().getConfig().isStorage()) {
            dataBase = new SQLite();
        }
    }

    void start(JavaPlugin plugin) {
        this.plugin = plugin;

        // Create all matches from arenas after load worlds, else we can get "world = source" is null.
        matchManager.load();

        // Register bukkit event listeners
        Bukkit.getPluginManager().registerEvents(new ConnectionListener(), getPlugin());
        Bukkit.getPluginManager().registerEvents(new BlockListener(), getPlugin());
        Bukkit.getPluginManager().registerEvents(new DeathListener(), getPlugin());
        Bukkit.getPluginManager().registerEvents(new DamageListener(), getPlugin());

        // Register commands
        getPlugin().getCommand("randomrush").setExecutor(new RandomRushCommand());

        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new Expansion().register();
        }
    }

    void stop(JavaPlugin plugin) {
        this.plugin = plugin;

        dataManager.savePlayersStatistics();

        getMatchManager().unload();
        getDataManager().unload();
    }
}
