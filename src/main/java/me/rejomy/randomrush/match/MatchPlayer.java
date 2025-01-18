package me.rejomy.randomrush.match;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.entity.Player;

@Getter
@Setter
public class MatchPlayer {
    Player player;
    Match match;

    @Getter
    Location catchedPlayerLocation;
    boolean spectator;

    public MatchPlayer (Player player, Match match) {
        this.player = player;
        this.match = match;

        this.catchedPlayerLocation = player.getLocation();
    }
}
