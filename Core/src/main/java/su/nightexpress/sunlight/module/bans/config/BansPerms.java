package su.nightexpress.sunlight.module.bans.config;

import org.bukkit.permissions.Permission;
import su.nightexpress.sunlight.config.PermissionTree;
import su.nightexpress.sunlight.config.Perms;

public class BansPerms {

    public static final PermissionTree ROOT    = Perms.detached("bans");
    public static final PermissionTree COMMAND = ROOT.branch("command");
    public static final PermissionTree BYPASS  = ROOT.branch("bypass");

    public static final Permission COMMAND_ALTS         = COMMAND.permission("alts");
    public static final Permission COMMAND_KICK         = COMMAND.permission("kick");
    public static final Permission COMMAND_BAN          = COMMAND.permission("ban");
    public static final Permission COMMAND_BAN_IP       = COMMAND.permission("banip");
    public static final Permission COMMAND_MUTE         = COMMAND.permission("mute");
    public static final Permission COMMAND_WARN         = COMMAND.permission("warn");
    public static final Permission COMMAND_UNBAN        = COMMAND.permission("unban");
    public static final Permission COMMAND_UNBAN_IP     = COMMAND.permission("unbanip");
    public static final Permission COMMAND_UNMUTE       = COMMAND.permission("unmute");
    public static final Permission COMMAND_UNWARN       = COMMAND.permission("unwarn");
    public static final Permission COMMAND_BAN_HISTORY  = COMMAND.permission("banhistory");
    public static final Permission COMMAND_MUTE_HISTORY = COMMAND.permission("mutehistory");
    public static final Permission COMMAND_WARN_HISTORY = COMMAND.permission("warnhistory");
    public static final Permission COMMAND_BAN_LIST     = COMMAND.permission("banlist");
    public static final Permission COMMAND_MUTE_LIST    = COMMAND.permission("mutelist");
    public static final Permission COMMAND_WARN_LIST    = COMMAND.permission("warnlist");

    public static final Permission ALTS_NOTIFY = ROOT.permission("alts.notify");

    public static final Permission PUNISHMENT_DELETE = ROOT.permission("punishment.delete");
    public static final Permission PUNISHMENT_TOGGLE = ROOT.permission("punishment.toggle");

    public static final Permission BYPASS_DURATION_LIMIT = BYPASS.permission("duration.limit");
    public static final Permission BYPASS_ALT_DETECTION  = BYPASS.permission("alts.detection");
}
