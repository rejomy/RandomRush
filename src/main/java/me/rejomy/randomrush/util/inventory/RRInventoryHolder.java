package me.rejomy.randomrush.util.inventory;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public enum RRInventoryHolder implements InventoryHolder {
    INSTANCE;

    @Override
    public Inventory getInventory() {
        return null;
    }
}
