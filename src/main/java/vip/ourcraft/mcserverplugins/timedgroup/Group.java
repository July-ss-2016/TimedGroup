package vip.ourcraft.mcserverplugins.timedgroup;

public class Group {
    private String name;
    private String chineseName;
    private boolean isDefaultGroup;
    private boolean isFlyGroup;

    public Group(String name, String chineseName, boolean isDefaultGroup) {
        this.name = name;
        this.chineseName = chineseName;
        this.isDefaultGroup = isDefaultGroup;
    }

    public Group(String name, String chineseName, boolean isDefaultGroup, boolean isFlyGroup) {
        this.name = name;
        this.chineseName = chineseName;
        this.isDefaultGroup = isDefaultGroup;
        this.isFlyGroup = isFlyGroup;
    }

    public boolean isDefaultGroup() {
        return isDefaultGroup;
    }

    public String getName() {
        return name;
    }

    public String getChineseName() {
        return chineseName;
    }

    public boolean isFlyGroup() {
        return isFlyGroup;
    }
}
