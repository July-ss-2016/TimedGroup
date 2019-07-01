package vip.ourcraft.mcserverplugins.timedgroup;

import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import vip.ourcraft.mcserverplugins.timedgroup.managers.GroupManager;
import vip.ourcraft.mcserverplugins.timedgroup.managers.GroupPlayerManager;

import java.io.File;
import java.util.HashMap;

public class TimedGroup extends JavaPlugin {
    private static TimedGroup instance;
    private Settings settings;
    private GroupManager groupManager;
    private GroupPlayerManager groupPlayerManager;
    private net.milkbowl.vault.permission.Permission vaultPer;

    public void onEnable() {
        instance = this;
        this.settings = new Settings();

        loadConfig();

        this.groupManager = new GroupManager(this);
        this.groupPlayerManager = new GroupPlayerManager();

        if (!setupPermissions()) {
            getLogger().warning("Vault Permission Hook 失败!");
            setEnabled(false);

            return;
        }

        if (!initFiles()) {
            getLogger().warning("文件(夹) 初始化完败!");
            setEnabled(false);

            return;
        }

        getCommand("tg").setExecutor(new PlayerCommand(this));
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> Bukkit.getScheduler().runTask(instance, new ExpiredCleanerTask(this)), 0L, 20L);
        getLogger().info("初始化完毕!");
    }

    private boolean initFiles() {
        File playerDataFolder = new File(getDataFolder(), "playerdata");

        return playerDataFolder.exists() || playerDataFolder.mkdirs();
    }

    public void loadConfig() {
        saveDefaultConfig();
        reloadConfig();

        FileConfiguration config = getConfig();
        HashMap<String, Group> groups = new HashMap<>();
        HashMap<String, vip.ourcraft.mcserverplugins.timedgroup.Permission> permissions = new HashMap<>();

        for (String groupName : config.getConfigurationSection("groups").getKeys(false)) {
            ConfigurationSection groupSection = config.getConfigurationSection("groups." + groupName);
            Group group = new Group(groupName, groupSection.getString("chinese_name"), groupName.equalsIgnoreCase(settings.getDefaultGroupName()), groupSection.getBoolean("is_fly_group"));

            groups.put(groupName, group);
        }

        for (String permissionName : config.getConfigurationSection("permissions").getKeys(false)) {
            ConfigurationSection permissionSection = config.getConfigurationSection("groups." + permissionName);
            vip.ourcraft.mcserverplugins.timedgroup.Permission permission = new vip.ourcraft.mcserverplugins.timedgroup.Permission(permissionName, permissionSection.getString("chinese_name"));

            permissions.put(permissionName, permission);
        }

        settings.setGroups(groups);
        settings.setPermissions(permissions);
        settings.setDefaultGroupName(config.getString("default_group_name"));
    }

    public GroupManager getGroupManager() {
        return groupManager;
    }

    private boolean setupPermissions() {
        RegisteredServiceProvider<net.milkbowl.vault.permission.Permission> permissionProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);

        if (permissionProvider != null) {
            vaultPer = permissionProvider.getProvider();
        }

        return (vaultPer != null);
    }

    public Settings getSettings() {
        return settings;
    }

    public Permission getVaultPer() {
        return vaultPer;
    }

    public static TimedGroup getInstance() {
        return instance;
    }

    public GroupPlayerManager getGroupPlayerManager() {
        return groupPlayerManager;
    }
}
