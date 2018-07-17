package vip.ourcraft.mcserverplugins.timedgroup;

import java.util.Objects;

public class PlayerOwnedGroup {
    private Group group;
    private long expiredTime;

    public PlayerOwnedGroup(Group group, long expiredTime) {
        this.group = group;
        this.expiredTime = expiredTime;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public long getExpiredTime() {
        return expiredTime;
    }

    public void setExpiredTime(long expiredTime) {
        this.expiredTime = expiredTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlayerOwnedGroup that = (PlayerOwnedGroup) o;
        return Objects.equals(group, that.group);
    }

    @Override
    public int hashCode() {

        return Objects.hash(group);
    }
}
