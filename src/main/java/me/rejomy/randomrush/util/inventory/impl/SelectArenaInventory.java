package me.rejomy.randomrush.util.inventory.impl;

import me.rejomy.randomrush.RandomRushAPI;
import me.rejomy.randomrush.util.inventory.InventoryBuilder;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public class SelectArenaInventory extends InventoryBuilder {
    final static YamlConfiguration config = RandomRushAPI.INSTANCE.getConfigManager().getSelectArenaInventoryConfig();

    public SelectArenaInventory() {
        super(config.getString("name"), config.getInt("slots"));
    }

    @Override
    public void fill() {
        loadItemsFromConfig(config);
    }

    @Override
    public void handle(Player player, int slot, InventoryClickEvent event) {

    }
}
