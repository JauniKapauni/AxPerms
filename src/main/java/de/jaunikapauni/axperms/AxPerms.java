package de.jaunikapauni.axperms;

import de.jaunikapauni.axperms.command.AddPermCommand;
import de.jaunikapauni.axperms.command.CheckCommand;
import de.jaunikapauni.axperms.command.GroupCommand;
import de.jaunikapauni.axperms.command.RemovePermCommand;
import de.jaunikapauni.axperms.listener.PlayerJoinListener;
import de.jaunikapauni.axperms.manager.DatabaseManager;
import de.jaunikapauni.axperms.manager.GroupManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class AxPerms extends JavaPlugin {
    DatabaseManager databaseManager;
    public DatabaseManager getDatabaseManager(){
        return databaseManager;
    }
    Map<UUID, PermissionAttachment> attachments = new HashMap<>();
    GroupManager groupManager;
    public GroupManager getGroupManager(){
        return groupManager;
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        saveDefaultConfig();
        databaseManager = new DatabaseManager(this);
        groupManager = new GroupManager(this);
        try{
            if(databaseManager.initDatabaseTable1() && databaseManager.initDatabaseTable2() && databaseManager.initDatabaseTable3() && databaseManager.initDatabaseTable4() == false){
                Bukkit.getLogger().severe("Error creating db table!");
                Bukkit.getServer().shutdown();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        getCommand("addperm").setExecutor(new AddPermCommand(this));
        getCommand("removeperm").setExecutor(new RemovePermCommand(this));
        getCommand("check").setExecutor(new CheckCommand());
        getCommand("group").setExecutor(new GroupCommand(this));
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);
        getLogger().info("");
        getLogger().info("----------------------------------------");
        getLogger().info("Name: " + getName());
        getLogger().info("Version: " + getDescription().getVersion());
        getLogger().info(String.join("Authors: " + ", ", getDescription().getAuthors()));
        getLogger().info("----------------------------------------");
        getLogger().info("");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        databaseManager.close();
    }

    public void reloadPermission(Player p) throws SQLException {
        PermissionAttachment old = attachments.remove(p.getUniqueId());
        if(old != null){
            p.removeAttachment(old);
        }
        PermissionAttachment attachment = p.addAttachment(this);
        attachments.put(p.getUniqueId(), attachment);
        try(Connection conn = getDatabaseManager().getConnection()){
            try(PreparedStatement ps = conn.prepareStatement("SELECT permission FROM perms WHERE uuid = ?")){
                ps.setString(1, p.getUniqueId().toString());
                ResultSet rs = ps.executeQuery();
                while(rs.next()){
                    attachment.setPermission(rs.getString("permission"), true);
                }
            }
        }
    }
}
