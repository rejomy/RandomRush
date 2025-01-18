package me.rejomy.randomrush.data.database;

import me.rejomy.randomrush.RandomRushAPI;
import me.rejomy.randomrush.data.PlayerData;
import me.rejomy.randomrush.data.StatisticalData;

import java.sql.*;
import java.util.UUID;

public abstract class DataBase {
    protected abstract Connection getConnection() throws SQLException;

    public Connection connection;

    public void set(UUID uuid, int games, int wins, int kills, int deaths, long time) throws SQLException {
        executeUpdate("INSERT OR REPLACE INTO users VALUES ('" + uuid + "', '" + games + "', '" + wins + "', '" +
                    kills + "', '" + deaths + "', '" + time + "')");
    }

    public String get(UUID uuid, int column) {
        ResultSet set = null;

        try {
            set = executeQuery("SELECT * FROM users WHERE uuid='" + uuid + "'");
            String value = set.getString(column);
            return value;
        } catch (SQLException | NullPointerException exception) {
            exception.printStackTrace();
        } finally {
            try {
                set.close();
                set.getStatement().close();
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        }

        return "";
    }

    public void remove(UUID uuid) throws SQLException {
        executeUpdate("DELETE FROM users WHERE uuid='" + uuid + "'");
    }

    private ResultSet executeQuery(String query) throws SQLException {
        Statement statement = connection.createStatement();
        return statement.executeQuery(query);
    }

    private void executeUpdate(String query) throws SQLException {
        Statement statement = connection.createStatement();
        statement.executeUpdate(query);
        statement.close();
    }

    public void loadStatisticalData(UUID uuid, StatisticalData statisticalData) throws SQLException {
        // Выполняем запрос, который выбирает все записи из таблицы users
        ResultSet resultSet = null;

        try {
            resultSet = executeQuery("SELECT * FROM users WHERE uuid = '" + uuid + "'");

            while (resultSet.next()) {
                // Перебираем все записи в result set
                long time = resultSet.getLong("changeTime");

                if (System.currentTimeMillis() - time > RandomRushAPI.INSTANCE.getConfigManager().getConfig().getStorageRemoveDataAfter()) {
                    remove(uuid);
                } else {
                    // Заполняем его поля значениями из result set
                    statisticalData.deaths = resultSet.getInt("deaths");
                    statisticalData.kills = resultSet.getInt("kills");
                    statisticalData.wins = resultSet.getInt("wins");
                    statisticalData.games = resultSet.getInt("games");
                }
            }
        } finally {
            if (resultSet != null) {
                resultSet.close();
                resultSet.getStatement().close();
            }
        }
    }

    public void savePlayerData(PlayerData playerData) throws SQLException {
        StatisticalData statisticalData = playerData.getStatisticalData();

        set(playerData.getPlayer().getUniqueId(), statisticalData.games, statisticalData.wins,
                statisticalData.kills, statisticalData.deaths, System.currentTimeMillis());
    }
}
