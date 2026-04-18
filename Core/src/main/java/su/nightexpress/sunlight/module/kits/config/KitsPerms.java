package su.nightexpress.sunlight.module.kits.config;

import org.bukkit.permissions.Permission;
import su.nightexpress.sunlight.config.PermissionTree;
import su.nightexpress.sunlight.config.Perms;

public class KitsPerms {

    private KitsPerms() {
    }

    public static final PermissionTree ROOT    = Perms.detached("kits");
    public static final PermissionTree COMMAND = ROOT.branch("command");
    public static final PermissionTree BYPASS  = ROOT.branch("bypass");
    public static final PermissionTree KIT     = ROOT.branch("kit");

    public static final Permission COMMAND_KITS_ROOT          = COMMAND.permission("kits.root");
    public static final Permission COMMAND_EDIT_KIT           = COMMAND.permission("kits.editor");
    public static final Permission COMMAND_PREVIEW_KIT        = COMMAND.permission("kits.preview");
    public static final Permission COMMAND_PREVIEW_KIT_OTHERS = COMMAND.permission("kits.preview.others");
    public static final Permission COMMAND_KIT_GET            = COMMAND.permission("kits.get");
    public static final Permission COMMAND_KIT_GIVE           = COMMAND.permission("kits.give");
    public static final Permission COMMAND_KIT_LIST           = COMMAND.permission("kits.list");
    public static final Permission COMMAND_KIT_LIST_OTHERS    = COMMAND.permission("kits.list.others");
    public static final Permission COMMAND_RESET_KIT_COOLDOWN = COMMAND.permission("kits.resetcooldown");
    public static final Permission COMMAND_SET_KIT_COOLDOWN   = COMMAND.permission("kits.setcooldown");

    public static final Permission BYPASS_COST     = BYPASS.permission("cost.money");
    public static final Permission BYPASS_COOLDOWN = BYPASS.permission("cooldown");
}
