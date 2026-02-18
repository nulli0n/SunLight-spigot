package su.nightexpress.sunlight.module.vanish.config;

import org.bukkit.permissions.Permission;
import su.nightexpress.sunlight.config.PermissionTree;
import su.nightexpress.sunlight.config.Perms;

public class VanishPerms {

    public static final PermissionTree MODULE  = Perms.detached("vanish");
    public static final PermissionTree COMMAND = MODULE.branch("command");
    public static final PermissionTree BYPASS  = MODULE.branch("bypass");

    public static final Permission COMMAND_VANISH        = COMMAND.permission("vanish");
    public static final Permission COMMAND_VANISH_OTHERS = COMMAND.permission("vanish.others");

    public static final Permission BYPASS_SEE = BYPASS.permission("see");
}
