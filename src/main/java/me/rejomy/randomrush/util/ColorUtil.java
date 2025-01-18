package me.rejomy.randomrush.util;

import org.bukkit.ChatColor;

public class ColorUtil {
    public static String toColor (String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }
}
