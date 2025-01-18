package me.rejomy.randomrush.command;

import me.rejomy.randomrush.RandomRushAPI;
import me.rejomy.randomrush.config.Config;
import me.rejomy.randomrush.config.Lang;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public abstract class Command implements CommandExecutor {
    protected static final Config config = RandomRushAPI.INSTANCE.getConfigManager().getConfig();
    protected static final Lang lang = RandomRushAPI.INSTANCE.getConfigManager().getLang();

    String name;
    int args;

    protected Command (String name, int argsLength) {
        this.name = name;
        this.args = argsLength;
    }

    protected abstract void handle(CommandSender sender, String... args);

    @Override
    public boolean onCommand(CommandSender commandSender, org.bukkit.command.Command command, String s, String[] strings) {
        handle(commandSender, strings);
        return true;
    }
}
