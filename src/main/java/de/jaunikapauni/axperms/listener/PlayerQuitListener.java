package de.jaunikapauni.axperms.listener;

import de.jaunikapauni.axperms.AxPerms;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.permissions.PermissionAttachment;

public class PlayerQuitListener implements Listener {

    AxPerms reference;
    public PlayerQuitListener(AxPerms reference){
        this.reference = reference;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e){
        Player p = e.getPlayer();
        PermissionAttachment attachment = reference.getAttachments().remove(p.getUniqueId());
        if(attachment != null){
            p.removeAttachment(attachment);
        }
    }
}
