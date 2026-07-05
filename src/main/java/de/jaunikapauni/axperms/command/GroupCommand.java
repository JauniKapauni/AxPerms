package de.jaunikapauni.axperms.command;

import de.jaunikapauni.axperms.AxPerms;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

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
            default:
                return false;
        }
        return false;
    }
}
