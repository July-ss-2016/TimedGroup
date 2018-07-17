package vip.ourcraft.mcserverplugins.timedgroup;

public class Group {
    private String groupName;
    private String chineseName;
    private String perGroupName;
    private boolean isDefaultGroup;

    public Group(String groupName, String chineseName, String perGroupName, boolean isDefaultGroup) {
        this.groupName = groupName;
        this.chineseName = chineseName;
        this.perGroupName = perGroupName;
        this.isDefaultGroup = isDefaultGroup;
    }

    public Group(String groupName, String chineseName, String perGroupName) {
        this.groupName = groupName;
        this.chineseName = chineseName;
        this.perGroupName = perGroupName;
    }

    public boolean isDefaultGroup() {
        return isDefaultGroup;
    }

    public void setDefaultGroup(boolean defaultGroup) {
        isDefaultGroup = defaultGroup;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getChineseName() {
        return chineseName;
    }

    public void setChineseName(String chineseName) {
        this.chineseName = chineseName;
    }

    public String getPerGroupName() {
        return perGroupName;
    }

    public void setPerGroupName(String perGroupName) {
        this.perGroupName = perGroupName;
    }
}
