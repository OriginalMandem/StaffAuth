package originalalex.github.io.events;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;
import originalalex.github.io.helper.CodeGenerator;
import originalalex.github.io.helper.CodeSender;
import originalalex.github.io.sql.DatabaseCommunicator;

import java.util.*;
import java.util.logging.Logger;

public class LoginEvent implements Listener {

    private DatabaseCommunicator communicator;
    private Set<UUID> unvalidated;
    private Map<UUID, String> playerCodes;

    private JavaPlugin plugin;

    public LoginEvent(JavaPlugin plugin, DatabaseCommunicator communicator) {
        this.plugin = plugin;
        this.communicator = communicator;
        initializeVariables();
    }

    private void initializeVariables() {
        playerCodes = new HashMap<>();
        unvalidated = new HashSet<>();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        if (player.hasPermission("staffauth.auth")) {
            String email = communicator.getEmail(player.getUniqueId().toString());
            if (email == null) {
                e.setJoinMessage(ChatColor.GREEN + "Hey, if you want your account to be secure, you need to register an email with the command /register [email]" +
                        "\nIt is essential that you use your real email as this it will be used to validate your identity in the future.");
                return;
            }
            e.setJoinMessage(ChatColor.GREEN + "Hey, please login using /login <code> where <code> is provided via the registered email");
            unvalidated.add(player.getUniqueId());

            String uniqueCode = CodeGenerator.generateCode();
            playerCodes.put(player.getUniqueId(), uniqueCode);
            CodeSender.sendCode(plugin, email, uniqueCode);
            for (Player p : Bukkit.getOnlinePlayers()) { // make him invis
                p.hidePlayer(player);
            }
        }
    }

    @EventHandler
    public void onBreakBlock(BlockBreakEvent e) {
        if (unvalidated.contains(e.getPlayer().getUniqueId())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        if (unvalidated.contains(e.getPlayer().getUniqueId())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {
        if (unvalidated.contains(e.getPlayer().getUniqueId())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
        if (unvalidated.contains(e.getPlayer().getUniqueId())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerAttack(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player) {
            Player damager = (Player) e.getDamager();
            if (unvalidated.contains(damager.getUniqueId())) {
                e.setCancelled(true);
            }
        } else if (e.getEntity() instanceof Player) {
            Player attacked = (Player) e.getDamager();
            if (unvalidated.contains(attacked.getUniqueId())) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        if (unvalidated.contains(e.getPlayer().getUniqueId())) {
            e.setCancelled(true);
        }
    }

    public void removeUUID(UUID uuid) {
        unvalidated.remove(uuid);
    }

    //Getter:
    public String getCode(UUID uuid) {
        return playerCodes.get(uuid);
    }

}
