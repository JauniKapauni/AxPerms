package de.jaunikapauni.axperms;

import de.jaunikapauni.axperms.command.AddPermCommand;
import de.jaunikapauni.axperms.command.CheckCommand;
import de.jaunikapauni.axperms.command.RemovePermCommand;
import de.jaunikapauni.axperms.listener.PlayerJoinListener;
import de.jaunikapauni.axperms.manager.DatabaseManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class AxPerms extends JavaPlugin {
    DatabaseManager databaseManager;
    public DatabaseManager getDatabaseManager(){
        return databaseManager;
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        saveDefaultConfig();
        databaseManager = new DatabaseManager(this);
        try{
            if(databaseManager.initDatabaseTable1() == false){
                Bukkit.getLogger().severe("Error creating db table!");
                Bukkit.getServer().shutdown();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        getCommand("addperm").setExecutor(new AddPermCommand(this));
        getCommand("removeperm").setExecutor(new RemovePermCommand(this));
        getCommand("check").setExecutor(new CheckCommand());
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
}
