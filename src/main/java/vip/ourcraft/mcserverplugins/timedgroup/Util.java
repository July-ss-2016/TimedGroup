package vip.ourcraft.mcserverplugins.timedgroup;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

public class Util {
    private static List<String> times = Arrays.asList("y", "m", "d", "s");

    public static void sendMsg(CommandSender cs, String msg) {
        cs.sendMessage("§a[TimedGroup] §d" + ChatColor.translateAlternateColorCodes('&', msg));
    }

    public static void sendMsgWithoutPrefix(CommandSender cs, String msg) {
        cs.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
    }

    public static boolean isPlayer(Object o) {
        return o instanceof Player;
    }

    public static boolean isPlayerOnline(Player player) {
        return player != null && player.isOnline();
    }

    public static int getSecondFromStr(String s) {
        if (s == null) {
            return -1;
        }

        int result = 0;

        for (int i = 0; i < times.size(); i++) {
            if (s.contains(times.get(i))) {
                int start = i == 0 ? 0 : s.indexOf(times.get(i - 1));
                int end = s.indexOf(times.get(i));

                System.out.println(start + "," + end);

                start = start == -1 ? 0 : start;

                //System.out.println(s.substring(start, end));
                //result += Integer.parseInt(s.substring(i == 0 ? 0 : s.indexOf(times.get(i - 1)), s.indexOf(times.get(i))));
            }
        }

        return result;
    }

    public static void main(String[] args) {
        /*
        基本思路：正则表达式搜索[0-9]+[y|m|d|h|m|s]，匹配出所有文本，再分离出文本和数字，再根据文本和数字计算
         */
    }
}
