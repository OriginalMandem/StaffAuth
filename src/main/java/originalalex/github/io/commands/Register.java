package originalalex.github.io.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import originalalex.github.io.sql.DatabaseCommunicator;

public class Register implements CommandExecutor {

    private DatabaseCommunicator communicator;

    public Register(DatabaseCommunicator db) {
        this.communicator = db;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] strings) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "You must be a player to use this command!");
            return true;
        }
        Player player = (Player) sender;
        if (!player.hasPermission("staffauth.auth")) {
            player.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
            return true;
        }
        if (strings.length != 1) {
            player.sendMessage(ChatColor.RED + "Incorrect usage! You must use it like this /register [email]");
            return true;
        }
        String email = strings[0];
        String possibleEmail = communicator.getEmail(player.getUniqueId().toString());
        if (possibleEmail == null) {
            communicator.writeEmail(player.getUniqueId().toString(), email);
            sender.sendMessage(ChatColor.GREEN + "Successfully set email to " + email + ".");
        } else {
            if (!sender.hasPermission("staffauth.changeemail")) {
                sender.sendMessage(ChatColor.RED + "You do not have permission to change your email");
            } else {
                communicator.updateEmail(player.getUniqueId().toString(), email);
                sender.sendMessage(ChatColor.GREEN + "You have updated your login email to " + email + ".");
            }
        }
        return true;
    }
}
