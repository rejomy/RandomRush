package me.rejomy.randomrush.util.inventory;

import com.cryptomorin.xseries.XMaterial;
import lombok.Getter;
import me.rejomy.randomrush.RandomRushAPI;
import me.rejomy.randomrush.util.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public abstract class InventoryBuilder implements Listener {
    @Getter
    final Inventory inventory;
    @Getter
    HashMap<Integer, InventoryItem> items = new HashMap<>();
    protected boolean canRegister = true, canUnregister = true;

    public InventoryBuilder (String name, int size) {
        this(Bukkit.createInventory(RRInventoryHolder.INSTANCE, size, ColorUtil.toColor(name)));
    }

    public InventoryBuilder (String name, InventoryType type) {
        this(Bukkit.createInventory(RRInventoryHolder.INSTANCE, type, ColorUtil.toColor(name)));
    }

    private InventoryBuilder (Inventory inventory) {
        this.inventory = inventory;

        register();
    }

    public abstract void fill();

    public abstract void handle(final Player player, final int slot, final InventoryClickEvent event);

    @EventHandler(priority = EventPriority.LOW)
    public void onClick (InventoryClickEvent event) {
        int slot = event.getSlot();

        // We should`t check from inventory clicks, its unnecessary :)
        if (slot < 0) return;

        Inventory inventory = event.getClickedInventory();

        if (isThisInventory(inventory)) {
            // We cancel this action, because a player can move his item to slot and close inventory.
            // Player loose item.
            // Or player can steal items from inventory.
            event.setCancelled(true);

            // Check if items contains slot, if not, player click to empty slot...
            if (!items.containsKey(slot)) {
                return;
            }

            Player player = (Player) event.getWhoClicked();

            // Если в строке commands что-то указано, выполняем это здесь, а потом передаем на обработку.
            for (String command : getItems().get(slot).getCommands()) {
                char[] chars = command.toCharArray();

                if (chars.length > 2 && chars[0] == '[' && chars[chars.length - 1] == ']') {
                    command = command.replaceAll("[^A-Za-z]", "");

                    switch (command.toLowerCase()) {
                        case "close": {
                            player.closeInventory();
                        }
                    }
                } else Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("$player", player.getName()));
            }
            // ***

            handle(player, event.getSlot(), event);
        }
    }

    // Remove handlers from here if no one is watching
    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        Inventory inventory = event.getInventory();

        if (isThisInventory(inventory)) {
            if (inventory.getViewers().size() == 1) {
                unregister();
            }
        }
    }

    public void open(Player player) {
        // Если до этого инвентарь не был никем открыт, то есть, его либо открывают в первый раз,
        // либо давно не открывали, мы его обновляем и регистрируем слушатели.
        if (inventory.getViewers().isEmpty()) {
            fill();
            register();
        }

        player.openInventory(inventory);
    }

    protected void loadItemsFromConfig(FileConfiguration config) {
        for (String itemName : config.getConfigurationSection("items").getKeys(false)) {
            InventoryItem item = new InventoryItem()
                    .setName(config.getString("items." + itemName + ".name"))
                    .setLore(config.getStringList("items." + itemName + ".lore"))
                    .setCommands(config.getStringList("items." + itemName + ".commands"))
                    .setMaterial(XMaterial.valueOf(config.getString("items." + itemName + ".material")));

            Object slot = config.get("items." + itemName + ".slot");

            if (slot == null) {
                continue;
            }

            items.put((int) slot, item);
            setItem(item, (int) slot);
        }
    }

    protected void loadItemFromConfig(FileConfiguration config, String path, BiConsumer<Player, InventoryClickEvent> task, Object... replacers) {
        InventoryItem item = new InventoryItem()
                .setMaterial(XMaterial.valueOf(config.getString(path + ".material")))
                .setName(config.getString(path + ".name"), replacers)
                .setLore(config.getStringList(path + ".lore"), replacers)
                .setBiConsumer(task);

        setItem(item, config.getInt(path + ".slot"));
    }

    protected void loadItemFromConfig(FileConfiguration config, String path, Consumer<Player> task, Object... replacers) {
        InventoryItem item = new InventoryItem()
                .setMaterial(XMaterial.valueOf(config.getString(path + ".material")))
                .setName(config.getString(path + ".name"), replacers)
                .setLore(config.getStringList(path + ".lore"), replacers)
                .setConsumer(task);

        setItem(item, config.getInt(path + ".slot"));
    }

    protected void loadItemFromConfig(FileConfiguration config, String path, Object... replacers) {
        InventoryItem item = new InventoryItem()
                .setMaterial(XMaterial.valueOf(config.getString(path + ".material")))
                .setName(config.getString(path + ".name"), replacers)
                .setLore(config.getStringList(path + ".lore"), replacers);

        setItem(item, config.getInt(path + ".slot"));
    }

    protected void setItem(InventoryItem item, int slot) {
        items.put(slot, item);
        // Sometimes we need to create null item for handle it on handle() method.
        // same with setName
        if (item == null) return;
        inventory.setItem(slot, InventoryItem.build(item));
    }

    protected static InventoryItem getItemFromConfig(FileConfiguration config, String path, Object... replacers) {
        return new InventoryItem()
                .setMaterial(XMaterial.valueOf(config.getString(path + ".material")))
                .setName(config.getString(path + ".name"), replacers)
                .setLore(config.getStringList(path + ".lore"), replacers);
    }

    protected boolean isThisInventory(Inventory inventory) {
        return inventory.getHolder() == RRInventoryHolder.INSTANCE &&
                InventoryUtil.getInventoryName(inventory).equals(InventoryUtil.getInventoryName(getInventory()));
    }

    void register() {
        if (!canRegister) return;

        canUnregister = true;
        canRegister = false;
        Bukkit.getPluginManager().registerEvents(this, RandomRushAPI.INSTANCE.getPlugin());
    }

    public void unregister() {
        if (!canUnregister) return;

        canRegister = true;
        HandlerList.unregisterAll(this);
    }
}
