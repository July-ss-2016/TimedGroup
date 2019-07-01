package vip.ourcraft.mcserverplugins.timedgroup;

import java.util.Objects;

public class OwnedPermission {
    private Permission permission;
    private long expiredTime;

    public OwnedPermission(Permission permission, long expiredTime) {
        this.permission = permission;
        this.expiredTime = expiredTime;
    }

    public Permission getPermission() {
        return permission;
    }

    public long getExpiredTime() {
        return expiredTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OwnedPermission that = (OwnedPermission) o;
        return Objects.equals(permission, that.permission);
    }

    @Override
    public int hashCode() {
        return Objects.hash(permission);
    }
}
