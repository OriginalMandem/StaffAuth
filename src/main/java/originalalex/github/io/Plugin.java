package originalalex.github.io;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import originalalex.github.io.commands.Login;
import originalalex.github.io.commands.Register;
import originalalex.github.io.events.LoginEvent;
import originalalex.github.io.sql.DatabaseCommunicator;

public class Plugin extends JavaPlugin {

    @Override
    public void onEnable() {
        saveDefaultConfig();
        DatabaseCommunicator db = new DatabaseCommunicator(this);
        LoginEvent loginEvent = new LoginEvent(this, db);
        getCommand("login").setExecutor(new Login(loginEvent));
        getCommand("register").setExecutor(new Register(db));
        Bukkit.getServer().getPluginManager().registerEvents(loginEvent, this);
    }

}
