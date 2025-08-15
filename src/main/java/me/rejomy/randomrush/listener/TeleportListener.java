package me.rejomy.randomrush.listener;

import me.rejomy.randomrush.RandomRushAPI;
import me.rejomy.randomrush.match.Match;
import me.rejomy.randomrush.match.MatchPlayer;
import me.rejomy.randomrush.util.Utils;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

public class TeleportListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onTeleport(PlayerTeleportEvent event) {
        var player = event.getPlayer();
        Location to = event.getTo(),
                from = event.getFrom();
        boolean worldEquals = to.getWorld() == from.getWorld();

        if (!worldEquals && RandomRushAPI.INSTANCE.getConfigManager().getConfig().isRemoveIfWorldChange()) {
            MatchPlayer matchPlayer = RandomRushAPI.INSTANCE.getMatchManager().getMatchPlayer(player);

            if (matchPlayer != null && matchPlayer.getMatch().getStatus() == Match.Status.WAITING) {
                matchPlayer.getMatch().removePlayer(matchPlayer, false);
                Utils.sendMessage(player,
                        RandomRushAPI.INSTANCE.getConfigManager().getLang().getMatchLeave(),
                        "name", matchPlayer.getMatch().getArena().name);
            }
        }
    }
}
