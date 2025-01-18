package me.rejomy.randomrush.listener;

import me.rejomy.randomrush.RandomRushAPI;
import me.rejomy.randomrush.match.Match;
import me.rejomy.randomrush.match.MatchPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ConnectionListener implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        RandomRushAPI.INSTANCE.getDataManager().add(player);
    }

    @EventHandler(ignoreCancelled = true)
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        MatchPlayer matchPlayer = RandomRushAPI.INSTANCE.getMatchManager().getMatchPlayer(player);

        if (matchPlayer != null) {
            Match match = matchPlayer.getMatch();

            match.removePlayer(matchPlayer);
        }

        RandomRushAPI.INSTANCE.getDataManager().remove(player);
    }
}
