package de.jaunikapauni.axperms.listener;

import de.jaunikapauni.axperms.AxPerms;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.permissions.PermissionAttachment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class PlayerJoinListener implements Listener {
    AxPerms reference;
    public PlayerJoinListener(AxPerms reference){
        this.reference = reference;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e){
        Player p = e.getPlayer();
        UUID uuid = p.getUniqueId();
        try{
            Set<String> groups = reference.getGroupManager().loadPlayerGroupsFromDB(uuid);
            if(groups.isEmpty()){
                String defaultGroup = reference.getGroupManager().getDefaultGroup();
                if(defaultGroup != null){
                    reference.getGroupManager().addPlayer(uuid, defaultGroup);
                }
            }
            reference.reloadPermission(p);
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }
}
