package su.nightexpress.sunlight.module.warmups.config;

import org.bukkit.permissions.Permission;
import su.nightexpress.sunlight.config.PermissionTree;
import su.nightexpress.sunlight.config.Perms;

public class WarmupsPerms {

    public static final PermissionTree MODULE = Perms.detached("warmups");
    public static final PermissionTree BYPASS = MODULE.branch("bypass");

    public static final Permission BYPASS_TELEPORT = BYPASS.permission("teleport");
    public static final Permission BYPASS_COMMAND  = BYPASS.permission("command");
}
