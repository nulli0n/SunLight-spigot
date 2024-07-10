package su.nightexpress.sunlight.module.bans.config;

import su.nightexpress.nightcore.util.wrapper.UniPermission;
import su.nightexpress.sunlight.config.Perms;
import su.nightexpress.sunlight.module.bans.util.Placeholders;

public class BansPerms {

    private static final String PREFIX         = Perms.PREFIX + "bans.";
    private static final String PREFIX_COMMAND = PREFIX + "command.";
    private static final String PREFIX_BYPASS  = PREFIX + "bypass.";

    public static final UniPermission MODULE  = new UniPermission(PREFIX + Placeholders.WILDCARD);
    public static final UniPermission COMMAND = new UniPermission(PREFIX_COMMAND + Placeholders.WILDCARD);
    public static final UniPermission BYPASS  = new UniPermission(PREFIX_BYPASS + Placeholders.WILDCARD);

    public static final UniPermission COMMAND_KICK           = new UniPermission(PREFIX_COMMAND + "kick");
    public static final UniPermission COMMAND_BAN            = new UniPermission(PREFIX_COMMAND + "ban");
    public static final UniPermission COMMAND_BAN_IP         = new UniPermission(PREFIX_COMMAND + "banip");
    public static final UniPermission COMMAND_MUTE           = new UniPermission(PREFIX_COMMAND + "mute");
    public static final UniPermission COMMAND_WARN           = new UniPermission(PREFIX_COMMAND + "warn");
    public static final UniPermission COMMAND_UNBAN          = new UniPermission(PREFIX_COMMAND + "unban");
    public static final UniPermission COMMAND_UNMUTE         = new UniPermission(PREFIX_COMMAND + "unmute");
    public static final UniPermission COMMAND_UNWARN         = new UniPermission(PREFIX_COMMAND + "unwarn");
    public static final UniPermission COMMAND_BAN_HISTORY    = new UniPermission(PREFIX_COMMAND + "banhistory");
    public static final UniPermission COMMAND_MUTE_HISTORY   = new UniPermission(PREFIX_COMMAND + "mutehistory");
    public static final UniPermission COMMAND_WARN_HISTORY   = new UniPermission(PREFIX_COMMAND + "warnhistory");
    public static final UniPermission COMMAND_BAN_LIST       = new UniPermission(PREFIX_COMMAND + "banlist");
    public static final UniPermission COMMAND_MUTE_LIST      = new UniPermission(PREFIX_COMMAND + "mutelist");
    public static final UniPermission COMMAND_WARN_LIST      = new UniPermission(PREFIX_COMMAND + "warnlist");

    public static final UniPermission PUNISHMENT_DELETE = new UniPermission(PREFIX + "punishment.remove");
    public static final UniPermission PUNISHMENT_EXPIRE = new UniPermission(PREFIX + "punishment.expire");

    public static final UniPermission BYPASS_DURATION_LIMIT = new UniPermission(PREFIX_BYPASS + "duration.limit");

    static {
        Perms.PLUGIN.addChildren(MODULE);

        MODULE.addChildren(
            COMMAND,
            BYPASS,
            PUNISHMENT_EXPIRE,
            PUNISHMENT_DELETE
        );

        COMMAND.addChildren(
            COMMAND_KICK,
            COMMAND_BAN,
            COMMAND_BAN_IP,
            COMMAND_MUTE,
            COMMAND_WARN,
            COMMAND_UNBAN,
            COMMAND_UNMUTE,
            COMMAND_UNWARN,
            COMMAND_BAN_HISTORY,
            COMMAND_MUTE_HISTORY,
            COMMAND_WARN_HISTORY,
            COMMAND_BAN_LIST,
            COMMAND_MUTE_LIST,
            COMMAND_WARN_LIST
        );

        BYPASS.addChildren(
            BYPASS_DURATION_LIMIT
        );
    }
}
