package vip.ourcraft.mcserverplugins.timedgroup;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GroupPlayer {
    private TimedGroup plugin = TimedGroup.getInstance();
    private Player bukkitPlayer;
    private File playerDataFile;
    private YamlConfiguration playerDataYml;
    private HashMap<String, PlayerOwnedGroup> ownedGroups; // 拥有的组
    private PlayerOwnedGroup currentOwnedGroup; // 当前使用的组

    public GroupPlayer(Player player) {
        this.bukkitPlayer = player;
        this.playerDataFile = new File(plugin.getDataFolder() + File.separator + "playerdata", player.getName().toLowerCase() + ".yml");
        this.playerDataYml = YamlConfiguration.loadConfiguration(playerDataFile);

        load();
    }

    public Player getBukkitPlayer() {
        return bukkitPlayer;
    }

    public boolean save() {
        try {
            playerDataYml.save(playerDataFile);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public void load() {
        PlayerOwnedGroup defaultOwnedGroup = new PlayerOwnedGroup(plugin.getGroupManager().getGroup(plugin.getSettings().getDefaultGroupName()), 0L);
        String currentGroupName = playerDataYml.getString("current_group");

        this.ownedGroups = new HashMap<>();
        // 没有就是默认组
        this.currentOwnedGroup = currentGroupName == null ? defaultOwnedGroup : new PlayerOwnedGroup(plugin.getGroupManager().getGroup(currentGroupName), getOwnedGroupExpiredTime(currentGroupName));

        if (playerDataYml.isConfigurationSection("owned_groups")) {
            for (String groupName : playerDataYml.getConfigurationSection("owned_groups").getKeys(false)) {
                ownedGroups.put(groupName, new PlayerOwnedGroup(plugin.getGroupManager().getGroup(groupName), getOwnedGroupExpiredTime(groupName)));
            }
        }

        // 存默认组到拥有组里
        ownedGroups.put(defaultOwnedGroup.getGroup().getGroupName(), defaultOwnedGroup);
    }

    // 得到自有的组
    public List<PlayerOwnedGroup> getOwnedGroups() {
        return new ArrayList<>(ownedGroups.values());
    }

    // 设置当前自有的组
    public boolean setCurrentOwnedGroup(PlayerOwnedGroup ownedGroup) {
        if (!ownedGroups.containsValue(ownedGroup)) {
            throw new IllegalArgumentException("group not owned!");
        }

        PlayerOwnedGroup oldOwnedGroup = getCurrentOwnedGroup();
        Group group = ownedGroup.getGroup();

        // null或isDefaultGroup() == true 则是默认组
        playerDataYml.set("current_group", (group == null || group.isDefaultGroup()) ? null : group.getGroupName());

        if (save()) {
            load();

            // 此时已被重载
            // 从旧组移除，添加到新组
            return plugin.getVaultPer().playerRemoveGroup(bukkitPlayer, oldOwnedGroup.getGroup().getGroupName())
                    && plugin.getVaultPer().playerAddGroup(bukkitPlayer, getCurrentOwnedGroup().getGroup().getGroupName());
        }

        return false;
    }

    // 拿走自有的组
    public boolean takeGroup(PlayerOwnedGroup ownedGroup) {
        if (ownedGroup == null) {
            throw new IllegalArgumentException("group cannot be null!");
        }

        if (!ownedGroups.containsValue(ownedGroup)) {
            throw new IllegalArgumentException("group not owned!");
        }

        playerDataYml.set("owned_groups." + ownedGroup.getGroup().getGroupName(), null);

        if (save()) {
            load();

            if (ownedGroup.equals(currentOwnedGroup)) {
                return setCurrentOwnedGroup(null);
            }

            return true;
        }

        return false;
    }

    // 给予自有组
    public boolean giveGroup(Group group, long expiredTime) {
        if (group == null) {
            throw new IllegalArgumentException("group cannot be null!");
        }

        // 过期时间必须大于现在的时间
        if (expiredTime != 0L && System.currentTimeMillis() > expiredTime) {
            throw new IllegalArgumentException("expired time must > current time.");
        }

        playerDataYml.set("owned_groups." + group.getGroupName() + ".expired_time", expiredTime);

        if (save()) {
            load();

            return true;
        }

        return false;
    }

    // 得到当前的自有组
    public PlayerOwnedGroup getCurrentOwnedGroup() {
        return currentOwnedGroup;
    }

    // 得到自有组的过期时间
    private long getOwnedGroupExpiredTime(String groupName) {
        return playerDataYml.getLong("owned_groups." + groupName + ".expired_time", -1);
    }

    public PlayerOwnedGroup getOwnedGroup(String name) {
        return ownedGroups.get(name);
    }
}
