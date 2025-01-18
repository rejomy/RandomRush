package me.rejomy.randomrush.data;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import me.rejomy.randomrush.arena.Arena;
import me.rejomy.randomrush.util.inventory.impl.SetupInventory;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

@Getter
@Setter
public class PlayerData {
    Player player;
    String name;

    Arena arena;
    SetupInventory arenaSetup;

    @Setter(AccessLevel.NONE)
    StatisticalData statisticalData = new StatisticalData();

    public PlayerData (Player player) {
        this.player = player;
        this.name = player.getName();
    }
}
