package de.jaunikapauni.axperms.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CheckCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        Player p = (Player) sender;
        String perm = args[0];
        if(p.hasPermission(perm)){
            p.sendMessage(perm);
        } else {
            p.sendMessage("no");
        }
        return true;
    }
}
