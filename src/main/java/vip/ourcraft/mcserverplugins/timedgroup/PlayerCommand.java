package vip.ourcraft.mcserverplugins.timedgroup;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import vip.ourcraft.mcserverplugins.timedgroup.managers.GroupManager;
import vip.ourcraft.mcserverplugins.timedgroup.managers.GroupPlayerManager;

import java.text.SimpleDateFormat;
import java.util.List;

public class PlayerCommand implements CommandExecutor {
    private TimedGroup plugin;
    private SimpleDateFormat SDF = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    private GroupManager groupManager;
    private GroupPlayerManager groupPlayerManager;

    public PlayerCommand(TimedGroup plugin) {
        this.plugin = plugin;
        this.groupManager = plugin.getGroupManager();
        this.groupPlayerManager = plugin.getGroupPlayerManager();
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        boolean isAdmin = cs.hasPermission("TimedGroup.admin");

        // 个人信息
        if (args.length == 1 && args[0].equalsIgnoreCase("info")) {
            if (!Util.isPlayer(cs)) {
                cs.sendMessage("命令执行者必须是玩家!");
                return true;
            }

            Player player = (Player) cs;

            Util.sendMsgWithoutPrefix(player, getGroupMessages(groupPlayerManager.getGroupPlayer(player)));
            return true;
        }

        // 切换组
        if (args.length == 2 && args[0].equalsIgnoreCase("set")) {
            int index;

            if (!Util.isPlayer(cs)) {
                cs.sendMessage("命令执行者必须是玩家!");
                return true;
            }

            try {
                index = Integer.parseInt(args[1]);
            } catch (Exception e) {
                Util.sendMsg(cs, "&c序号必须是合法数字!");
                return true;
            }

            Player player = (Player) cs;
            GroupPlayer groupPlayer = groupPlayerManager.getGroupPlayer(player);
            List<OwnedGroup> ownedGroups = groupPlayer.getOwnedGroups();

            if (index > ownedGroups.size() || index <= 0) {
                Util.sendMsg(cs, "&c您有 &e" + ownedGroups.size() + "个 &c组, 而您输入的序号是 &e" + index + "&c, 输入 &e/tg info &c来查看拥有的称号!");
                return true;
            }

            // 新组(自有的)
            OwnedGroup newCurrentOwnedGroup = ownedGroups.get(index - 1);

            Util.sendMsg(cs, groupPlayer.setCurrentOwnedGroup(newCurrentOwnedGroup) ? "&d成功将组切换至: &e" + newCurrentOwnedGroup.getGroup().getChineseName() + "&d." : "&c切换组失败, 请联系管理员!");
            return true;
        }

        // 玩家的命令execute已经到此结束了，下面的是管理员指令了
        if (!isAdmin) {
            // 帮助信息
            Util.sendMsg(cs, "&c/tg info &b- &c查看拥有的用户组");
            Util.sendMsg(cs, "&c/tg set <序号> &b- &c切换用户组");
            return true;
        }

        Player player = null;
        GroupPlayer groupPlayer = null;

        if (args.length >= 2) {
            player = Bukkit.getPlayer(args[1]);
            groupPlayer = groupPlayerManager.getGroupPlayer(player);

            if (!Util.isPlayerOnline(player)) {
                cs.sendMessage(args[1] + " 玩家不在线.");
                return true;
            }
        }


        // 重载
        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            plugin.loadConfig();
            groupPlayerManager.unloadAllGroupPlayers(); // 同时重载PrefixPlayers
            cs.sendMessage("ok.");
            return true;
        }

        // 查看玩家的组
        if (args.length == 2 && args[0].equalsIgnoreCase("look")) {
            Util.sendMsgWithoutPrefix(cs, "\n" + player.getName() + "的组:\n" + getGroupMessages(groupPlayer));
            return true;
        }

