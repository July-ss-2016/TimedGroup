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
    private Player player;
    private File playerDataFile;
    private YamlConfiguration playerDataYml;
    private HashMap<String, OwnedGroup> ownedGroups; // 拥有的组
    private OwnedGroup currentOwnedGroup; // 当前使用的组
    private HashMap<String, OwnedPermission> ownedPermissions; // 拥有的权限

    public GroupPlayer(Player player) {
        this.player = player;
        this.playerDataFile = new File(plugin.getDataFolder() + File.separator + "playerdata", player.getName().toLowerCase() + ".yml");
        this.playerDataYml = YamlConfiguration.loadConfiguration(playerDataFile);

        load();
    }

    public Player getPlayer() {
        return player;
    }

    private boolean save() {
        try {
            playerDataYml.save(playerDataFile);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    private void load() {
        OwnedGroup defaultOwnedGroup = new OwnedGroup(plugin.getGroupManager().getGroup(plugin.getSettings().getDefaultGroupName()), 0L);
        String currentGroupName = playerDataYml.getString("current_group");

        this.ownedGroups = new HashMap<>();
        this.ownedPermissions = new HashMap<>();
        // 没有就是默认组
        this.currentOwnedGroup = currentGroupName == null ? defaultOwnedGroup : new OwnedGroup(plugin.getGroupManager().getGroup(currentGroupName), getOwnedGroupExpiredTime(currentGroupName));

        if (playerDataYml.isConfigurationSection("owned_groups")) {
            for (String groupName : playerDataYml.getConfigurationSection("owned_groups").getKeys(false)) {
                ownedGroups.put(groupName, new OwnedGroup(plugin.getGroupManager().getGroup(groupName), getOwnedGroupExpiredTime(groupName)));
            }
        }

        if (playerDataYml.isConfigurationSection("owned_permissions")) {
            for (String permissionName : playerDataYml.getConfigurationSection("owned_permissions").getKeys(false)) {
                ownedPermissions.put(permissionName, new OwnedPermission(plugin.getGroupManager().getPermission(permissionName), getOwnedPermissionExpiredTime(permissionName)));
            }
        }

        // 存默认组到拥有组里
        ownedGroups.put(defaultOwnedGroup.getGroup().getName(), defaultOwnedGroup);
    }

    // 得到自有组Map
    public HashMap<String, OwnedGroup> getOwnedGroupsMap() {
        return ownedGroups;
    }

    // 得到自有组List
    public List<OwnedGroup> getOwnedGroups() {
        return new ArrayList<>(ownedGroups.values());
    }

    // 设置当前自有组
    public boolean setCurrentOwnedGroup(OwnedGroup ownedGroup) {
        if (ownedGroup != null && !ownedGroups.containsValue(ownedGroup)) {
            throw new IllegalArgumentException("group not owned!");
        }

        OwnedGroup oldOwnedGroup = getCurrentOwnedGroup();

        // null或isDefaultGroup() == true 则是默认组
        playerDataYml.set("current_group", (ownedGroup == null || ownedGroup.getGroup().isDefaultGroup()) ? null : ownedGroup.getGroup().getName());

        if (save()) {
            load();

            // 此时已被重载
            // 从Vault旧组移除，添加到Vault新组
            return plugin.getVaultPer().playerRemoveGroup(player, oldOwnedGroup.getGroup().getName())
                    && plugin.getVaultPer().playerAddGroup(player, getCurrentOwnedGroup().getGroup().getName());
        }

        return false;
    }

    // 拿走自有组
    public boolean takeGroup(OwnedGroup ownedGroup) {
        if (ownedGroup == null) {
            throw new IllegalArgumentException("group cannot be null!");
        }

        if (!ownedGroups.containsValue(ownedGroup)) {
            throw new IllegalArgumentException("group not owned!");
        }

        playerDataYml.set("owned_groups." + ownedGroup.getGroup().getName(), null);

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
    public boolean giveGroup(Group group, int second) {
        if (group == null) {
            throw new IllegalArgumentException("group cannot be null!");
        }

        if (second <= 0) {
            throw new IllegalArgumentException("second must > 0");
        }

        playerDataYml.set("owned_groups." + group.getName() + ".expired_time", getOwnedGroupExpiredTime(group.getName()) + second * 1000L);

        if (save()) {
            load();

            return true;
        }

        return false;
    }

    // 得到当前的自有组
    public OwnedGroup getCurrentOwnedGroup() {
        return currentOwnedGroup;
    }

    // 得到自有组的过期时间
    private long getOwnedGroupExpiredTime(String groupName) {
        return playerDataYml.getLong("owned_groups." + groupName + ".expired_time", -1);
    }

    // 得到自有权限的过期时间
    private long getOwnedPermissionExpiredTime(String permissionName) {
        return playerDataYml.getLong("owned_groups." + permissionName + ".expired_time", -1);
    }

    // 得到自有组
    public OwnedGroup getOwnedGroup(String name) {
        return ownedGroups.get(name);
    }

    // 给予权限
    public boolean givePermission(Permission permission, long second) {
        if (permission == null) {
            throw new IllegalArgumentException("permission cannot be null!");
        }

        if (second <= 0) {
            throw new IllegalArgumentException("second must >= 0");
        }

        playerDataYml.set("owned_permissions." + permission.getName() + ".expired_time", getOwnedPermissionExpiredTime(permission.getName()) + second * 1000L);

        if (save()) {
            load();

            plugin.getVaultPer().playerAdd(player, permission.getName()); // Vault给权限
            return true;
        }

        return false;
    }

    // 拿走自有权限
    public boolean takePermission(OwnedPermission ownedPermission) {
        if (ownedPermission == null) {
            throw new IllegalArgumentException("permission cannot be null!");
        }

        if (!ownedPermissions.containsValue(ownedPermission)) {
            throw new IllegalArgumentException("permission not owned!");
        }

        playerDataYml.set("owned_permissions." + ownedPermission.getPermission().getName(), null);

        if (save()) {
            load();
            return true;
        }

        return false;
    }

    // 得到自有权限
    public OwnedPermission getOwnedPermission(String name) {
        return ownedPermissions.get(name);
    }

    // 得到自有权限List
    public List<OwnedPermission> getOwnedPermissions() {
        return new ArrayList<>(ownedPermissions.values());
    }
}
