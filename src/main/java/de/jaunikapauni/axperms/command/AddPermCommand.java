package de.jaunikapauni.axperms.command;

import de.jaunikapauni.axperms.AxPerms;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

public class AddPermCommand implements CommandExecutor {
    AxPerms reference;
    public AddPermCommand(AxPerms reference){
        this.reference = reference;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if(args.length < 2){
            return false;
        }
        if(!(sender instanceof Player)){
            sender.sendMessage("Only players can run this command!");
            return true;
        }
        Player p = (Player) sender;
        if(!p.hasPermission("axperms.addperm")){
            p.sendMessage("You don't have the permission! [axperms.addperm]");
            return true;
        }
        String targetName = args[0];
        OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(targetName);
        UUID uuid = targetPlayer.getUniqueId();
        String permission = args[1];
        try(Connection conn = reference.getDatabaseManager().getConnection()){
            try(PreparedStatement ps = conn.prepareStatement("INSERT INTO perms(uuid, permission) VALUES (?, ?)")){
                ps.setString(1, uuid.toString());
                ps.setString(2, permission);
                ps.executeUpdate();
                sender.sendMessage("Permission " + permission + " was added for " + targetName);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return true;
    }
}
