package me.rejomy.randomrush.util.inventory;

import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class InventoryUtil {
    public static String getInventoryName(Inventory inventory) {
        try {
            // Попытка получить метод для Spigot 1.16.5
            HumanEntity viewer = inventory.getViewers().isEmpty() ? null : inventory.getViewers().get(0);
            if (viewer != null) {
                Method getOpenInventoryMethod = viewer.getClass().getMethod("getOpenInventory");
                getOpenInventoryMethod.setAccessible(true);
                InventoryView view = (InventoryView) getOpenInventoryMethod.invoke(viewer);

                if (view != null) {
                    Method getTitleMethod = view.getClass().getMethod("getTitle");
                    getTitleMethod.setAccessible(true);
                    return (String) getTitleMethod.invoke(view);
                }
            }
        } catch (NullPointerException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            // Если не найдено, попробуйте получить метод для Spigot 1.8
            return inventory.getName();
        }

        return null;
    }
}
