package su.nightexpress.sunlight.module.afk.core;

import org.bukkit.permissions.Permission;
import su.nightexpress.sunlight.config.PermissionTree;
import su.nightexpress.sunlight.config.Perms;

public class AfkPerms {

    public static final PermissionTree ROOT    = Perms.detached("afk");
    public static final PermissionTree COMMAND = ROOT.branch("command");

    public static final Permission COMMAND_AFK        = COMMAND.permission("afk");
    public static final Permission COMMAND_AFK_OTHERS = COMMAND.permission("afk.others");
}
