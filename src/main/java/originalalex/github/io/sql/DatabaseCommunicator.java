package originalalex.github.io.sql;

import com.google.common.util.concurrent.Service;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.*;
import java.sql.Connection;
import java.sql.Statement;
import java.util.logging.Logger;

public class DatabaseCommunicator {

    private String url = "";
    private String root = "root";
    private String rootPass = "";

    private Connection connection;

    public DatabaseCommunicator(JavaPlugin plugin) {
        ConfigurationSection section = plugin.getConfig().getConfigurationSection("Database");
        String url = section.getString("host");
        String port = section.getString("port");
        String user = section.getString("user");
        String pass = section.getString("pass");
        this.root = user;
        this.rootPass = pass;
        this.url = "jdbc:mysql://" + url + ":" + port;
        connect();
    }

    public DatabaseCommunicator(String url, String port, String user, String pass) {
        this.url = "jdbc:mysql://" + url + ":" + port;
        this.root = user;
        this.rootPass = pass;
        connect();
    }

    private boolean connect() {
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost", root, rootPass);
            Statement statement = connection.createStatement();
            int r = statement.executeUpdate("CREATE DATABASE IF NOT EXISTS minecraft");
            statement.executeUpdate("USE minecraft");
            statement.execute("CREATE TABLE IF NOT EXISTS emails(id text, email varchar(30))");
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void writeEmail(String uuid, String email) {
        try {
            Statement statement = connection.createStatement();
            Logger.getLogger("Minecraft").warning("INSERT INTO emails VALUES('" + uuid + "', '" + email + "')");
            statement.executeUpdate("INSERT INTO emails VALUES('" + uuid + "', '" + email + "')");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateEmail(String uuid, String email) {
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate("UPDATE emails SET email='" + email + "' WHERE id LIKE '" + uuid + "'");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getEmail(String uuid) {
        try {
            Statement statement = connection.createStatement();
            ResultSet res = statement.executeQuery("SELECT * FROM emails WHERE id LIKE '" + uuid + "'");
            if (res.next() == false) {
                return null;
            }
            return res.getString("email");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}
