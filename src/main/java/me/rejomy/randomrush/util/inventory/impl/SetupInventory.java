package me.rejomy.randomrush.util.inventory.impl;

import lombok.Getter;
import me.rejomy.randomrush.RandomRushAPI;
import me.rejomy.randomrush.arena.Arena;
import me.rejomy.randomrush.arena.SetupArena;
import me.rejomy.randomrush.util.Utils;
import me.rejomy.randomrush.util.inventory.InventoryBuilder;
import me.rejomy.randomrush.util.inventory.InventoryItem;
import me.rejomy.randomrush.util.world.Position;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.io.IOException;
import java.util.stream.Collectors;

// TODO: Возможность удалять арену, потому что в /rr setup нельзя редактировать или создавать новые, если ты уже что-то редактируешь.
// TODO: Мы создаем для каждого игрока в /rr setup свой экземпляр этого класса с arena и если 2 игрока откроют одну арену, то кто последний сохранит, те настройки пременятся.
// TODO: В event`ax будет приниматься любое сообщение и любой интеракт по блоку, оно ведь не знает какой конкретно игрок нам нужен.
public class SetupInventory extends InventoryBuilder {
    final static YamlConfiguration config = RandomRushAPI.INSTANCE.getConfigManager().getSetupInventoryConfig();

    boolean shouldWriteArenaName;
    boolean canSetPositions;
    boolean canSetCenter;
    @Getter
    SetupArena arena;

    public SetupInventory() {
        super(config.getString("name"), config.getInt("slots"));

        this.arena = new SetupArena();
    }

    public SetupInventory(Arena arena) {
        super(config.getString("name"), config.getInt("slots"));

        this.arena = new SetupArena(arena);
    }

    @Override
    public void fill() {
        loadItemFromConfig(config, "items.set-arena-name",
                (player) -> {
                    shouldWriteArenaName = true;
                    canRegister = false;
                    canUnregister = false;
                    Utils.sendMessage(player, RandomRushAPI.INSTANCE.getConfigManager().getLang().getSetupShouldWriteName());
                    player.closeInventory();
                }, "name", arena.name == null? "" : arena.name);

        loadItemFromConfig(config, "items.set-player-per-team",
                (player) -> {
                    SetupPlayerPerTeamInventory pptInv = new SetupPlayerPerTeamInventory(this, arena);
                    pptInv.open(player);
                }, "ppt", arena.getPlayerPerTeam().stream()
                        .map(String::valueOf)
                        .collect(Collectors.joining(", ")));

        loadItemFromConfig(config, "items.set-max-players",
                (player, event) -> {
                    ClickType type = event.getClick();

                    if (type == ClickType.RIGHT)
                        arena.maxPlayers = Math.max(arena.minPlayers, arena.maxPlayers - 1);
                    else if (type == ClickType.LEFT) arena.maxPlayers += 1;

                    fill();
                }, "max_players", arena.maxPlayers);

        loadItemFromConfig(config, "items.set-min-players",
                (player, event) -> {
                    ClickType type = event.getClick();

                    if (type == ClickType.RIGHT)
                        arena.minPlayers = Math.max(2, arena.minPlayers - 1);
                    else if (type == ClickType.LEFT)
                        arena.minPlayers = Math.min(arena.maxPlayers, arena.minPlayers + 1);

                    fill();
                }, "min_players", arena.minPlayers);

        loadItemFromConfig(config, "items.select-spawn-pos",
                (player) -> {
                    canSetPositions = true;
                    canSetCenter = false;
                    canRegister = false;
                    canUnregister = false;
                    Utils.sendMessage(player, RandomRushAPI.INSTANCE.getConfigManager().getLang().getSetupHowPosition());
                    player.closeInventory();
                });

        loadItemFromConfig(config, "items.set-center-of-map",
                (player, event) -> {
                    canSetCenter = true;
                    canSetPositions = false;
                    canRegister = false;
                    canUnregister = false;
                    Utils.sendMessage(player, RandomRushAPI.INSTANCE.getConfigManager().getLang().getSetupHowPosition());
                    player.closeInventory();
                });

        loadItemFromConfig(config, "items.delete",
                (player, event) -> {
                    RandomRushAPI.INSTANCE.getArenaManager().delete(arena);
                    RandomRushAPI.INSTANCE.getDataManager().get(player).setArenaSetup(null);
                    player.closeInventory();
                });

        loadItemFromConfig(config, "items.save",
                (player) -> {
                    try {
                        if (arena.maxPlayers < arena.minPlayers) {
                            Utils.sendMessage(player, RandomRushAPI.INSTANCE.getConfigManager().getLang().getSetupSaveIncorrectPlayers(), "min", arena.minPlayers, "max", arena.maxPlayers);
                            return;
                        }

                        if (arena.name == null || arena.name.isEmpty()) {
                            Utils.sendMessage(player, RandomRushAPI.INSTANCE.getConfigManager().getLang().getSetupSaveIncorrectName());
                            return;
                        }

                        if (arena.getPlayerPerTeam().isEmpty()) {
                            arena.getPlayerPerTeam().add(1);
                        }

                        if (arena.getSpawnPositions().size() != arena.maxPlayers) {
                            Utils.sendMessage(player, RandomRushAPI.INSTANCE.getConfigManager().getLang().getSetupSaveIncorrectPositions());
                            return;
                        }

                        if (arena.worldName.isEmpty()) {
                            Utils.sendMessage(player, RandomRushAPI.INSTANCE.getConfigManager().getLang().getSetupSaveIncorrectWorldName());
                            return;
                        }

                        if (arena.getCenterPosition() == null) {
                            Utils.sendMessage(player, RandomRushAPI.INSTANCE.getConfigManager().getLang().getSetupSaveIncorrectCenter());
                            return;
                        }

                        if (!arena.isOverrideArena()) {
                            for (Arena gameArena : RandomRushAPI.INSTANCE.getArenaManager().getArenas()) {
                                if (gameArena.name.equalsIgnoreCase(arena.name)) {
                                    Utils.sendMessage(player, RandomRushAPI.INSTANCE.getConfigManager().getLang().getSetupSaveNameExist(), "name", arena.name);
                                    return;
                                }
                            }
                        }

                        Utils.sendMessage(player, RandomRushAPI.INSTANCE.getConfigManager().getLang().getSetupSaveSuccess(), "arena", arena.name);
                        arena.saveToFile();
                    } catch (IOException exception) {
                    }
                });
    }

