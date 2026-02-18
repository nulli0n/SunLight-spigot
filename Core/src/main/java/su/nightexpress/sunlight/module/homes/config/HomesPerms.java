package su.nightexpress.sunlight.module.homes.config;

import org.bukkit.permissions.Permission;
import su.nightexpress.sunlight.config.PermissionTree;
import su.nightexpress.sunlight.config.Perms;

public class HomesPerms {

    public static final PermissionTree ROOT    = Perms.detached("homes");
    public static final PermissionTree COMMAND = ROOT.branch("command");
    public static final PermissionTree BYPASS  = ROOT.branch("bypass");

    public static final Permission COMMAND_HOMES_ROOT          = COMMAND.permission("homes.root");
    public static final Permission COMMAND_HOMES_DELETE        = COMMAND.permission("homes.delete");
    public static final Permission COMMAND_HOMES_DELETE_OTHERS = COMMAND.permission("homes.delete.others");
    public static final Permission COMMAND_HOMES_SET           = COMMAND.permission("homes.set");
    public static final Permission COMMAND_HOMES_SET_OTHERS    = COMMAND.permission("homes.set.others");
    public static final Permission COMMAND_HOMES_TELEPORT      = COMMAND.permission("homes.teleport");
    public static final Permission COMMAND_HOMES_LIST          = COMMAND.permission("homes.list");
    public static final Permission COMMAND_HOMES_LIST_OTHERS   = COMMAND.permission("homes.list.others");
    public static final Permission COMMAND_HOMES_INVITE        = COMMAND.permission("homes.invite");
    public static final Permission COMMAND_HOMES_VISIT         = COMMAND.permission("homes.visit");
    public static final Permission COMMAND_HOMES_VISIT_ALL     = COMMAND.permission("homes.visit.all");

    public static final Permission COMMAND_HOMES_ADMIN_ROOT    = COMMAND.permission("homes.admin");

    public static final Permission BYPASS_UNSAFE_LOCATION     = BYPASS.permission("unsafe");
    public static final Permission BYPASS_CREATION_WORLDS     = BYPASS.permission("creation.worlds");
    public static final Permission BYPASS_CREATION_PROTECTION = BYPASS.permission("creation.protection");
}
