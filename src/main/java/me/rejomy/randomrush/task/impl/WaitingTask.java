package me.rejomy.randomrush.task.impl;

import me.rejomy.randomrush.RandomRushAPI;
import me.rejomy.randomrush.match.Match;
import me.rejomy.randomrush.match.MatchPlayer;
import me.rejomy.randomrush.task.Task;
import me.rejomy.randomrush.util.PlayerUtil;
import me.rejomy.randomrush.util.Utils;
import me.rejomy.randomrush.util.world.Position;

import java.util.Map;

public class WaitingTask extends Task {

    private final int WAITING_DELAY = RandomRushAPI.INSTANCE.getConfigManager().getConfig().getWaitingDelay();

    private int startingDelay = WAITING_DELAY;

    public WaitingTask(Match match) {
        super(match);

        setBukkitRunnable(bukkitRunnable);
    }

    Runnable bukkitRunnable = () -> {
        // Cancel this task, because players smaller than min players for start.
        if (match.getPlayers().size() < match.getArena().minPlayers) {
            cancelTask();
            return;
        }

        int lastDelay = startingDelay;

        int delay = (match.getPlayers().size() * WAITING_DELAY) / match.getArena().maxPlayers;

        if (delay < startingDelay) {
            startingDelay = delay;
        }

        startingDelay--;

        match.updateTime(startingDelay);

        // Start the game if starting delay equals zero.
        if (startingDelay == 0) {
            match.setStatus(Match.Status.PLAYING);
            GameTask gameTask = new GameTask(match);
            gameTask.run();

            int i = 0;

            for (MatchPlayer matchPlayer : match.getPlayers()) {
                int spawnPositionsSize = match.getArena().getSpawnPositions().size();

                matchPlayer.getPlayer().teleport(
                        Position.toLocation(match.getWorld(), match.getArena().getSpawnPositions()
                                .get(i % spawnPositionsSize)));

                PlayerUtil.clearPotionEffects(matchPlayer.getPlayer());
                PlayerUtil.clearItems(matchPlayer.getPlayer());
                matchPlayer.getPlayer().setGameMode(RandomRushAPI.INSTANCE.getConfigManager().getConfig().getGameMode());

                i++;
            }

            match.setTaskID(gameTask.getTaskId());

            cancelTask();
        } else {
            int lastPercentage = (lastDelay * 100) / WAITING_DELAY;
            int percentage = (delay * 100) / WAITING_DELAY;

            for (Map.Entry<Byte, String> entry : RandomRushAPI.INSTANCE.getConfigManager().getLang().getStartMessages().entrySet()) {
                if (entry.getKey() > lastPercentage && entry.getKey() < percentage) {
                    for (MatchPlayer player : match.getPlayers()) {
                        Utils.sendMessage(player.getPlayer(), entry.getValue(), "time", startingDelay);
                    }

                    break;
                }
            }
        }
    };
}
