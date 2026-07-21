package de.jaunikapauni.axperms.listener;

import de.jaunikapauni.axperms.AxPerms;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.permissions.PermissionAttachment;

import java.util.UUID;

public class PlayerQuitListener implements Listener {

    AxPerms reference;
    public PlayerQuitListener(AxPerms reference){
        this.reference = reference;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e){
        Player p = e.getPlayer();
        UUID uuid = p.getUniqueId();
        PermissionAttachment permissionAttachment = reference.getAttachments().remove(uuid);
        if(permissionAttachment != null){
            p.removeAttachment(permissionAttachment);
        }
        reference.getCacheManager().removePlayer(uuid);
    }
}
