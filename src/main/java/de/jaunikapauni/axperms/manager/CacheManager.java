package de.jaunikapauni.axperms.manager;

import java.util.*;

public class CacheManager {
    Map<UUID, Set<String>> playerGroups = new HashMap<>();
    Map<String, Set<String>> groupPermissions = new HashMap<>();
    Map<String, String> groupPrefixes = new HashMap<>();
    Map<String, String> groupSuffixes = new HashMap<>();
    Map<String, Set<String>> groupInheritance = new HashMap<>();
    String defaultGroup = null;

    public void setPlayerGroups(UUID uuid, Set<String> groups){
        playerGroups.put(uuid, new HashSet<>(groups));
    }

    public Set<String> getPlayerGroups(UUID uuid){
        return playerGroups.getOrDefault(uuid, new HashSet<>());
    }

    public void addPlayerGroup(UUID uuid, String group){
        Set<String> groups = playerGroups.computeIfAbsent(uuid, k -> new HashSet<>());
        groups.add(group);
    }

    public void removePlayerGroup(UUID uuid, String group){
        Set<String> groups = playerGroups.get(uuid);
        if(groups != null){
            groups.remove(group);
        }
    }

    public void removePlayer(UUID uuid){
        playerGroups.remove(uuid);
    }

    public void setDefaultGroup(String group){
        this.defaultGroup = group;
    }

    public String getDefaultGroup(){
        return defaultGroup;
    }

    public void setGroupPermissions(String group, Set<String> permissions){
        groupPermissions.put(group, new HashSet<>(permissions));
    }

    public Set<String> getGroupPermissions(String group){
        return groupPermissions.getOrDefault(group, new HashSet<>());
    }

    public void addGroupPermission(String group, String permission){
        Set<String> permissions = groupPermissions.computeIfAbsent(group, k -> new HashSet<>());
        permissions.add(permission);
    }

    public void removeGroupPermission(String group, String permission){
        Set<String> perms = groupPermissions.get(group);
        if(perms != null){
            perms.remove(permission);
        }
    }

    public void setGroupPrefix(String group, String prefix){
        groupPrefixes.put(group, prefix == null ? "" : prefix);
    }

    public String getGroupPrefix(String group){
        return groupPrefixes.getOrDefault(group, group);
    }

    public void setGroupSuffix(String group, String suffix){
        groupSuffixes.put(group, suffix == null ? "" : suffix);
    }

    public String getGroupSuffix(String group){
        return groupSuffixes.getOrDefault(group, "");
    }

    public void setGroupInheritance(String child, Set<String> parents){
        groupInheritance.put(child, new HashSet<>(parents));
    }

    public Set<String> getGroupParents(String child){
        return groupInheritance.getOrDefault(child, new HashSet<>());
    }

    public void addInheritance(String parent, String child){
        Set<String> parents = groupInheritance.computeIfAbsent(child, k -> new HashSet<>());
        parents.add(parent);
    }

    public void removeInheritance(String parent, String child){
        Set<String> parents = groupInheritance.get(child);
        if(parents != null){
            parents.remove(parent);
        }
    }

    public void removeGroup(String group){
        groupPermissions.remove(group);
        groupPrefixes.remove(group);
        groupSuffixes.remove(group);
        groupInheritance.remove(group);
        for(Set<String> parents : groupInheritance.values()){
            parents.remove(group);
        }
    }

    public boolean groupExists(String group){
        group = group.toLowerCase();
        return groupPermissions.containsKey(group) || groupPrefixes.containsKey(group) || groupSuffixes.containsKey(group);
    }

    public void clear(){
        playerGroups.clear();
        groupPermissions.clear();
        groupPrefixes.clear();
        groupSuffixes.clear();
        groupInheritance.clear();
        defaultGroup = null;
    }
}
