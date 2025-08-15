package me.rejomy.randomrush.match;

import lombok.Getter;
import lombok.Setter;
import me.rejomy.randomrush.RandomRushAPI;
import me.rejomy.randomrush.arena.Arena;
import me.rejomy.randomrush.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Match {
    List<MatchPlayer> players;
    Arena arena;
    World world;

    int playersPerTeam;
    int taskID;

    // By default we set waiting status, because match is 100% not starting when we create object of this class.
    Status status = Status.WAITING;

    public MatchPlayer getPlayer(Player player) {
        return getPlayers().stream().filter(matchPlayer -> matchPlayer.getPlayer() == player).findAny().orElse(null);
    }

    public void end() {
        new ArrayList<>(getPlayers()).forEach(matchPlayer -> {
            removePlayer(matchPlayer, true);
            Utils.sendMessage(matchPlayer.getPlayer(), RandomRushAPI.INSTANCE.getConfigManager().getLang().getMatchRemove(), "name", arena.name);
        });

        Bukkit.getScheduler().cancelTask(getTaskID());

        RandomRushAPI.INSTANCE.getWorldManager().unload(this);

        RandomRushAPI.INSTANCE.getMatchManager().delete(this);
    }

    public void removePlayer(MatchPlayer matchPlayer, boolean teleport) {
        // By default we are not applying effects, inventory items and teleports
        // if teleport to map in waiting status is false.
        boolean isTeleportedToMap = !(getStatus() == Status.WAITING &&
                        !RandomRushAPI.INSTANCE.getConfigManager().getConfig().isTeleportToMap());
        Player player = matchPlayer.getPlayer();

        getPlayers().remove(matchPlayer);

        if (matchPlayer.isSpectator()) {
            for (Player target : Bukkit.getOnlinePlayers()) {
                target.showPlayer(player);
                player.showPlayer(target);
            }

            player.setFlying(false);
            player.setAllowFlight(false);
        }

        if (isTeleportedToMap) {
            player.setHealth(player.getMaxHealth());
            player.setFoodLevel(20);
            player.setLevel(0);
            player.getInventory().clear();
        }

        for (PotionEffect potionEffect : player.getActivePotionEffects()) {
            player.removePotionEffect(potionEffect.getType());
        }

        if (isTeleportedToMap && teleport)
            player.teleport(
                    RandomRushAPI.INSTANCE.getConfigManager().getConfig().isTeleportToSpawn() ?
                            RandomRushAPI.INSTANCE.getMatchManager().getSpawnLocation() :
                            matchPlayer.getCatchedPlayerLocation());
    }

    public void updateTime(int newTime) {
        for (MatchPlayer player : getPlayers()) {
            player.getPlayer().setLevel(newTime);
        }
    }

    public enum Status {
        WAITING,
        PLAYING
    }
}
