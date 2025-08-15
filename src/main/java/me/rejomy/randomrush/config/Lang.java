package me.rejomy.randomrush.config;

import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class Lang extends YamlConfig {
    String setupSetEmptyName;
    String setupSuccessName;
    String setupShouldWriteName;
    String setupSuccessPosition;
    String setupSuccessCenter;
    String setupHowPosition;
    String setupSaveIncorrectPlayers;
    String setupSaveIncorrectName;
    String setupSaveIncorrectPositions;
    String setupSaveIncorrectWorldName;
    String setupSaveIncorrectCenter;
    String setupSaveNameExist;
    String setupSaveSuccess;

    String waitingIsFull;

    String matchMaxBuildY;
    String matchJoin;
    String matchJoinAlreadyInTheGame;
    String matchLeave;
    String matchLeaveNotInMatch;
    String matchRemove;
    String matchDie;

    String spawn;

    HashMap<Byte, String> startMessages = new HashMap<>();

    List<String> commandAvailableCommands;

    public Lang(FileConfiguration config) {
        super(config);
    }

    @Override
    public void load() {
        setupSetEmptyName = getStringElse("setup.set-name.empty", "Arena name cannot be empty.");
        setupSuccessName = getStringElse("setup.set-name.success", "You success set name $name for the arena.");
        setupShouldWriteName = getStringElse("setup.set-name.should-write", "You should write arena name in chat. You can use english alphabet and numbers.");

        setupSuccessPosition = getStringElse("setup.set-position.success", "Position $x $y $z has been added for $arena!");
        setupSuccessCenter = getStringElse("setup.set-center.success", "Center $x $y $z has been added for $arena!");
        setupHowPosition = getStringElse("setup.set-position.how", "When you do right click in your mouse, your legs location will be saved as position.");

        setupSaveIncorrectPlayers = getStringElse("setup.save.incorrect-players", "You should select min and max values which are greater than zero. $min $max");
        setupSaveIncorrectName = getStringElse("setup.save.incorrect-name", "You should write arena name.");
        setupSaveIncorrectPositions = getStringElse("setup.save.incorrect-positions", "You should set number of positions equals max players.");
        setupSaveIncorrectWorldName = getStringElse("setup.save.incorrect-world-name", "You must specify the world to be used.");
        setupSaveIncorrectCenter = getStringElse("setup.save.incorrect-center", "You should set center of arena.");
        setupSaveNameExist = getStringElse("setup.save.name-exist", "The name of arena already exist. Please write any name. Arena name=$name");
        setupSaveSuccess = getStringElse("setup.save.success", "Arena $arena was success saved to file! :)");

        waitingIsFull = getStringElse("waiting.is-full", "You cant connect to the game, because the match is full.");

        matchMaxBuildY = getStringElse("match.max-build-y", "You cant place blocks here.");
        matchJoin = getStringElse("match.join", "You joined to $name match! $players/$max");
        matchJoinAlreadyInTheGame = getStringElse("match.join-already-is-in-match", "You already in the game, write /rr leave and try to rejoin.");
        matchLeave = getStringElse("match.leave", "You leave from $name");
        matchLeaveNotInMatch = getStringElse("match.leave-not-in-match", "You are not current in a match.");
        matchRemove = getStringElse("match.remove", "You has been removed from $name");
        matchDie = getStringElse("match.die", "You are die :(");

        spawn = getStringElse("command.spawn", "Spawn was success set to $world $x $y $z $yaw $pitch");
        commandAvailableCommands = getStringListElse("command.available-commands", new ArrayList<>());

        for (byte percent : config.getConfigurationSection("start-message").getKeys(false).stream().map(Byte::parseByte).toList()) {
            startMessages.put(percent, config.getString("start-message." + percent));
        }
    }
}
