package su.nightexpress.sunlight.module.backlocation.config;

import org.bukkit.permissions.Permission;
import su.nightexpress.sunlight.config.PermissionTree;
import su.nightexpress.sunlight.config.Perms;

public class BackLocationPerms {

    public static final PermissionTree ROOT    = Perms.detached("backlocation");
    public static final PermissionTree COMMAND = ROOT.branch("command");
    public static final PermissionTree BYPASS  = ROOT.branch("bypass");

    public static final Permission COMMAND_BACK             = COMMAND.permission("back");
    public static final Permission COMMAND_BACK_OTHERS      = COMMAND.permission("back.others");
    public static final Permission COMMAND_DEATHBACK        = COMMAND.permission("deathback");
    public static final Permission COMMAND_DEATHBACK_OTHERS = COMMAND.permission("deathback.others");

    public static final Permission BYPASS_PREVIOUS_WORLDS = BYPASS.permission("previous.worlds");
    public static final Permission BYPASS_PREVIOUS_CAUSES = BYPASS.permission("previous.causes");
    public static final Permission BYPASS_DEATH_WORLDS    = BYPASS.permission("death.worlds");
}
