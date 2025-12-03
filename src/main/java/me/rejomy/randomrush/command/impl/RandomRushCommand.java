package me.rejomy.randomrush.command.impl;

import me.rejomy.randomrush.RandomRushAPI;
import me.rejomy.randomrush.arena.Arena;
import me.rejomy.randomrush.command.Command;
import me.rejomy.randomrush.data.PlayerData;
import me.rejomy.randomrush.match.MatchPlayer;
import me.rejomy.randomrush.util.StringUtil;
import me.rejomy.randomrush.util.Utils;
import me.rejomy.randomrush.util.inventory.impl.SelectArenaInventory;
import me.rejomy.randomrush.util.inventory.impl.SetupInventory;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.stream.Collectors;

public class RandomRushCommand extends Command {
    public RandomRushCommand() {
        super(null, 0);
    }

    @Override
    protected void handle(CommandSender sender, String... args) {
        if (args.length == 0) {
            Utils.sendMessage(sender, lang.getCommandAvailableCommands());
            return;
        }

        switch (args[0].toLowerCase()) {
            case "setup" -> {
                if (sender instanceof ConsoleCommandSender) {
                    sender.sendMessage("This command can be executed only from player!");
                    return;
                }

                Player player = (Player) sender;
                PlayerData data = RandomRushAPI.INSTANCE.getDataManager().get(player);

                if (data.getArenaSetup() != null) {
                    data.getArenaSetup().open(player);
                    return;
                }

                SetupInventory setupInventory;

                if (args.length == 2) {
                    Arena arena = RandomRushAPI.INSTANCE.getArenaManager().getByName(args[1]);

                    if (arena == null) {
                        Utils.sendMessage(sender, "Arena not found!");
                        return;
                    }

                    setupInventory = new SetupInventory(arena);
                } else setupInventory = new SetupInventory();

                data.setArenaSetup(setupInventory);
                data.getArenaSetup().open(player);
            }

            case "list" -> {
                String arenaNames = RandomRushAPI.INSTANCE.getArenaManager().getArenas().stream()
                        .map(arena -> arena.name)
                        .collect(Collectors.joining(", "));

                Utils.sendMessage(sender, "Arenas loaded: " + arenaNames);
            }

            case "menu" -> {
                if (sender instanceof ConsoleCommandSender) {
                    sender.sendMessage("This command can be executed only from player!");
                    return;
                }

                Player player = (Player) sender;

                new SelectArenaInventory().open(player);
            }

            case "join" -> {
                Player player = Bukkit.getPlayer(args[1]);

                if (RandomRushAPI.INSTANCE.getMatchManager().isInMatch(player)) {
                    Utils.sendMessage(player, RandomRushAPI.INSTANCE.getConfigManager().getLang().getMatchJoinAlreadyInTheGame());
                    return;
                }

                Integer playersPerTeam = args.length == 3 ? Integer.parseInt(args[2]) : null;
                boolean successAdded = RandomRushAPI.INSTANCE.getMatchManager().addPlayer(player,
                        playersPerTeam != null ?
                        (match) -> match.getPlayersPerTeam() == playersPerTeam : null);

                if (!successAdded) {
                    RandomRushAPI.INSTANCE.getPlugin().getLogger().severe("Error when trying to add " + player.getName() + " with " + playersPerTeam + " to match.");
                    RandomRushAPI.INSTANCE.getPlugin().getLogger().severe("Maybe match with " + playersPerTeam + " player per team amount not found?");
                }
            }

            case "leave" -> {
                if (sender instanceof ConsoleCommandSender) {
                    sender.sendMessage("This command can be executed only from player!");
                    return;
                }

                MatchPlayer matchPlayer = RandomRushAPI.INSTANCE.getMatchManager().getMatchPlayer((Player) sender);

                if (matchPlayer != null) {
                    matchPlayer.getMatch().removePlayer(matchPlayer, true);
                    Utils.sendMessage(sender, RandomRushAPI.INSTANCE.getConfigManager().getLang().getMatchLeave(), "name", matchPlayer.getMatch().getArena().name);
                } else Utils.sendMessage(sender, RandomRushAPI.INSTANCE.getConfigManager().getLang().getMatchLeaveNotInMatch());
            }

            case "spawn" -> {
                if (sender instanceof ConsoleCommandSender) {
                    sender.sendMessage("This command can be executed only from player!");
                    return;
                }

                Player player = (Player) sender;
                Location location = player.getLocation();

                File file = new File(RandomRushAPI.INSTANCE.getPlugin().getDataFolder(), "spawn.yml");

                try {
                    if (!file.exists()) {
                        file.createNewFile();
                    }

                    YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
                    String world = location.getWorld().getName();
                    double x = location.getX(),
                            y = location.getY(),
                            z = location.getZ(),
                            yaw = location.getYaw(),
                            pitch = location.getPitch();

                    config.set("world", world);
                    config.set("x", x);
                    config.set("y", y);
                    config.set("z", z);
                    config.set("yaw", yaw);
                    config.set("pitch", pitch);

                    config.save(file);
                    sender.sendMessage(StringUtil.apply(lang.getSpawn(), "world", world, "x", x, "y", y, "z", z,
                            "yaw", yaw, "pitch", pitch));
                    RandomRushAPI.INSTANCE.getMatchManager().setSpawnLocation(location);
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            }

            default -> Utils.sendMessage(sender, lang.getCommandAvailableCommands());
        }
    }
}
