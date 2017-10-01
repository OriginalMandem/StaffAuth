package originalalex.github.io.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import originalalex.github.io.events.LoginEvent;

public class Login implements CommandExecutor {

    private LoginEvent le;

    public Login(LoginEvent event) {
        this.le = event;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "You must be a player to use this command!");
            return true;
        }
        Player player = (Player) sender;
        if (!player.hasPermission("staffauth.auth")) {
            player.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
            return true;
        }
        if (args.length != 1) {
            player.sendMessage(ChatColor.RED + "Incorrect usage! You must use it like this /login [code sent via email]");
            return true;
        }
        String code = le.getCode(player.getUniqueId());
        if (!args[0].equalsIgnoreCase(code)) {
            player.sendMessage(ChatColor.RED + "Wrong code!");
            return true;
        }
        le.removeUUID(player.getUniqueId());
        player.sendMessage(ChatColor.GREEN + "Thank you for verifying, you can now move around.");
        return true;
    }

}
