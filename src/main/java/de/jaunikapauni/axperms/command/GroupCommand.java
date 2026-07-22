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
import java.util.Arrays;
import java.util.Locale;

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
        String groupName;
        switch (subCommand.toLowerCase()){
            case "create":
                groupName = args[1].toLowerCase();
                reference.getGroupManager().createGroup(groupName);
                p.sendMessage("Group " + groupName + " was created!");
                return true;
            case "delete":
                groupName = args[1].toLowerCase();
                reference.getGroupManager().deleteGroup(groupName);
                p.sendMessage("Group " + groupName + " was deleted!");
                return true;
            case "addperm":
                if(args.length < 3){
                    return false;
                }
                String permToAdd = args[2];
                String groupNameAddPerm = args[1].toLowerCase();
                if(!reference.getCacheManager().groupExists(groupNameAddPerm)){
                    p.sendMessage("Group " + groupNameAddPerm + " does not exist!");
                    return true;
                }
                reference.getGroupManager().addPermission(groupNameAddPerm, permToAdd);
                p.sendMessage("Permission " + permToAdd + " added to group " + groupNameAddPerm);
                return true;
            case "removeperm":
                if(args.length < 3){
                    return false;
                }
                groupName = args[1].toLowerCase();
                String permToRemove = args[2];
                reference.getGroupManager().removePermission(groupName, permToRemove);
                p.sendMessage("Permission " + permToRemove + " removed from group " + groupName);
                return true;
            case "addplayer":
                if(args.length < 3){
                    return false;
                }
                String playerNameAdd = args[1];
                String groupAdd = args[2].toLowerCase();
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
                String groupRemove = args[2].toLowerCase();
                OfflinePlayer targetRemove = Bukkit.getOfflinePlayer(playerNameRemove);
                reference.getGroupManager().removePlayer(targetRemove.getUniqueId(), groupRemove);
                p.sendMessage("Removed " + playerNameRemove + " from group " + groupRemove);
                Player onlineRemove = targetRemove.getPlayer();
                if(onlineRemove != null){
                    try {
                        reference.reloadPermission(onlineRemove);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
                return true;
            case "addinherit":
                if(args.length < 3){
                    return false;
                }
                String parentAdd = args[1].toLowerCase();
                String childAdd = args[2].toLowerCase();
                if(!reference.getCacheManager().groupExists(parentAdd)){
                    p.sendMessage("Group " + parentAdd + " does not exist!");
                    return true;
                }
                if(!reference.getCacheManager().groupExists(childAdd)){
                    p.sendMessage("Group " + childAdd + " does not exist!");
                    return true;
                }
                reference.getGroupManager().addInheritance(parentAdd, childAdd);
                p.sendMessage("Group " + childAdd + " now inherits " + parentAdd);
                return true;
            case "removeinherit":
                if(args.length < 3){
                    return false;
                }
                String parentRemove = args[1].toLowerCase();
                String childRemove = args[2].toLowerCase();
                reference.getGroupManager().removeInheritance(parentRemove, childRemove);
                p.sendMessage("Inheritance removed: " + childRemove + " -> " + parentRemove);
                return true;
            case "setprefix":
                if(args.length < 3){
                    return false;
                }
                String prefixGroup = args[1].toLowerCase();
                if(!reference.getCacheManager().groupExists(prefixGroup)){
                    p.sendMessage("Group " + prefixGroup + " does not exist!");
                    return true;
                }
                String prefix = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
                reference.getGroupManager().setPrefix(prefixGroup, prefix);
                p.sendMessage("Prefix set for " + prefixGroup);
                return true;
            case "setsuffix":
                if(args.length < 3){
                    return false;
                }
                String suffixGroup = args[1].toLowerCase();
                if(!reference.getCacheManager().groupExists(suffixGroup)){
                    p.sendMessage("Group " + suffixGroup + " does not exist!");
                    return true;
                }
                String suffix = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
                reference.getGroupManager().setSuffix(suffixGroup, suffix);
                p.sendMessage("Suffix set for " + suffixGroup);
                return true;
            case "setdefault":
                String defaultGroup = args[1];
                if(!reference.getCacheManager().groupExists(defaultGroup)){
                    p.sendMessage("Group " + defaultGroup + " does not exist!");
                    return true;
                }
                reference.getGroupManager().setDefaultGroup(defaultGroup);
                p.sendMessage("Default group set to " + defaultGroup);
                return true;
            default:
                return false;
        }
    }
}
