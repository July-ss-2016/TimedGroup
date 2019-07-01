package vip.ourcraft.mcserverplugins.timedgroup;

import java.util.Objects;

public class OwnedGroup {
    private Group group;
    private long expiredTime;

    public OwnedGroup(Group group, long expiredTime) {
        this.group = group;
        this.expiredTime = expiredTime;
    }

    public Group getGroup() {
        return group;
    }

    public long getExpiredTime() {
        return expiredTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OwnedGroup that = (OwnedGroup) o;
        return Objects.equals(group, that.group);
    }

    @Override
    public int hashCode() {
        return Objects.hash(group);
    }
}
