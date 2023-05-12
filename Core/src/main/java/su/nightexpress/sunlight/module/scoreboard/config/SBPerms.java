package su.nightexpress.sunlight.module.scoreboard.config;

import su.nexmedia.engine.api.server.JPermission;
import su.nightexpress.sunlight.Perms;
import su.nightexpress.sunlight.Placeholders;

public class SBPerms {

    private static final String PREFIX = Perms.PREFIX + "scoreboard.";
    private static final String PREFIX_COMMAND = PREFIX + "command.";

    public static final JPermission MODULE = new JPermission(PREFIX + Placeholders.WILDCARD, "Access to all Scoreboard module functions.");
    public static final JPermission COMMAND = new JPermission(PREFIX_COMMAND + Placeholders.WILDCARD, "Access to all Scoreboard module commands.");

    public static final JPermission COMMAND_SCOREBOARD = new JPermission(PREFIX_COMMAND + "scoreboard");
    public static final JPermission COMMAND_SCOREBOARD_OTHERS = new JPermission(PREFIX_COMMAND + "scoreboard.others");

    static {
        Perms.PLUGIN.addChildren(MODULE);

        MODULE.addChildren(COMMAND);

        COMMAND.addChildren(COMMAND_SCOREBOARD, COMMAND_SCOREBOARD_OTHERS);
    }
}
