package me.rejomy.randomrush.manager;

import lombok.Getter;
import me.rejomy.randomrush.RandomRushAPI;
import me.rejomy.randomrush.config.Config;
import me.rejomy.randomrush.config.Lang;
import me.rejomy.randomrush.interfaces.Loadable;
import me.rejomy.randomrush.util.io.FileUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

@Getter
public class ConfigManager implements Loadable {
    final File directory = RandomRushAPI.INSTANCE.getPlugin().getDataFolder();
    final File arenasDirectory = new File(directory, "arenas");

    Config config;
    Lang lang;

    YamlConfiguration setupInventoryConfig;
    YamlConfiguration setupPlayerPerTeamInventoryConfig;
    YamlConfiguration setupPlayerPerTeamAddInventoryConfig;
    YamlConfiguration selectArenaInventoryConfig;

    @Override
    public void load() {
        if (!directory.exists()) {
            directory.mkdirs();

            // Load a small information about this plugin.
            RandomRushAPI.INSTANCE.getPlugin().saveResource("how_it_work.txt", false);
        }

        File file = new File(directory, "spawn.yml");

        if (file.exists()) {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
            RandomRushAPI.INSTANCE.getMatchManager().setSpawnLocation(
                    new Location(Bukkit.getWorld(config.getString("world")),
                            config.getDouble("x"),
                            config.getDouble("y"),
                            config.getDouble("z"),
                            (float) config.getDouble("yaw"),
                            (float) config.getDouble("pitch")));
        }

        if (!arenasDirectory.exists()) {
            arenasDirectory.mkdirs();
        }

        loadConfig();
        loadLang();
        loadInventories();
    }

    void loadConfig() {
        if (config == null) {
            // We load the config from jar if this not loaded before.
            RandomRushAPI.INSTANCE.getPlugin().saveDefaultConfig();

            config = new Config(RandomRushAPI.INSTANCE.getPlugin().getConfig());
        }

        config.load();
    }

    void loadLang() {
        if (lang == null) {
            lang = new Lang(YamlConfiguration.loadConfiguration(
                    FileUtil.getOrLoadFromJar(directory, "lang.yml")));
        }

        lang.load();
    }

    void loadInventories() {
        setupInventoryConfig = YamlConfiguration.loadConfiguration(
                FileUtil.getOrLoadFromJar(directory, "inventories/setup.yml"));
        setupPlayerPerTeamInventoryConfig = YamlConfiguration.loadConfiguration(
                FileUtil.getOrLoadFromJar(directory, "inventories/setup-player-per-team.yml"));
        setupPlayerPerTeamAddInventoryConfig = YamlConfiguration.loadConfiguration(
                FileUtil.getOrLoadFromJar(directory, "inventories/setup-player-per-team-add.yml"));
        selectArenaInventoryConfig = YamlConfiguration.loadConfiguration(
                FileUtil.getOrLoadFromJar(directory, "inventories/select-arena.yml"));
    }
}
