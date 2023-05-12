package su.nightexpress.sunlight.module.bans.util;

import su.nexmedia.engine.api.server.JPermission;
import su.nightexpress.sunlight.Perms;
import su.nightexpress.sunlight.module.bans.command.KickCommand;
import su.nightexpress.sunlight.module.bans.command.ban.BanCommand;
import su.nightexpress.sunlight.module.bans.command.ban.BanipCommand;
import su.nightexpress.sunlight.module.bans.command.ban.UnbanCommand;
import su.nightexpress.sunlight.module.bans.command.history.BanHistoryCommand;
import su.nightexpress.sunlight.module.bans.command.history.MuteHistoryCommand;
import su.nightexpress.sunlight.module.bans.command.history.WarnHistoryCommand;
import su.nightexpress.sunlight.module.bans.command.list.BanListCommand;
import su.nightexpress.sunlight.module.bans.command.list.MuteListCommand;
import su.nightexpress.sunlight.module.bans.command.list.WarnListCommand;
import su.nightexpress.sunlight.module.bans.command.mute.MuteCommand;
import su.nightexpress.sunlight.module.bans.command.mute.UnmuteCommand;
import su.nightexpress.sunlight.module.bans.command.warn.UnwarnCommand;
import su.nightexpress.sunlight.module.bans.command.warn.WarnCommand;

public class BansPerms {

    private static final String PREFIX         = Perms.PREFIX + "bans.";
    private static final String PREFIX_COMMAND = PREFIX + "command.";
    private static final String PREFIX_BYPASS  = PREFIX + "bypass.";
    private static final String PREFIX_HISTORY = PREFIX + "history.";

    public static final JPermission MODULE  = new JPermission(PREFIX + Placeholders.WILDCARD, "Access to all Bans module functions.");
    public static final JPermission COMMAND = new JPermission(PREFIX_COMMAND + Placeholders.WILDCARD, "Access to all Bans commands.");
    public static final JPermission BYPASS  = new JPermission(PREFIX_BYPASS + Placeholders.WILDCARD, "Bypasses all Bans module restrictions.");
    public static final JPermission HISTORY = new JPermission(PREFIX_HISTORY + Placeholders.WILDCARD, "Access to all Bans history management functions.");

    public static final JPermission COMMAND_BAN                = new JPermission(PREFIX_COMMAND + BanCommand.NAME);
    public static final JPermission COMMAND_BANIP              = new JPermission(PREFIX_COMMAND + BanipCommand.NAME);
    public static final JPermission COMMAND_KICK               = new JPermission(PREFIX_COMMAND + KickCommand.NAME);
    public static final JPermission COMMAND_MUTE               = new JPermission(PREFIX_COMMAND + MuteCommand.NAME);
    public static final JPermission COMMAND_UNBAN              = new JPermission(PREFIX_COMMAND + UnbanCommand.NAME);
    public static final JPermission COMMAND_UNMUTE             = new JPermission(PREFIX_COMMAND + UnmuteCommand.NAME);
    public static final JPermission COMMAND_UNWARN             = new JPermission(PREFIX_COMMAND + UnwarnCommand.NAME);
    public static final JPermission COMMAND_WARN               = new JPermission(PREFIX_COMMAND + WarnCommand.NAME);
    public static final JPermission COMMAND_MUTEHISTORY        = new JPermission(PREFIX_COMMAND + MuteHistoryCommand.NAME);
    public static final JPermission COMMAND_BANHISTORY         = new JPermission(PREFIX_COMMAND + BanHistoryCommand.NAME);
    public static final JPermission COMMAND_WARNHISTORY        = new JPermission(PREFIX_COMMAND + WarnHistoryCommand.NAME);
    public static final JPermission COMMAND_BANLIST            = new JPermission(PREFIX_COMMAND + BanListCommand.NAME);
    public static final JPermission COMMAND_MUTELIST           = new JPermission(PREFIX_COMMAND + MuteListCommand.NAME);
    public static final JPermission COMMAND_WARNLIST           = new JPermission(PREFIX_COMMAND + WarnListCommand.NAME);

    public static final JPermission HISTORY_REMOVE         = new JPermission(PREFIX_HISTORY + "remove");
    public static final JPermission HISTORY_EXPIRE         = new JPermission(PREFIX_HISTORY + "expire");

    public static final JPermission BYPASS_DURATION_LIMIT = new JPermission(PREFIX_BYPASS + "duration.limit");

    static {
        Perms.PLUGIN.addChildren(MODULE);

        MODULE.addChildren(COMMAND, BYPASS, HISTORY);

        COMMAND.addChildren(COMMAND_BAN, COMMAND_BANIP, COMMAND_KICK, COMMAND_MUTE, COMMAND_UNBAN, COMMAND_UNMUTE, COMMAND_UNWARN,
            COMMAND_WARN, COMMAND_MUTEHISTORY, COMMAND_BANHISTORY,
            COMMAND_WARNHISTORY, COMMAND_BANLIST, COMMAND_MUTELIST, COMMAND_WARNLIST);

        HISTORY.addChildren(HISTORY_EXPIRE, HISTORY_REMOVE);

        BYPASS.addChildren(BYPASS_DURATION_LIMIT);
    }
}
