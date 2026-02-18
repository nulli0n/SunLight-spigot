package su.nightexpress.sunlight.module.playerwarps.core;

import org.bukkit.permissions.Permission;
import su.nightexpress.sunlight.config.PermissionTree;
import su.nightexpress.sunlight.config.Perms;

public class PlayerWarpsPerms {

    public static final PermissionTree ROOT    = Perms.detached("playerwarps");
    public static final PermissionTree COMMAND = ROOT.branch("command");
    public static final PermissionTree BYPASS  = ROOT.branch("bypass");
    public static final PermissionTree OPTION  = ROOT.branch("option");
    public static final PermissionTree WARP    = ROOT.branch("warp");

    public static final Permission COMMAND_WARPS_ROOT        = COMMAND.permission("warps.root");
    public static final Permission COMMAND_WARPS_CREATE      = COMMAND.permission("warps.create");
    public static final Permission COMMAND_WARPS_UPDATE      = COMMAND.permission("warps.update");
    public static final Permission COMMAND_WARPS_DELETE      = COMMAND.permission("warps.delete");
    public static final Permission COMMAND_WARPS_JUMP        = COMMAND.permission("warps.teleport");
    public static final Permission COMMAND_WARPS_JUMP_OTHERS = COMMAND.permission("warps.teleport.others");
    public static final Permission COMMAND_WARPS_LIST        = COMMAND.permission("warps.list");
    public static final Permission COMMAND_WARPS_LIST_OTHERS = COMMAND.permission("warps.list.others");

    public static final Permission OPTION_PRICE = OPTION.permission("price");

    public static final Permission BYPASS_CREATION_WORLD = BYPASS.permission("creation.world");
    public static final Permission BYPASS_PRICE          = BYPASS.permission("price");
    public static final Permission BYPASS_OWNERSHIP      = BYPASS.permission("ownership");
}
