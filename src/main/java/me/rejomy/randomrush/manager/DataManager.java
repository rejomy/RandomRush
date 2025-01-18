package me.rejomy.randomrush.manager;

import me.rejomy.randomrush.RandomRushAPI;
import me.rejomy.randomrush.arena.Arena;
import me.rejomy.randomrush.data.PlayerData;
import me.rejomy.randomrush.data.database.DataBase;
import me.rejomy.randomrush.interfaces.UnLoadable;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DataManager implements UnLoadable {
    List<PlayerData> playerDataList = new ArrayList<>();

    public void add(Player player) {
        PlayerData data = new PlayerData(player);

        if (RandomRushAPI.INSTANCE.getConfigManager().getConfig().isStorage()) {
            try {
                RandomRushAPI.INSTANCE.getDataBase().loadStatisticalData(player.getUniqueId(), data.getStatisticalData());
            } catch (SQLException exception) {
                RandomRushAPI.INSTANCE.getPlugin().getLogger().severe("");
                RandomRushAPI.INSTANCE.getPlugin().getLogger().severe("Error when load player stats from database.");
                RandomRushAPI.INSTANCE.getPlugin().getLogger().severe("Player=" + player.getName() + " uuid=" + player.getUniqueId());
                exception.printStackTrace();
                RandomRushAPI.INSTANCE.getPlugin().getLogger().severe("");
            }
        }

        playerDataList.add(data);
    }

    public PlayerData get(Player player) {
        return playerDataList.stream().filter(data -> data.getPlayer() == player).findAny().orElse(null);
    }

    public PlayerData get(String name) {
        return playerDataList.stream().filter(data -> data.getName().equals(name)).findAny().orElse(null);
    }

    public PlayerData get(Arena arena) {
        return playerDataList.stream().filter(data -> data.getArena() == arena).findAny().orElse(null);
    }

    public void remove(Player player) {
        playerDataList.removeIf(playerData -> {
            boolean remove = playerData.getPlayer() == player;

            if (remove) {
                if (playerData.getArenaSetup() != null) {
                    playerData.getArenaSetup().unregister();
                }
            }

            return remove;
        });
    }

    @Override
    public void unload() {
        for (PlayerData data : playerDataList) {
            if (data.getArenaSetup() != null) {
                // Close arena setup inventory if it is open for player.
                if (data.getArenaSetup().getInventory().getViewers().contains(data.getPlayer())) {
                    data.getPlayer().closeInventory();
                }

                // Unregister listener for arena setup.
                data.getArenaSetup().unregister();
            }
        }

       playerDataList.clear();
    }

    public void savePlayersStatistics() {
        DataBase dataBase = RandomRushAPI.INSTANCE.getDataBase();

        if (dataBase == null) return;

        try {
            for (Player player : Bukkit.getOnlinePlayers()) {
                PlayerData data = get(player);

                dataBase.savePlayerData(data);
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }
}
