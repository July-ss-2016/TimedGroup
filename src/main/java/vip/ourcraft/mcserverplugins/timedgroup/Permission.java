package vip.ourcraft.mcserverplugins.timedgroup;

public class Permission {
    private String name;
    private String chineseName;

    public Permission(String name, String chineseName) {
        this.name = name;
        this.chineseName = chineseName;
    }

    public String getName() {
        return name;
    }

    public String getChineseName() {
        return chineseName;
    }
}
