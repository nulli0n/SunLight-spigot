package su.nightexpress.sunlight.module.kits.config;

import su.nightexpress.nightcore.util.wrapper.UniPermission;
import su.nightexpress.sunlight.config.Perms;
import su.nightexpress.sunlight.module.ModuleId;
import su.nightexpress.sunlight.module.kits.util.Placeholders;

public class KitsPerms {

    public static final String PREFIX         = Perms.PREFIX + ModuleId.KITS + ".";
    public static final String PREFIX_COMMAND = PREFIX + "command.";
    public static final String PREFIX_BYPASS  = PREFIX + "bypass.";
    public static final String PREFIX_KIT     = PREFIX + "kit.";

    public static final UniPermission MODULE  = new UniPermission(PREFIX + Placeholders.WILDCARD);
    public static final UniPermission COMMAND = new UniPermission(PREFIX_COMMAND + Placeholders.WILDCARD);
    public static final UniPermission BYPASS  = new UniPermission(PREFIX_BYPASS + Placeholders.WILDCARD);
    public static final UniPermission KIT     = new UniPermission(PREFIX_KIT + Placeholders.WILDCARD);

    public static final UniPermission COMMAND_EDIT_KIT           = new UniPermission(PREFIX_COMMAND + "kits.editor");
    public static final UniPermission COMMAND_PREVIEW_KIT        = new UniPermission(PREFIX_COMMAND + "kits.preview");
    public static final UniPermission COMMAND_PREVIEW_KIT_OTHERS = new UniPermission(PREFIX_COMMAND + "kits.preview.others");
    public static final UniPermission COMMAND_KIT                = new UniPermission(PREFIX_COMMAND + "kits.get");
    public static final UniPermission COMMAND_KIT_OTHERS         = new UniPermission(PREFIX_COMMAND + "kits.give");
    public static final UniPermission COMMAND_KIT_LIST           = new UniPermission(PREFIX_COMMAND + "kits.list");
    public static final UniPermission COMMAND_KIT_LIST_OTHERS    = new UniPermission(PREFIX_COMMAND + "kits.list.others");
    public static final UniPermission COMMAND_RESET_KIT_COOLDOWN = new UniPermission(PREFIX_COMMAND + "kits.resetcooldown");
    public static final UniPermission COMMAND_SET_KIT_COOLDOWN   = new UniPermission(PREFIX_COMMAND + "kits.setcooldown");

    public static final UniPermission BYPASS_COST     = new UniPermission(PREFIX_BYPASS + "cost.money");
    public static final UniPermission BYPASS_COOLDOWN = new UniPermission(PREFIX_BYPASS + "cooldown");

    static {
        Perms.PLUGIN.addChildren(MODULE);

        MODULE.addChildren(COMMAND, BYPASS, KIT);

        COMMAND.addChildren(
            COMMAND_EDIT_KIT,
            COMMAND_PREVIEW_KIT, COMMAND_PREVIEW_KIT_OTHERS,
            COMMAND_KIT,
            COMMAND_KIT_OTHERS,
            COMMAND_KIT_LIST, COMMAND_KIT_LIST_OTHERS,
            COMMAND_RESET_KIT_COOLDOWN,
            COMMAND_SET_KIT_COOLDOWN
        );

        BYPASS.addChildren(
            BYPASS_COOLDOWN,
            BYPASS_COST
        );
    }
}
