package su.nightexpress.sunlight.module.texts;

import org.bukkit.permissions.Permission;
import su.nightexpress.sunlight.config.PermissionTree;
import su.nightexpress.sunlight.config.Perms;

public class TextsPerms {

    public static final PermissionTree MODULE  = Perms.detached("customtext");
    public static final PermissionTree COMMAND = MODULE.branch("command");
    public static final PermissionTree TEXT    = MODULE.branch("text");

    public static final Permission COMMAND_TEXT = COMMAND.permission("text");
}
