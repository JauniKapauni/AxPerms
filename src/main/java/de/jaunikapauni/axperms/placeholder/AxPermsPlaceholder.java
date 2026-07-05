package de.jaunikapauni.axperms.placeholder;

import de.jaunikapauni.axperms.AxPerms;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class AxPermsPlaceholder extends PlaceholderExpansion {

    AxPerms reference;
    public AxPermsPlaceholder(AxPerms reference){
        this.reference = reference;
    }
    @Override
    public @NotNull String getIdentifier() {
        return reference.getName();
    }

    @Override
    public @NotNull String getAuthor() {
        return String.join("", reference.getDescription().getAuthors());
    }

    @Override
    public @NotNull String getVersion() {
        return reference.getDescription().getVersion();
    }

    @Override
    public String onRequest(OfflinePlayer p, @NotNull String params){
        if(p == null){
            return "";
        }
        switch (params.toLowerCase()){
            case "prefix":
                return getPrefix(p);
            case "suffix":
                return getSuffix(p);
            case "primary_group":
                return getPrimaryGroup(p);
            default:
                return null;
        }
    }

    public String getPrefix(OfflinePlayer p){
        try{
            List<String> groups = reference.getGroupManager().getGroups(p.getUniqueId());
            if(groups.isEmpty()){
                return "";
            }
            String group = groups.get(0);
            return reference.getGroupManager().getPrefix(group);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String getSuffix(OfflinePlayer p){
        try{
            List<String> groups = reference.getGroupManager().getGroups(p.getUniqueId());
            if(groups.isEmpty()){
                return "";
            }
            String group = groups.get(0);
            return reference.getGroupManager().getSuffix(group);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String getPrimaryGroup(OfflinePlayer p){
        try{
            List<String> groups = reference.getGroupManager().getGroups(p.getUniqueId());
            if(groups.isEmpty()){
                return "default";
            }
            return groups.get(0);
        } catch (Exception e) {
            return "default";
        }
    }
}
