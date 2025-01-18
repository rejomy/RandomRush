package me.rejomy.randomrush.task.impl;

import com.cryptomorin.xseries.XMaterial;
import me.rejomy.randomrush.RandomRushAPI;
import me.rejomy.randomrush.match.Match;
import me.rejomy.randomrush.match.MatchPlayer;
import me.rejomy.randomrush.task.Task;
import org.bukkit.World;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class GameTask extends Task {

    private final static int MATCH_DELAY = RandomRushAPI.INSTANCE.getConfigManager().getConfig().getMaxRoundTime();
    private final static int MAX_BORDER_SIZE = RandomRushAPI.INSTANCE.getConfigManager().getConfig().getMaxBorderSize();

    int delay = MATCH_DELAY;
    World world = match.getWorld();

    public GameTask(Match match) {
        super(match);
        setBukkitRunnable(bukkitRunnable);
    }

    Runnable bukkitRunnable = () -> {
        delay--;

        if (delay % RandomRushAPI.INSTANCE.getConfigManager().getConfig().getItemDelay() == 0) {
            List<XMaterial> items;

            if (RandomRushAPI.INSTANCE.getConfigManager().getConfig().isWhiteListMode()) {
                items = RandomRushAPI.INSTANCE.getConfigManager().getConfig().getListItems();
            }
            else {
                items = Arrays.stream(XMaterial.values()).collect(Collectors.toList());
                items.removeAll(RandomRushAPI.INSTANCE.getConfigManager().getConfig().getListItems());
            }

            for (MatchPlayer player : match.getPlayers()) {
                player.getPlayer().getInventory().addItem(items.get(new Random().nextInt(items.size())).parseItem());
            }
        }

        if (match.getPlayers().size() <= 1) {
            match.end();
            RandomRushAPI.INSTANCE.getMatchManager().create(match.getArena(), match.getPlayersPerTeam());
            return;
        }

        match.updateTime(delay);

        world.getWorldBorder().setSize((double) (MAX_BORDER_SIZE * delay) / MATCH_DELAY);
    };
}
