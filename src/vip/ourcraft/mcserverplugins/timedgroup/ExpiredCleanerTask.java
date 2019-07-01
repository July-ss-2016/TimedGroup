package vip.ourcraft.mcserverplugins.timedgroup;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import vip.ourcraft.mcserverplugins.timedgroup.managers.GroupPlayerManager;

public class ExpiredGroupCleanerTask implements Runnable {
    public GroupPlayerManager groupPlayerManager;

    public ExpiredGroupCleanerTask(TimedGroup plugin) {
        this.groupPlayerManager = plugin.getGroupPlayerManager();
    }

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!player.isOnline()) {
                continue;
            }

            GroupPlayer groupPlayer = groupPlayerManager.getGroupPlayer(player);

            for (PlayerOwnedGroup playerOwnedGroup : groupPlayer.getOwnedGroups()) {
                long expiredTime = playerOwnedGroup.getExpiredTime();

                // 排除永久的
                if (expiredTime != 0 && System.currentTimeMillis() > expiredTime) {
                    groupPlayer.takeGroup(playerOwnedGroup);
                    Util.sendMsg(player, "&c您的组 &e" + playerOwnedGroup.getGroup().getChineseName() + " &c已到期!");
                }
            }
        }
    }
}
