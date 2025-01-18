package me.rejomy.randomrush.util.inventory.impl;

import me.rejomy.randomrush.RandomRushAPI;
import me.rejomy.randomrush.arena.Arena;
import me.rejomy.randomrush.util.inventory.InventoryBuilder;
import me.rejomy.randomrush.util.inventory.InventoryItem;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.HashMap;
import java.util.List;

public class AddPlayerPerTeamInventory extends InventoryBuilder {
    final static YamlConfiguration config = RandomRushAPI.INSTANCE.getConfigManager().getSetupPlayerPerTeamAddInventoryConfig();
    final Arena arena;
    final int elementIndex;
    final HashMap<Integer, Integer> map;
    final int selectSlot = config.getInt("items.select-amount.slot");
    final SetupPlayerPerTeamInventory inventory;

    public AddPlayerPerTeamInventory(Arena arena, SetupPlayerPerTeamInventory inventory, HashMap<Integer, Integer> map, int elementIndex) {
        super(config.getString("name"), config.getInt("slots"));

        this.map = map;
        this.elementIndex = elementIndex;
        this.arena = arena;

        this.inventory = inventory;

        fill();
    }

    @Override
    public void fill() {
        loadItemsFromConfig(config);

        updatePPTAmount();
    }

    @Override
    public void handle(Player player, int slot, InventoryClickEvent event) {
        if (slot == config.getInt("items.select-amount.slot")) {
            if (event.getClick() == ClickType.LEFT) {
                map.put(elementIndex, map.get(elementIndex) + 1);
                updatePPTAmount();
            }
            else if (event.getClick() == ClickType.RIGHT) {
                map.put(elementIndex, Math.max(1, map.get(elementIndex) - 1));
                updatePPTAmount();
            }
        }
        else if (slot == config.getInt("items.return.slot")) {
            inventory.fill();
            inventory.open(player);
        }
    }

    void updatePPTAmount() {
        List<String> selectAmountLore = config.getStringList("items.select-amount.lore");
        selectAmountLore.replaceAll(value -> value.replaceAll("\\$ppt", String.valueOf(map.get(elementIndex))));

        getItems().get(selectSlot).setLore(selectAmountLore);

        getInventory().setItem(selectSlot, InventoryItem.build(getItems().get(selectSlot)));
    }
}
