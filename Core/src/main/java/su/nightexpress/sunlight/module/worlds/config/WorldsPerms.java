package su.nightexpress.sunlight.module.worlds.config;

import org.bukkit.permissions.Permission;
import su.nightexpress.sunlight.config.PermissionTree;
import su.nightexpress.sunlight.config.Perms;

public class WorldsPerms {

    public static final PermissionTree MODULE  = Perms.detached("worlds");
    public static final PermissionTree COMMAND = MODULE.branch("command");
    public static final PermissionTree BYPASS  = MODULE.branch("bypass");

    public static final Permission COMMAND_WORLDS_ROOT = COMMAND.permission("root");
    public static final Permission COMMAND_WORLDS_CREATE = COMMAND.permission("createworld");
    public static final Permission COMMAND_WORLDS_DELETE = COMMAND.permission("deleteworld");
    public static final Permission COMMAND_WORLDS_LOAD   = COMMAND.permission("loadworld");
    public static final Permission COMMAND_WORLDS_UNLOAD = COMMAND.permission("unloadworld");
    public static final Permission COMMAND_WORLDS_EDITOR = COMMAND.permission("editor");

    public static final Permission BYPASS_COMMANDS = BYPASS.permission("commands");
    public static final Permission BYPASS_FLY      = BYPASS.permission("fly");
}
