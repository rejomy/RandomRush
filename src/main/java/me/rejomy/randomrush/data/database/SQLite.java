package me.rejomy.randomrush.data.database;

import me.rejomy.randomrush.RandomRushAPI;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLite extends DataBase {

    public SQLite() {

        try {
            Class.forName("org.sqlite.JDBC").newInstance();

            connection = getConnection();
            Statement statement = connection.createStatement();

            statement.executeUpdate("CREATE TABLE IF NOT EXISTS users (uuid TEXT PRIMARY KEY, " +
                    "games INT, " +
                    "wins INT, " +
                    "kills INT, " +
                    "deaths INT, " +
                    "changeTime LONG)");

            statement.close();
        } catch (Exception ignored) {}

    }

    @Override
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:sqlite:plugins/" + RandomRushAPI.INSTANCE.getPlugin().getDescription().getName() + "/users.db");
    }

}
