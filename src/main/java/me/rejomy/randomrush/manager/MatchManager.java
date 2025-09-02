package me.rejomy.randomrush.manager;

import lombok.Getter;
import lombok.Setter;
import me.rejomy.randomrush.RandomRushAPI;
import me.rejomy.randomrush.arena.Arena;
import me.rejomy.randomrush.interfaces.Loadable;
import me.rejomy.randomrush.interfaces.UnLoadable;
import me.rejomy.randomrush.match.Match;
import me.rejomy.randomrush.match.MatchPlayer;
import me.rejomy.randomrush.task.impl.WaitingTask;
import me.rejomy.randomrush.util.Utils;
import me.rejomy.randomrush.util.world.Position;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

// TODO: если мы сразу переносим игроков из лобби в старт игры, не делаю лобби ожидания,
//  при телепорте нельзя выбрать команду и если игроки в пати, их может закинуть в разные
public class MatchManager implements UnLoadable, Loadable {
    @Getter
    @Setter
    Location spawnLocation;
    List<Match> matches = new ArrayList<>();

    public Match create (Arena arena, int playersPerTeam) {
        Match match = new Match();
        match.setArena(arena);

        // Если матч будет генерировать количество игроков в команде на угад, то есть указав любое число,
        // мы возьмем любое из доступных.
        if (!match.getArena().getPlayerPerTeam().contains(playersPerTeam)) {
            List<Integer> ppt = match.getArena().getPlayerPerTeam();
            playersPerTeam = ppt.get(ThreadLocalRandom.current().nextInt(ppt.size()));
        }

        match.setPlayersPerTeam(playersPerTeam);

        match.setArena(arena);
        match.setPlayers(new ArrayList<>());

        // Spawn world for this match
        RandomRushAPI.INSTANCE.getWorldManager().load(match);
        match.getWorld().getWorldBorder().setCenter(Position.toLocation(match.getWorld(), match.getArena().getCenterPosition()));

        matches.add(match);

        return match;
    }

    public boolean addPlayer(Player player, Match match) {
        int playersSize = match.getPlayers().size();

        if (playersSize + 1 > match.getArena().maxPlayers) {
            Utils.sendMessage(player, RandomRushAPI.INSTANCE.getConfigManager().getLang().getWaitingIsFull());
            return false;
        }

        match.getPlayers().add(new MatchPlayer(player, match));

        if (playersSize + 1 == match.getArena().minPlayers) {
            WaitingTask waitingTask = new WaitingTask(match);
            waitingTask.run();
        }

        if (RandomRushAPI.INSTANCE.getConfigManager().getConfig().isTeleportToMap()) {
            int spawnPositionsSize = match.getArena().getSpawnPositions().size();
            player.teleport(
                    Position.toLocation(match.getWorld(), match.getArena().getSpawnPositions()
                            .get(playersSize % spawnPositionsSize)));
        }

        match.getPlayers().forEach(someMatchPlayer -> {
            Utils.sendMessage(someMatchPlayer.getPlayer(),
                    RandomRushAPI.INSTANCE.getConfigManager().getLang().getMatchJoin(),
                    "player", player.getName(),
                    "name", match.getArena().name,
                    "players", playersSize + 1,
                    "max", match.getArena().maxPlayers);

        });

        return true;
    }

    public boolean addPlayer(Player player, MatchCondition... conditions) {
        Match findMatch = matches.stream()
                .filter(match -> match.getStatus() != Match.Status.PLAYING &&
                        Arrays.stream(conditions).allMatch(condition -> condition.apply(match)))
                .max(Comparator.comparingInt(match -> match.getPlayers().size()))
                .orElse(null);

        return findMatch != null && addPlayer(player, findMatch);
    }

    public Match getMatch (Player player) {
        return matches.stream()
                .filter(match -> match.getPlayers() != null &&
                        match.getPlayers()
                        .stream()
                        .anyMatch(matchPlayer -> matchPlayer.getPlayer() == player))
                .findAny()
                .orElse(null);
    }

    public MatchPlayer getMatchPlayer (Player player) {
        return matches.stream()
                .flatMap(match -> match.getPlayers().stream())
                .filter(matchPlayer -> matchPlayer.getPlayer() == player)
                .findAny()
                .orElse(null);
    }

    public int getTotalOnline() {
        return matches.stream().mapToInt(match -> match.getPlayers().size()).sum();
    }

    public boolean isInMatch (Player player) {
        return getMatch(player) != null;
    }

    @Override
    public void unload() {
        // TODO: stop all games and teleport players who in game to spawn.
        new ArrayList<>(matches).forEach(Match::end);
    }

    @Override
    public void load() {
        for (Arena arena : RandomRushAPI.INSTANCE.getArenaManager().arenas) {
            for (int ppt : arena.getPlayerPerTeam()) {
                int preloadWorldsAmount = RandomRushAPI.INSTANCE.getConfigManager().getConfig().getPreloadWorldsPerArenaAmount();

                while (preloadWorldsAmount-- > 0) {
                    create(arena, ppt);
                }
            }
        }
    }

    public void delete(Match match) {
        matches.remove(match);
    }

    public interface MatchCondition {
        boolean apply(Match match);
    }
}
