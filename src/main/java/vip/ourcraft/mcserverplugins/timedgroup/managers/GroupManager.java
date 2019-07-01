package vip.ourcraft.mcserverplugins.timedgroup.managers;

import vip.ourcraft.mcserverplugins.timedgroup.Group;
import vip.ourcraft.mcserverplugins.timedgroup.Permission;
import vip.ourcraft.mcserverplugins.timedgroup.TimedGroup;

import java.util.HashMap;
import java.util.Map;

public class GroupManager {
    private HashMap<String, Group> groups;
    private HashMap<String, Permission> permissions;

    public GroupManager(TimedGroup plugin) {
        this.groups = new HashMap<>();
        this.permissions = new HashMap<>();

        for (Map.Entry<String, Group> entry : plugin.getSettings().getGroups().entrySet()) {
            groups.put(entry.getKey().toLowerCase(), entry.getValue());
        }

        for (Map.Entry<String, Permission> entry : plugin.getSettings().getPermissions().entrySet()) {
            permissions.put(entry.getKey().toLowerCase(), entry.getValue());
        }
    }

    public Permission getPermission(String permissionName) {
        return permissions.get(permissionName.toLowerCase());
    }

    public Group getGroup(String groupName) {
        return groups.get(groupName.toLowerCase());
    }
}
