package vip.ourcraft.mcserverplugins.timedgroup;

import java.util.HashMap;

public class Settings {
    private String defaultGroupName;
    private HashMap<String, Group> groups;

    public String getDefaultGroupName() {
        return defaultGroupName;
    }

    public void setDefaultGroupName(String defaultGroupName) {
        this.defaultGroupName = defaultGroupName;
    }

    public HashMap<String, Group> getGroups() {
        return groups;
    }

    public void setGroups(HashMap<String, Group> groups) {
        this.groups = groups;
    }
}
