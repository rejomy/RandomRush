package me.rejomy.randomrush.arena;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import me.rejomy.randomrush.RandomRushAPI;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Getter
@Setter
public class SetupArena extends Arena {
    @Setter(AccessLevel.NONE)
    private boolean isOverrideArena;

    public SetupArena() {}

    public SetupArena(Arena arena) {
        copy(arena);

        isOverrideArena = true;
    }

    public void saveToFile() throws IOException {
        File arenas = RandomRushAPI.INSTANCE.getConfigManager().getArenasDirectory();

        File file = new File(arenas, name + ".yml");

        if (!file.exists()) {
            file.createNewFile();
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

        config.set("max", maxPlayers);
        config.set("min", minPlayers);

        List<String> positions = getSpawnPositions().stream()
                .map(position -> new StringBuilder()
                    .append(position.getX()).append(" ")
                    .append(position.getY()).append(" ")
                    .append(position.getZ()).append(" ")
                    .append(position.getYaw()).append(" ")
                    .append(position.getPitch()).toString())
                .toList();

        config.set("positions", positions);

        config.set("center", new StringBuilder()
                .append(centerPosition.getX()).append(" ")
                .append(centerPosition.getY()).append(" ")
                .append(centerPosition.getZ()).append(" ")
                .append(centerPosition.getYaw()).append(" ")
                .append(centerPosition.getPitch()).toString());

        config.set("players-per-team", playerPerTeam);

        config.set("copy-from", worldName);

        config.save(file);
    }
}
