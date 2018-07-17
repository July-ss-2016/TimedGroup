package vip.ourcraft.mcserverplugins.timedgroup.managers;

import vip.ourcraft.mcserverplugins.timedgroup.Group;
import vip.ourcraft.mcserverplugins.timedgroup.TimedGroup;

import java.util.HashMap;

public class GroupManager {
    private HashMap<String, Group> groupMap;

    public GroupManager(TimedGroup plugin) {
        this.groupMap = plugin.getSettings().getGroups();
    }

    public Group getGroup(String groupName) {
        return groupMap.get(groupName);
    }
}
