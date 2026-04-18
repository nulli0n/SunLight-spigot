package su.nightexpress.sunlight.module.essential;

import org.bukkit.permissions.Permission;
import su.nightexpress.sunlight.config.PermissionTree;
import su.nightexpress.sunlight.config.Perms;

public class EssentialPerms {

    public static final PermissionTree MODULE  = Perms.detached("essential");
    public static final PermissionTree COMMAND = MODULE.branch("command");
    public static final PermissionTree BYPASS  = MODULE.branch("bypass");

    public static final Permission COMMAND_INVULNERABILITY        = COMMAND.permission("invulnerability");
    public static final Permission COMMAND_INVULNERABILITY_OTHERS = COMMAND.permission("invulnerability.others");

    public static final Permission BYPASS_INVULNERABILITY_WORLD  = BYPASS.permission("invulnerability.world");
    public static final Permission BYPASS_INVULNERABILITY_DAMAGE = BYPASS.permission("invulnerability.damage");
}
