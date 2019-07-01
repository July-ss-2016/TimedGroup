package vip.ourcraft.mcserverplugins.timedgroup;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import vip.ourcraft.mcserverplugins.timedgroup.managers.GroupPlayerManager;

public class ExpiredCleanerTask implements Runnable {
    private GroupPlayerManager groupPlayerManager;

    public ExpiredCleanerTask(TimedGroup plugin) {
        this.groupPlayerManager = plugin.getGroupPlayerManager();
    }

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!player.isOnline()) {
                continue;
            }

            GroupPlayer groupPlayer = groupPlayerManager.getGroupPlayer(player);

            for (OwnedPermission ownedPermission : groupPlayer.getOwnedPermissions()) {
                long expiredTime = ownedPermission.getExpiredTime();

                if (expiredTime != 0 && System.currentTimeMillis() > expiredTime) {
                    groupPlayer.takePermission(ownedPermission);
                    Util.sendMsg(player, "&c您的权限 &e" + ownedPermission.getPermission().getChineseName() + " &c已到期!");
                }
            }

            for (OwnedGroup ownedGroup : groupPlayer.getOwnedGroups()) {
                long expiredTime = ownedGroup.getExpiredTime();

                // 排除永久的
                if (expiredTime != 0 && System.currentTimeMillis() > expiredTime) {
                    if (ownedGroup.getGroup().isFlyGroup()) {
                        player.setFlying(false);
                        player.setAllowFlight(false);
                    }

                    groupPlayer.takeGroup(ownedGroup);
                    Util.sendMsg(player, "&c您的组 &e" + ownedGroup.getGroup().getChineseName() + " &c已到期!");
                }
            }
        }
    }
}
