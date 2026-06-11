package de.jaunikapauni.axperms.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CheckCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if(!(sender instanceof Player)){
            sender.sendMessage("Only players can run this command!");
            return true;
        }
        Player p = (Player) sender;
        if(!p.hasPermission("axperms.check")){
            p.sendMessage("You don't have the permission! [axperms.check]");
            return true;
        }
        if(args.length < 1) return false;
        String perm = args[0];
        if(p.hasPermission(perm)){
            p.sendMessage(perm);
        } else {
            p.sendMessage("no");
        }
        return true;
    }
}
