package me.rejomy.randomrush.config;

import com.cryptomorin.xseries.XMaterial;
import lombok.Getter;
import me.rejomy.randomrush.RandomRushAPI;
import me.rejomy.randomrush.util.TimeUtil;
import org.bukkit.GameMode;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Getter
public class Config extends YamlConfig {

    int waitingDelay;
    int maxRoundTime;
    int preloadWorldsPerArenaAmount;

    int itemDelay;
    List<XMaterial> listItems;

    boolean teleportToSpawn;
    boolean teleportToMap;
    boolean removeIfWorldChange;
    boolean storage;
    boolean whiteListMode;

    int maxBuildY;
    int maxBorderSize;
    boolean gameAllowedDestroyBlocks;
    GameMode gameMode;

    long storageRemoveDataAfter;

    public Config(FileConfiguration config) {
        super(config);
    }

    @Override
    public void load() {
        waitingDelay = getIntElse("delay.waiting-delay", 30);
        maxRoundTime = getIntElse("delay.max-round-time", 600);

        storage = getBooleanElse("storage.enable", true);
        storageRemoveDataAfter = TimeUtil.getTimeInMillis(getStringElse("storage.remove-data-after", "14d"));

        preloadWorldsPerArenaAmount = getIntElse("arena.preload-worlds-amount", 2);

        teleportToMap = getBooleanElse("match.waiting.teleport-to-map", false);
        teleportToSpawn = getBooleanElse("match.teleport-to-spawn", false);
        removeIfWorldChange = getBooleanElse("match.waiting.remove-if-change-world", true);

        maxBuildY = getIntElse("match.game.max-build-y", 255);
        maxBorderSize = getIntElse("match.game.max-border-size", 300);
        gameMode = GameMode.valueOf(getStringElse("match.game.gamemode", "SURVIVAL"));

        whiteListMode = getStringElse("match.items.mode", "").equalsIgnoreCase("whitelist");
        itemDelay = getIntElse("match.items.delay", 3);
        listItems = getStringListElse("match.items.items", new ArrayList<>())
                .stream()
                .map(item -> {
                    XMaterial xMaterial = XMaterial.matchXMaterial(item).orElse(null);

                    if (xMaterial == null) {
                        RandomRushAPI.INSTANCE.getPlugin().getLogger().warning("We cant get " + item + " material.");
                    }

                    return xMaterial;
                })
                .filter(Objects::nonNull).collect(Collectors.toList());
    }
}
