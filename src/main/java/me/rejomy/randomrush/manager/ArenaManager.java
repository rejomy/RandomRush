package me.rejomy.randomrush.manager;

import lombok.Getter;
import me.rejomy.randomrush.RandomRushAPI;
import me.rejomy.randomrush.arena.Arena;
import me.rejomy.randomrush.arena.SetupArena;
import me.rejomy.randomrush.interfaces.Loadable;
import me.rejomy.randomrush.util.world.Position;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ArenaManager implements Loadable {
    @Getter
    List<Arena> arenas = new ArrayList<>();

    // TODO: Проверять все ли данные есть в файле, если нету пропускать
    @Override
    public void load() {
        File arenas = RandomRushAPI.INSTANCE.getConfigManager().getArenasDirectory();

        for (File arenaFile : arenas.listFiles()) {
            Arena arena = new Arena();
            YamlConfiguration config = YamlConfiguration.loadConfiguration(arenaFile);

            arena.name = arenaFile.getName().replace(".yml", "");
            arena.worldName = config.getString("copy-from");
            arena.minPlayers = config.getInt("min");
            arena.maxPlayers = config.getInt("max");

            for (String spawnPos : config.getStringList("positions")) {
                String[] elements = spawnPos.split(" ");

                arena.getSpawnPositions().add(new Position(
                        Double.parseDouble(elements[0]),
                        Double.parseDouble(elements[1]),
                        Double.parseDouble(elements[2]),
                        Float.parseFloat(elements[3]),
                        Float.parseFloat(elements[4])));
            }

            String[] elements = config.getString("center").split(" ");
            arena.setCenterPosition(new Position(
                    Double.parseDouble(elements[0]),
                    Double.parseDouble(elements[1]),
                    Double.parseDouble(elements[2]),
                    Float.parseFloat(elements[3]),
                    Float.parseFloat(elements[4])));

            arena.getPlayerPerTeam().addAll((List<Integer>) config.get("players-per-team"));

            this.arenas.add(arena);
        }
    }

    public void delete(SetupArena setupArena) {
        File arenas = RandomRushAPI.INSTANCE.getConfigManager().getArenasDirectory();

        for (File arenaFile : arenas.listFiles()) {
            if (arenaFile.getName().replace(".yml", "").equals(setupArena.name)) {
                arenaFile.delete();
                break;
            }
        }
    }

    public Arena getByName(String arenaName) {
        return arenas.stream().filter(arena -> arena.name.equalsIgnoreCase(arenaName)).findAny().orElse(null);
    }
}
