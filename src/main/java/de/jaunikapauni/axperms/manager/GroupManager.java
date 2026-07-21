package de.jaunikapauni.axperms.manager;

import de.jaunikapauni.axperms.AxPerms;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class GroupManager {

    AxPerms reference;
    public GroupManager(AxPerms reference){
        this.reference = reference;
    }

    public void createGroup(String name){
        name = name.toLowerCase();
        if(reference.getCacheManager().groupExists(name)){
            return;
        }
        boolean firstGroup = reference.getCacheManager().getDefaultGroup() == null;
        try(Connection conn = reference.getDatabaseManager().getConnection()){
            try(PreparedStatement ps = conn.prepareStatement("INSERT INTO groups (name, is_default) VALUES (?, ?)")){
                ps.setString(1, name);
                ps.setBoolean(2, firstGroup);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        reference.getCacheManager().setGroupPrefix(name, "");
        reference.getCacheManager().setGroupSuffix(name, "");
        if(firstGroup){
            reference.getCacheManager().setDefaultGroup(name);
        }
    }

    public void deleteGroup(String name){
        name = name.toLowerCase();
        try(Connection conn = reference.getDatabaseManager().getConnection()){
            try(PreparedStatement ps = conn.prepareStatement("DELETE FROM groups WHERE name = ?")){
                ps.setString(1, name);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        reference.getCacheManager().removeGroup(name);
    }

    public void addPermission(String group, String permission){
        try(Connection conn = reference.getDatabaseManager().getConnection()){
            try(PreparedStatement ps = conn.prepareStatement("INSERT INTO group_perms(group_name, permission) VALUES (?, ?)")){
                ps.setString(1, group);
                ps.setString(2, permission);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void removePermission(String group, String permission){
        try(Connection conn = reference.getDatabaseManager().getConnection()){
            try(PreparedStatement ps = conn.prepareStatement("DELETE FROM group_perms WHERE group_name = ? AND permission = ?")){
                ps.setString(1, group);
                ps.setString(2, permission);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void addPlayer(UUID uuid, String group){
        try(Connection conn = reference.getDatabaseManager().getConnection()){
            try(PreparedStatement ps = conn.prepareStatement("INSERT INTO player_groups(uuid, group_name) VALUES (?, ?)")){
                ps.setString(1, uuid.toString());
                ps.setString(2, group);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void removePlayer(UUID uuid, String group){
        try(Connection conn = reference.getDatabaseManager().getConnection()){
            try(PreparedStatement ps = conn.prepareStatement("DELETE FROM player_groups WHERE uuid = ? AND group_name = ?")){
                ps.setString(1, uuid.toString());
                ps.setString(2, group);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<String> getGroups(UUID uuid){
        List<String> groups = new ArrayList<>();
        try(Connection conn = reference.getDatabaseManager().getConnection()){
            try(PreparedStatement ps = conn.prepareStatement("SELECT group_name FROM player_groups WHERE uuid = ?")){
                ps.setString(1, uuid.toString());
                ResultSet rs = ps.executeQuery();
                while (rs.next()){
                    groups.add(rs.getString("group_name"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return groups;
    }

    public List<String> getPermissions(String group){
        List<String> permissions = new ArrayList<>();
        try(Connection conn = reference.getDatabaseManager().getConnection()){
            try(PreparedStatement ps = conn.prepareStatement("SELECT permission FROM group_perms WHERE group_name = ?")){
                ps.setString(1, group);
                ResultSet rs = ps.executeQuery();
                while (rs.next()){
                    permissions.add(rs.getString("permission"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return permissions;
    }

    public void addInheritance(String parent, String child){
        try(Connection conn = reference.getDatabaseManager().getConnection()){
            try(PreparedStatement ps = conn.prepareStatement("INSERT INTO group_inheritance(parent_group, child_group) VALUES (?, ?)")){
                ps.setString(1, parent);
                ps.setString(2, child);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void removeInheritance(String parent, String child){
        try(Connection conn = reference.getDatabaseManager().getConnection()){
            try(PreparedStatement ps = conn.prepareStatement("DELETE FROM group_inheritance WHERE parent_group = ? AND child_group = ?")){
                ps.setString(1, parent);
                ps.setString(2, child);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Set<String> getAllInheritedGroups(String group){
        Set<String> result = new HashSet<>();
        resolve(group, result);
        return result;
    }

    public void resolve(String group, Set<String> result){
        if(result.contains(group)){
            return;
        }
        result.add(group);
        try(Connection conn = reference.getDatabaseManager().getConnection()){
            try(PreparedStatement ps = conn.prepareStatement("SELECT parent_group FROM group_inheritance WHERE child_group = ?")){
                ps.setString(1, group);
                try(ResultSet rs = ps.executeQuery()){
                    while (rs.next()){
                        String parent = rs.getString("parent_group");
                        resolve(parent, result);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void setPrefix(String group, String prefix){
        try(Connection conn = reference.getDatabaseManager().getConnection()){
            try(PreparedStatement ps = conn.prepareStatement("UPDATE groups SET prefix = ? WHERE name = ?")){
                ps.setString(1, prefix);
                ps.setString(2, group);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void setSuffix(String group, String suffix){
        try(Connection conn = reference.getDatabaseManager().getConnection()){
            try(PreparedStatement ps = conn.prepareStatement("UPDATE groups SET suffix = ? WHERE name = ?")){
                ps.setString(1, suffix);
                ps.setString(2, group);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public String getPrefix(String group){
        try(Connection conn = reference.getDatabaseManager().getConnection()){
            try(PreparedStatement ps = conn.prepareStatement("SELECT prefix FROM groups WHERE name = ?")){
                ps.setString(1, group);
                ResultSet rs = ps.executeQuery();
                if(rs.next()){
                    return rs.getString("prefix");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return group;
    }

    public String getSuffix(String group){
        try(Connection conn = reference.getDatabaseManager().getConnection()){
            try(PreparedStatement ps = conn.prepareStatement("SELECT suffix FROM groups WHERE name = ?")){
                ps.setString(1, group);
                ResultSet rs = ps.executeQuery();
                if(rs.next()){
                    String suffix = rs.getString("suffix");
                    return suffix == null ? "" : suffix;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return "";
    }

    public String getDefaultGroup(){
        try(Connection conn = reference.getDatabaseManager().getConnection()){
            try(PreparedStatement ps = conn.prepareStatement("SELECT name FROM groups WHERE is_default = TRUE")){
                ResultSet rs = ps.executeQuery();
                if(rs.next()){
                    return rs.getString("name");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public void setDefaultGroup(String group){
        try(Connection conn = reference.getDatabaseManager().getConnection()){
            try(PreparedStatement ps = conn.prepareStatement("UPDATE groups SET is_default = FALSE")){
                ps.executeUpdate();
            }
            try(PreparedStatement ps2 = conn.prepareStatement("UPDATE groups SET is_default = TRUE WHERE name = ?")){
                ps2.setString(1, group.toLowerCase());
                ps2.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void loadAllGroupsIntoCache() throws SQLException {
        try(Connection conn = reference.getDatabaseManager().getConnection()){
            try(PreparedStatement ps = conn.prepareStatement("SELECT name, prefix, suffix, is_default FROM groups")){
                ResultSet rs = ps.executeQuery();
                while (rs.next()){
                    String name = rs.getString("name");
                    String prefix = rs.getString("prefix");
                    String suffix = rs.getString("suffix");
                    boolean isDefault = rs.getBoolean("is_default");
                    reference.getCacheManager().setGroupPrefix(name, prefix == null ? "" : prefix);
                    reference.getCacheManager().setGroupSuffix(name, prefix == null ? "" : suffix);
                    if(isDefault){
                        reference.getCacheManager().setDefaultGroup(name);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        try(Connection conn = reference.getDatabaseManager().getConnection()){
            try(PreparedStatement ps = conn.prepareStatement("SELECT group_name, permission FROM group_perms")){
                ResultSet rs = ps.executeQuery();
                Map<String, Set<String>> groupPerms = new HashMap<>();
                while (rs.next()){
                    String groupName = rs.getString("group_name");
                    String permission = rs.getString("permission");
                    Set<String> perms = groupPerms.computeIfAbsent(groupName, k -> new HashSet<>());
                    perms.add(permission);
                }
                for(Map.Entry<String, Set<String>> entry : groupPerms.entrySet()){
                    reference.getCacheManager().setGroupPermissions(entry.getKey(),entry.getValue());
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        try(Connection conn = reference.getDatabaseManager().getConnection()){
            try(PreparedStatement ps = conn.prepareStatement("SELECT parent_group, child_group FROM group_inheritance")){
                ResultSet rs = ps.executeQuery();
                Map<String, Set<String>> inheritance = new HashMap<>();
                while (rs.next()){
                    String parent = rs.getString("parent_group");
                    String child = rs.getString("child_group");
                    Set<String> parents = inheritance.computeIfAbsent(child, k -> new HashSet<>());
                    parents.add(parent);
                }
                for (Map.Entry<String, Set<String>> entry : inheritance.entrySet()){
                    reference.getCacheManager().setGroupInheritance(entry.getKey(), entry.getValue());
                }
            }
        }
    }
}
