package me.rejomy.randomrush.util.inventory;

import com.cryptomorin.xseries.XMaterial;
import lombok.Getter;
import me.rejomy.randomrush.util.StringUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class InventoryItem {
    private XMaterial material;
    private String name;
    private List<String> lore;
    @Getter
    private List<String> commands = new ArrayList<>();
    @Getter
    private Consumer<Player> consumer;
    @Getter
    private BiConsumer<Player, InventoryClickEvent> biConsumer;

    public InventoryItem setMaterial(XMaterial material) {
        this.material = material;

        return this;
    }

    public InventoryItem setConsumer(Consumer<Player> consumer) {
        this.consumer = consumer;

        return this;
    }

    public InventoryItem setBiConsumer(BiConsumer<Player, InventoryClickEvent> biConsumer) {
        this.biConsumer = biConsumer;

        return this;
    }

    public InventoryItem setName(String name, Object... replacers) {
        this.name = StringUtil.apply(name, replacers);

        return this;
    }

    public InventoryItem setLore(List<String> lore, Object... replacers) {
        this.lore = StringUtil.apply(lore, replacers);

        return this;
    }

    public InventoryItem setCommands(List<String> commands) {
        this.commands = commands;

        return this;
    }

    // ** Convert this item to bukkit item.
    public static ItemStack build(InventoryItem item) {
        ItemStack itemStack = item.material.parseItem();
        ItemMeta meta = itemStack.getItemMeta();

        meta.setDisplayName(item.name);
        meta.setLore(item.lore);

        itemStack.setItemMeta(meta);

        return itemStack;
    }
}
