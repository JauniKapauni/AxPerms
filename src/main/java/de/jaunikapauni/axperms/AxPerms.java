package de.jaunikapauni.axperms;

import de.jaunikapauni.axperms.command.AddPermCommand;
import de.jaunikapauni.axperms.command.CheckCommand;
import de.jaunikapauni.axperms.command.GroupCommand;
import de.jaunikapauni.axperms.command.RemovePermCommand;
import de.jaunikapauni.axperms.listener.PlayerJoinListener;
import de.jaunikapauni.axperms.listener.PlayerQuitListener;
import de.jaunikapauni.axperms.manager.CacheManager;
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

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    Map<UUID, PermissionAttachment> attachments = new HashMap<>();
    GroupManager groupManager;

    public GroupManager getGroupManager() {
        return groupManager;
    }
    CacheManager cacheManager;
    public CacheManager getCacheManager(){
        return cacheManager;
    }

    public Map<UUID, PermissionAttachment> getAttachments() {
        return attachments;
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        saveDefaultConfig();
        databaseManager = new DatabaseManager(this);
        cacheManager = new CacheManager();
        groupManager = new GroupManager(this);
        try {
            groupManager.loadAllGroupsIntoCache();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        try {
            if (!databaseManager.initDatabaseTable1() || !databaseManager.initDatabaseTable2() || !databaseManager.initDatabaseTable3() || !databaseManager.initDatabaseTable4() || !databaseManager.initDatabaseTable5()) {
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
        getServer().getPluginManager().registerEvents(new PlayerQuitListener(this), this);
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
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
                ResultSet rs = ps.executeQuery();
                while (rs.next()){
                    attachment.setPermission(rs.getString("permission"), true);
                }
            }
        }
        Set<String> playerGroups = groupManager.getGroups(uuid);
        for(String group : playerGroups){
            Set<String> allGroups = groupManager.getAllInheritedGroups(group);
            for(String g : allGroups){
                Set<String> perms = groupManager.getPermissions(g);
                for(String permission : perms){
                    attachment.setPermission(permission, true);
                }
            }
        }
    }
}
