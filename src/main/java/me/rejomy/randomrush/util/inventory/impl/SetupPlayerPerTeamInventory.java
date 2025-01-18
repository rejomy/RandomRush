package me.rejomy.randomrush.util.inventory.impl;

import me.rejomy.randomrush.RandomRushAPI;
import me.rejomy.randomrush.arena.Arena;
import me.rejomy.randomrush.arena.SetupArena;
import me.rejomy.randomrush.util.inventory.InventoryBuilder;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class SetupPlayerPerTeamInventory extends InventoryBuilder {
    final static YamlConfiguration config = RandomRushAPI.INSTANCE.getConfigManager().getSetupPlayerPerTeamInventoryConfig();
    Arena arena;
    SetupInventory parentInventory;
    HashMap<Integer, Integer> teams = new HashMap<>();
    final int addTeamSlot = config.getInt("items.add-team.slot");

    public SetupPlayerPerTeamInventory(SetupInventory parentInventory, SetupArena arena) {
        super(config.getString("name"), config.getInt("slots"));

        this.arena = arena;
        this.parentInventory = parentInventory;

        int i = 0;

        for (int size : arena.getPlayerPerTeam()) {
            teams.put(i++, size);
        }
    }

    @Override
    public void fill() {
        loadItemsFromConfig(config);

        arena.getPlayerPerTeam().clear();

        // Remove player per teams duplicates.
        // Player can add duplicate in config or in setup add player per team inventory.
        teams.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey, (a, b) -> a))
                .forEach((k, v) -> teams.entrySet().removeIf(e -> e.getValue().equals(k) && !e.getKey().equals(v)));

        for (int a = 0; a < teams.size(); a++) {
            int ppt = teams.get(a);

            arena.getPlayerPerTeam().add(ppt);

            setItem(getItemFromConfig(config, "items.abstract-team", "ppt", String.valueOf(ppt)), a);
        }
    }

    @Override
    public void handle(Player player, int slot, InventoryClickEvent event) {
        if (slot == addTeamSlot) {
            teams.put(teams.size(), 1);
            AddPlayerPerTeamInventory ppt = new AddPlayerPerTeamInventory(arena, this, teams, teams.size() - 1);
            ppt.open(player);
        }
        else if (slot < teams.size()) {
            new AddPlayerPerTeamInventory(arena, this, teams, slot).open(player);
        }
        else if (slot == config.getInt("items.return.slot")) {
            parentInventory.fill();
            parentInventory.open(player);
        }
    }
}
