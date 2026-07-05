package de.jaunikapauni.axperms.command;

import de.jaunikapauni.axperms.AxPerms;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;

public class GroupCommand implements CommandExecutor {

    AxPerms reference;
    public GroupCommand(AxPerms reference){
        this.reference = reference;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if(!(sender instanceof Player)){
            return true;
        }
        Player p = (Player) sender;
        if(!p.hasPermission("axperms.group")){
            p.sendMessage("You don't have the permission! [axperms.group]");
            return true;
        }
        if(args.length < 2){
            return false;
        }
        String subCommand = args[0];
        String groupName = args[1];
        switch (subCommand.toLowerCase()){
            case "create":
                reference.getGroupManager().createGroup(groupName);
                p.sendMessage("Group " + groupName + " was created!");
                return true;
            case "delete":
                reference.getGroupManager().deleteGroup(groupName);
                p.sendMessage("Group " + groupName + " was deleted!");
                return true;
            case "addperm":
                if(args.length < 3){
                    return false;
                }
                String permToAdd = args[2];
                reference.getGroupManager().addPermission(groupName, permToAdd);
                p.sendMessage("Permission " + permToAdd + " added to group " + groupName);
                return true;
            case "removeperm":
                if(args.length < 3){
                    return false;
                }
                String permToRemove = args[2];
                reference.getGroupManager().removePermission(groupName, permToRemove);
                p.sendMessage("Permission " + permToRemove + " removed from group " + groupName);
                return true;
            case "addplayer":
                if(args.length < 3){
                    return false;
                }
                String playerNameAdd = args[1];
                String groupAdd = args[2];
                OfflinePlayer targetAdd = Bukkit.getOfflinePlayer(playerNameAdd);
                reference.getGroupManager().addPlayer(targetAdd.getUniqueId(), groupAdd);
                p.sendMessage("Added " + playerNameAdd + " to group " + groupAdd);
                Player onlineAdd = targetAdd.getPlayer();
                if(onlineAdd != null){
                    try{
                        reference.reloadPermission(onlineAdd);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
                return true;
            case "removeplayer":
                if(args.length < 3){
                    return false;
                }
                String playerNameRemove = args[1];
                String groupRemove = args[2];
                OfflinePlayer targetRemove = Bukkit.getOfflinePlayer(playerNameRemove);
                reference.getGroupManager().removePlayer(targetRemove.getUniqueId(), groupRemove);
                p.sendMessage("Removed " + playerNameRemove + " from group " + groupRemove);
                Player onlineRemove = targetRemove.getPlayer();
                if(onlineRemove != null){
                    try {
                        reference.reloadPermission(p);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
                return true;
            default:
                return false;
        }
    }
}
