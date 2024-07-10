package su.nightexpress.sunlight.module.scoreboard.config;

import su.nightexpress.nightcore.util.wrapper.UniPermission;
import su.nightexpress.sunlight.config.Perms;
import su.nightexpress.sunlight.Placeholders;

public class SBPerms {

    private static final String PREFIX         = Perms.PREFIX + "scoreboard.";
    private static final String PREFIX_COMMAND = PREFIX + "command.";

    public static final UniPermission MODULE  = new UniPermission(PREFIX + Placeholders.WILDCARD);
    public static final UniPermission COMMAND = new UniPermission(PREFIX_COMMAND + Placeholders.WILDCARD);

    public static final UniPermission COMMAND_SCOREBOARD        = new UniPermission(PREFIX_COMMAND + "scoreboard");
    public static final UniPermission COMMAND_SCOREBOARD_OTHERS = new UniPermission(PREFIX_COMMAND + "scoreboard.others");

    static {
        Perms.PLUGIN.addChildren(MODULE);

        MODULE.addChildren(COMMAND);

        COMMAND.addChildren(
            COMMAND_SCOREBOARD,
            COMMAND_SCOREBOARD_OTHERS
        );
    }
}
