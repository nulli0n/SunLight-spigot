package su.nightexpress.sunlight.module.spawns.config;

import org.bukkit.permissions.Permission;
import su.nightexpress.sunlight.config.PermissionTree;
import su.nightexpress.sunlight.config.Perms;

public class SpawnsPerms {

    public static final PermissionTree MODULE  = Perms.detached("spawns");
    public static final PermissionTree COMMAND = MODULE.branch("command");
    public static final PermissionTree BYPASS  = MODULE.branch("bypass");
    public static final PermissionTree SPAWN   = MODULE.branch("spawn");

    public static final Permission COMMAND_SPAWNS_CREATE          = COMMAND.permission("spawns.create");
    public static final Permission COMMAND_SPAWNS_DELETE          = COMMAND.permission("spawns.delete");
    public static final Permission COMMAND_SPAWNS_TELEPORT        = COMMAND.permission("spawns.teleport");
    public static final Permission COMMAND_SPAWNS_TELEPORT_OTHERS = COMMAND.permission("spawns.teleport.others");
    public static final Permission COMMAND_SPAWNS_EDITOR          = COMMAND.permission("spawns.editor");
}
