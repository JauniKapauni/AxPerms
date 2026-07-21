package de.jaunikapauni.axperms.placeholder;

import de.jaunikapauni.axperms.AxPerms;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;
import java.util.Set;

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
        Set<String> groups = reference.getCacheManager().getPlayerGroups(p.getUniqueId());
        if(groups.isEmpty()){
            return "";
        }
        String group = groups.iterator().next();
        return reference.getCacheManager().getGroupPrefix(group);
    }

    public String getSuffix(OfflinePlayer p){
        Set<String> groups = reference.getCacheManager().getPlayerGroups(p.getUniqueId());
        if(groups.isEmpty()){
            return "";
        }
        String group = groups.iterator().next();
        return reference.getCacheManager().getGroupSuffix(group);
    }

    public String getPrimaryGroup(OfflinePlayer p){
        Set<String> groups = reference.getCacheManager().getPlayerGroups(p.getUniqueId());
        if(groups.isEmpty()){
            return "none";
        }
        String group = groups.iterator().next();
        return group.substring(0, 1).toUpperCase() + group.substring(1);
    }
}
