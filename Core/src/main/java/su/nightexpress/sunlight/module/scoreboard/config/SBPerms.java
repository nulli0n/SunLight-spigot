package su.nightexpress.sunlight.module.scoreboard.config;

import org.bukkit.permissions.Permission;
import su.nightexpress.sunlight.config.PermissionTree;
import su.nightexpress.sunlight.config.Perms;

public class SBPerms {

    public static final PermissionTree MODULE  = Perms.detached("scoreboard");
    public static final PermissionTree COMMAND = MODULE.branch("command");

    public static final Permission COMMAND_SCOREBOARD        = COMMAND.permission("scoreboard");
    public static final Permission COMMAND_SCOREBOARD_OTHERS = COMMAND.permission("scoreboard.others");
}
