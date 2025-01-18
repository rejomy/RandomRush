package me.rejomy.randomrush.util;

import lombok.experimental.UtilityClass;
import org.bukkit.command.CommandSender;

import java.util.List;

@UtilityClass
public class Utils {

    public void sendMessage(CommandSender sender, String message, Object... replacements) {
        if (message == null || message.isEmpty()) return;

        for (int i = 1; i <= replacements.length; i += 2) {
            message = message.replaceAll("\\$" + replacements[i - 1], String.valueOf(replacements[i]));
        }

        sender.sendMessage(ColorUtil.toColor(message));
    }

    public void sendMessage(CommandSender sender, List<String> messages) {
        if (messages == null || messages.isEmpty()) return;

        messages.forEach(message -> sendMessage(sender, message));
    }
}