    @Override
    public void handle(Player player, int slot, InventoryClickEvent event) {
        InventoryItem item = getItems().get(slot);

        // Run action when a player click to item.
        if (item.getConsumer() != null) {
            item.getConsumer().accept(player);
        } else if (item.getBiConsumer() != null) item.getBiConsumer().accept(player, event);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onChat(AsyncPlayerChatEvent event) {
        if (!shouldWriteArenaName) return;

        Player player = event.getPlayer();
        String message = event.getMessage().replaceAll("[^A-z0-9]", "");

        if (message.isEmpty()) {
            Utils.sendMessage(player, RandomRushAPI.INSTANCE.getConfigManager().getLang().getSetupSetEmptyName());
            return;
        }

        arena.name = message;
        Utils.sendMessage(player, RandomRushAPI.INSTANCE.getConfigManager().getLang().getSetupSuccessName(), "name", message);
        shouldWriteArenaName = false;

        canUnregister = true;

        open(player);
        fill();
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onInteract(PlayerInteractEvent event) {
        if (!canSetPositions && !canSetCenter) return;

        Player player = event.getPlayer();

        if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_AIR) return;

        Location location = player.getLocation();
        Position position = new Position(location);

        if (canSetPositions) {
            arena.worldName = location.getWorld().getName();
            arena.getSpawnPositions().add(position);

            event.setCancelled(true);

            Utils.sendMessage(player, RandomRushAPI.INSTANCE.getConfigManager().getLang().getSetupSuccessPosition(), "x", position.getX(), "z", position.getZ(), "y", position.getY(), "arena", arena.name);
        } else {
            arena.worldName = location.getWorld().getName();
            arena.setCenterPosition(position);

            event.setCancelled(true);

            Utils.sendMessage(player, RandomRushAPI.INSTANCE.getConfigManager().getLang().getSetupSuccessCenter(), "x", position.getX(), "z", position.getZ(), "y", position.getY(), "arena", arena.name);
        }

        canUnregister = true;
    }
}
