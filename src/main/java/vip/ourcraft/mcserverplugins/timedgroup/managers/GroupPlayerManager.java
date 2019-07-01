package vip.ourcraft.mcserverplugins.timedgroup.managers;

import org.bukkit.entity.Player;
import vip.ourcraft.mcserverplugins.timedgroup.GroupPlayer;
import vip.ourcraft.mcserverplugins.timedgroup.Util;

import java.util.HashMap;

public class GroupPlayerManager {
    private HashMap<String, GroupPlayer> groupPlayerMap;

    public GroupPlayerManager() {
        this.groupPlayerMap = new HashMap<>();
    }

    public GroupPlayer getGroupPlayer(Player player) {
        if (!Util.isPlayerOnline(player)) {
            throw new IllegalArgumentException("player must be online!");
        }

        String playerName = player.getName();

        if (!groupPlayerMap.containsKey(playerName)) {
            groupPlayerMap.put(playerName, new GroupPlayer(player));
        }

        return groupPlayerMap.get(playerName);
    }

    public void unloadAllGroupPlayers() {
        groupPlayerMap.clear();
    }
}
