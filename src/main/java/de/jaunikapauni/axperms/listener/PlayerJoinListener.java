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

public class PlayerJoinListener implements Listener {
    AxPerms reference;
    public PlayerJoinListener(AxPerms reference){
        this.reference = reference;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) throws SQLException {
        Player p = e.getPlayer();
        List<String> permissions = new ArrayList<>();
        try(Connection conn = reference.getDatabaseManager().getConnection()){
            try(PreparedStatement ps = conn.prepareStatement("SELECT permission FROM perms WHERE uuid = ?")){
                ps.setString(1, e.getPlayer().getUniqueId().toString());
                try(ResultSet rs = ps.executeQuery()){
                    while(rs.next()){
                        permissions.add(rs.getString("permission"));
                    }
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }

        if(!permissions.isEmpty()){
            PermissionAttachment attachment = p.addAttachment(reference);
            for(String perm : permissions){
                attachment.setPermission(perm, true);
            }
            p.sendMessage("All permission were loaded!");
        }
        reference.reloadPermission(p);
    }
}
