package me.rejomy.randomrush.listener;

import me.rejomy.randomrush.RandomRushAPI;
import me.rejomy.randomrush.match.Match;
import me.rejomy.randomrush.match.MatchPlayer;
import me.rejomy.randomrush.util.Utils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class DeathListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        MatchPlayer matchPlayer = RandomRushAPI.INSTANCE.getMatchManager().getMatchPlayer(player);

        // Return because this player is not in match
        if (matchPlayer == null) return;

        Match match = matchPlayer.getMatch();

        Utils.sendMessage(player, RandomRushAPI.INSTANCE.getConfigManager().getLang().getMatchDie());
        match.removePlayer(matchPlayer);
    }

}
