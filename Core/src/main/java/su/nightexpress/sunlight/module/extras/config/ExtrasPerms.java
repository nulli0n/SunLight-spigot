package su.nightexpress.sunlight.module.extras.config;

import org.bukkit.permissions.Permission;
import su.nightexpress.sunlight.config.PermissionTree;
import su.nightexpress.sunlight.config.Perms;

public class ExtrasPerms {

    public static final PermissionTree MODULE  = Perms.detached("extras");
    public static final PermissionTree COMMAND = MODULE.branch("command");

    public static final Permission COMMAND_CHAIRS            = COMMAND.permission("chairs");
    public static final Permission COMMAND_CHAIRS_OTHERS     = COMMAND.permission("chairs.others");
    public static final Permission COMMAND_SIT               = COMMAND.permission("sit");
    public static final Permission COMMAND_SIT_OTHERS        = COMMAND.permission("sit.others");
    public static final Permission COMMAND_CHEST_SORT        = COMMAND.permission("chestsort");
    public static final Permission COMMAND_CHEST_SORT_OTHERS = COMMAND.permission("chestsort.others");

    public static final Permission SIGNS_COLOR  = MODULE.permission("signs.color");
    public static final Permission ANVILS_COLOR = MODULE.permission("anvils.color");
}
