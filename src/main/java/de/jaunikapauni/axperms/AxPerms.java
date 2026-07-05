package de.jaunikapauni.axperms;

import de.jaunikapauni.axperms.command.AddPermCommand;
import de.jaunikapauni.axperms.command.CheckCommand;
import de.jaunikapauni.axperms.command.GroupCommand;
import de.jaunikapauni.axperms.command.RemovePermCommand;
import de.jaunikapauni.axperms.listener.PlayerJoinListener;
import de.jaunikapauni.axperms.manager.DatabaseManager;
import de.jaunikapauni.axperms.manager.GroupManager;
import de.jaunikapauni.axperms.placeholder.AxPermsPlaceholder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

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
            if(databaseManager.initDatabaseTable1() && databaseManager.initDatabaseTable2() && databaseManager.initDatabaseTable3() && databaseManager.initDatabaseTable4() && databaseManager.initDatabaseTable5() == false){
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
        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null){
            new AxPermsPlaceholder(this).register();
            getLogger().info("Successfully registered " + getName() + " placeholders!");
        }
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
        UUID uuid = p.getUniqueId();
        try(Connection conn = getDatabaseManager().getConnection()){
            try(PreparedStatement ps = conn.prepareStatement("SELECT permission FROM perms WHERE uuid = ?")){
                ps.setString(1, uuid.toString());
                try(ResultSet rs = ps.executeQuery()){
                    while (rs.next()){
                        attachment.setPermission(rs.getString("permission"), true);
                    }
                    try(PreparedStatement ps1 = conn.prepareStatement("SELECT group_name FROM player_groups WHERE uuid = ?")){
                        ps1.setString(1, uuid.toString());
                        try(ResultSet rs1 = ps1.executeQuery()){
                            while (rs1.next()){
                                String group = rs1.getString("group_name");
                                Set<String> allGroups = new HashSet<>();
                                allGroups = getGroupManager().getAllInheritedGroups(group);
                                for(String g : allGroups){
                                    try(PreparedStatement ps2 = conn.prepareStatement("SELECT permission FROM group_perms WHERE group_name = ?")){
                                        ps2.setString(1, g);
                                        ResultSet rs2 = ps2.executeQuery();
                                        while (rs2.next()){
                                            attachment.setPermission(rs2.getString("permission"), true);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
