package me.rejomy.randomrush.command.impl;

import me.rejomy.randomrush.RandomRushAPI;
import me.rejomy.randomrush.arena.Arena;
import me.rejomy.randomrush.command.Command;
import me.rejomy.randomrush.data.PlayerData;
import me.rejomy.randomrush.match.MatchPlayer;
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
                if (args.length == 2) {
                    Player player = Bukkit.getPlayer(args[1]);

                    RandomRushAPI.INSTANCE.getMatchManager().addPlayer(player);
                } else if (args.length == 3) {
                    Player player = Bukkit.getPlayer(args[1]);
                    int playerPerTeam = Integer.parseInt(args[2]);

                    if (RandomRushAPI.INSTANCE.getMatchManager().isInMatch(player)) {
                        Utils.sendMessage(player, RandomRushAPI.INSTANCE.getConfigManager().getLang().getMatchJoinAlreadyInTheGame());
                        return;
                    }

                    if (!RandomRushAPI.INSTANCE.getMatchManager().addPlayer(player,
                            (match) -> match.getPlayersPerTeam() == playerPerTeam)) {
                        RandomRushAPI.INSTANCE.getPlugin().getLogger().severe("Error when trying to add " + player.getName() + " with " + playerPerTeam + " to match.");
                        RandomRushAPI.INSTANCE.getPlugin().getLogger().severe("Maybe match with " + playerPerTeam + " player per team amount not found?");
                    }
                }
            }

            case "leave" -> {
                if (sender instanceof ConsoleCommandSender) {
                    sender.sendMessage("This command can be executed only from player!");
                    return;
                }

                MatchPlayer matchPlayer = RandomRushAPI.INSTANCE.getMatchManager().getMatchPlayer((Player) sender);

                if (matchPlayer != null) {
                    matchPlayer.getMatch().removePlayer(matchPlayer);
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

                    config.set("world", location.getWorld().getName());
                    config.set("x", location.getX());
                    config.set("y", location.getY());
                    config.set("z", location.getZ());
                    config.set("yaw", location.getYaw());
                    config.set("pitch", location.getPitch());

                    config.save(file);
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            }

            default -> Utils.sendMessage(sender, lang.getCommandAvailableCommands());
        }
    }
}
