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
        try(Connection conn = reference.getDatabaseManager().getConnection()){
            try(PreparedStatement ps = conn.prepareStatement("INSERT INTO groups(name) VALUES (?)")){
                ps.setString(1, name);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteGroup(String name){
        try(Connection conn = reference.getDatabaseManager().getConnection()){
            try(PreparedStatement ps = conn.prepareStatement("DELETE FROM groups WHERE name = ?")){
                ps.setString(1, name);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
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
}