        // 给予组或续费组
        if (args.length == 4 && args[0].equalsIgnoreCase("ggive")) {
            Group group = groupManager.getGroup(args[2]);
            int day;

            if (!Util.isPlayerOnline(player)) {
                cs.sendMessage(args[1] + " 玩家不在线.");
                return true;
            }

            if (group == null) {
                cs.sendMessage(args[2] + " 组不存在.");
                return true;
            }

            try {
                day = Integer.parseInt(args[3]);
            } catch (Exception e) {
                cs.sendMessage(args[3] + " 天数不合法.");
                return true;
            }

            OwnedGroup ownedGroup = groupPlayer.getOwnedGroup(group.getName());
            long expiredTime, tmp;
            boolean result;

            expiredTime = day == 0 ? 0 : ownedGroup == null ? System.currentTimeMillis() + day * 86400000L
                    : (tmp = ownedGroup.getExpiredTime()) == 0 ? System.currentTimeMillis() + day * 86400000L : tmp + day * 86400000L;

            // 创建or续费

//            if ((result = groupPlayer.giveGroup(group, expiredTime) && groupPlayer.setCurrentOwnedGroup(groupPlayer.getOwnedGroup(group.getName())))) {
//                Util.sendMsg(player, "&d恭喜您获得组: &e" + group.getChineseName() + "&d, 有效期至 &e" + (expiredTime == 0 ? "永久" : SDF.format(expiredTime)) + "&d.");
//                // 切换到新组
//                groupPlayer.setCurrentOwnedGroup(groupPlayer.getOwnedGroup(group.getName()));
//            }

//            Util.sendMsg(cs, "give_group: player = " + player.getName() + ", group = " + group.getName() + ", day = " + day + ", result = " + (result ? "success" : "failed"));
            return true;
        }

        // 拿走组
        if (args.length == 3 && args[0].equalsIgnoreCase("gtake")) {
            if (!Util.isPlayerOnline(player)) {
                cs.sendMessage(args[1] + " 玩家不在线.");
                return true;
            }

            if (!groupPlayer.getOwnedGroupsMap().containsKey(args[2])) {
                cs.sendMessage("组不存在!");
                return true;
            }

            OwnedGroup ownedGroup = groupPlayer.getOwnedGroupsMap().get(args[2]);

            if (ownedGroup.getGroup().isDefaultGroup()) {
                cs.sendMessage("你不能拿走默认组!");
                return true;
            }

            Util.sendMsg(cs, "take_group: player = " + player.getName() + ", group = " + ownedGroup.getGroup().getName() + ", result = " + (groupPlayer.takeGroup(ownedGroup) ? "success" : "failed"));
            return true;
        }

        // 给予权限
        if (args.length == 4 && args[0].equalsIgnoreCase("pgive")) {
            if (!Util.isPlayerOnline(player)) {
                cs.sendMessage(args[1] + " 玩家不在线.");
                return true;
            }

            Permission permission = groupManager.getPermission(args[2]);

            if (permission == null) {
                cs.sendMessage(args[2] + " 权限不存在.");
                return true;
            }

            OwnedPermission ownedPermission = groupPlayer.getOwnedPermission(permission.getName());
            long expiredTime, tmp;
            boolean result;
            int day;

            try {
                day = Integer.parseInt(args[3]);
            } catch (Exception e) {
                cs.sendMessage(args[3] + " 天数不合法.");
                return true;
            }

            expiredTime = day == 0 ? 0 : ownedPermission == null ? System.currentTimeMillis() + day * 86400000L
                    : (tmp = ownedPermission.getExpiredTime()) == 0 ? System.currentTimeMillis() + day * 86400000L : tmp + day * 86400000L;

            if (groupPlayer.givePermission(permission, expiredTime)) {

            }
        }


        cs.sendMessage("/tg look <id>");
        cs.sendMessage("/tg ggive <id> <group> <day[>=0,0=forever]>");
        cs.sendMessage("/tg gtake <id> <group>");
        cs.sendMessage("/tg pgive <id> <permission> <day[>=0,0=forever]>");
        cs.sendMessage("/tg ptake <id> <permission>");
        return true;
    }

    private String getGroupMessages(GroupPlayer groupPlayer) {
        OwnedGroup currentOwnedGroup = groupPlayer.getCurrentOwnedGroup();
        StringBuilder msg = new StringBuilder();
        int counter = 0;

        msg.append("&7======== &c用户组 &7========");
        msg.append("\n");

        for (OwnedGroup playerOwnedGroup : groupPlayer.getOwnedGroups()) {
            long expiredTime = playerOwnedGroup.getExpiredTime();

            msg.append(playerOwnedGroup.equals(currentOwnedGroup) ? "&d&l" : "&f").append(counter + 1).append(".").append(" &f").append(playerOwnedGroup.getGroup().getChineseName()).append(" &a-> &f").append(expiredTime == 0 ? "永不过期" : SDF.format(expiredTime) + " 到期");
            msg.append("\n");
            ++ counter;
        }

        return msg.toString();
    }
}
