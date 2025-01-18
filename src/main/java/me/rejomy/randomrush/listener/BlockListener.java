package me.rejomy.randomrush.listener;

import me.rejomy.randomrush.RandomRushAPI;
import me.rejomy.randomrush.util.Utils;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();

        if (RandomRushAPI.INSTANCE.getMatchManager().isInMatch(player)) {
            // Set event cancelled state dependency on current config allowed block break value.
            event.setCancelled(!RandomRushAPI.INSTANCE.getConfigManager().getConfig().isGameAllowedDestroyBlocks());
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlockPlaced();

        if (RandomRushAPI.INSTANCE.getMatchManager().isInMatch(player)) {
            if (block.getY() > RandomRushAPI.INSTANCE.getConfigManager().getConfig().getMaxBuildY()) {
                Utils.sendMessage(player, RandomRushAPI.INSTANCE.getConfigManager().getLang().getMatchMaxBuildY());

                event.setCancelled(true);
            }
        }
    }
}
