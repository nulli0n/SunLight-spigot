package su.nightexpress.sunlight.module.warps.config;

import su.nightexpress.nightcore.util.wrapper.UniPermission;
import su.nightexpress.sunlight.config.Perms;
import su.nightexpress.sunlight.module.warps.util.Placeholders;

public class WarpsPerms {

    public static final String PREFIX         = Perms.PREFIX + "warps.";
    public static final String PREFIX_COMMAND = PREFIX + "command.";
    public static final String PREFIX_BYPASS  = PREFIX + "bypass.";
    public static final String PREFIX_EDITOR  = PREFIX + "editor.";
    public static final String PREFIX_WARP    = PREFIX + "warp.";
    public static final String PREFIX_AMOUNT  = PREFIX + "amount.";

    public static final UniPermission MODULE  = new UniPermission(PREFIX + Placeholders.WILDCARD);
    public static final UniPermission COMMAND = new UniPermission(PREFIX_COMMAND + Placeholders.WILDCARD);
    public static final UniPermission BYPASS  = new UniPermission(PREFIX_BYPASS + Placeholders.WILDCARD);
    public static final UniPermission EDITOR  = new UniPermission(PREFIX_EDITOR + Placeholders.WILDCARD);
    public static final UniPermission WARP    = new UniPermission(PREFIX_WARP + Placeholders.WILDCARD);

    public static final UniPermission COMMAND_WARPS_CREATE          = new UniPermission(PREFIX_COMMAND + "warps.create");
    public static final UniPermission COMMAND_WARPS_CREATE_OTHERS   = new UniPermission(PREFIX_COMMAND + "warps.create.others");
    public static final UniPermission COMMAND_WARPS_DELETE          = new UniPermission(PREFIX_COMMAND + "warps.delete");
    public static final UniPermission COMMAND_WARPS_DELETE_OTHERS   = new UniPermission(PREFIX_COMMAND + "warps.delete.others");
    public static final UniPermission COMMAND_WARPS_TELEPORT        = new UniPermission(PREFIX_COMMAND + "warps.teleport");
    public static final UniPermission COMMAND_WARPS_TELEPORT_OTHERS = new UniPermission(PREFIX_COMMAND + "warps.teleport.others");
    public static final UniPermission COMMAND_WARPS_LIST            = new UniPermission(PREFIX_COMMAND + "warps.list");
    public static final UniPermission COMMAND_WARPS_LIST_OTHERS     = new UniPermission(PREFIX_COMMAND + "warps.list.others");
    public static final UniPermission COMMAND_WARPS_RESET_COOLDOWN  = new UniPermission(PREFIX_COMMAND + "warps.resetcooldown");
    public static final UniPermission COMMAND_WARPS_SET_COOLDOWN    = new UniPermission(PREFIX_COMMAND + "warps.setcooldown");

    public static final UniPermission EDITOR_OTHERS           = new UniPermission(PREFIX_EDITOR + "others", "Allows to edit other's warps.");
    public static final UniPermission EDITOR_VISIT_COST       = new UniPermission(PREFIX_EDITOR + "visit.cost", "Allows to set warp's visit cost.");
    public static final UniPermission EDITOR_VISIT_COOLDOWN   = new UniPermission(PREFIX_EDITOR + "visit.cooldown", "Allows to set warp's visit cooldown.");
    public static final UniPermission EDITOR_VISIT_TIMES      = new UniPermission(PREFIX_EDITOR + "visit.times", "Allows to set warp's visit times.");
    public static final UniPermission EDITOR_COMMAND_SHORTCUT = new UniPermission(PREFIX_EDITOR + "command.shortcut", "Allows to set warp's command shortcut.");
    public static final UniPermission EDITOR_TYPE             = new UniPermission(PREFIX_EDITOR + "type", "Allows to change warp's type.");
    public static final UniPermission EDITOR_PERMISSION       = new UniPermission(PREFIX_EDITOR + "permission", "Allows to change warp permission requirement.");

    public static final UniPermission BYPASS_DESCRIPTION_SIZE = new UniPermission(PREFIX_BYPASS + "description.size", "Bypasses the description limit.");
    public static final UniPermission BYPASS_CREATION_SAFE    = new UniPermission(PREFIX_BYPASS + "creation.safe", "Bypasses 'safe' location check for warp creation.");
    public static final UniPermission BYPASS_CREATION_WORLD   = new UniPermission(PREFIX_BYPASS + "creation.world", "Bypasses 'world' check for warp creation.");
    public static final UniPermission BYPASS_VISIT_COST       = new UniPermission(PREFIX_BYPASS + "visit.cost", "Bypasses warp visit costs.");
    public static final UniPermission BYPASS_VISIT_TIME       = new UniPermission(PREFIX_BYPASS + "visit.time", "Bypasses warp visit times.");
    public static final UniPermission BYPASS_VISIT_COOLDOWN   = new UniPermission(PREFIX_BYPASS + "visit.cooldown", "Bypasses warp visit cooldowns.");

    static {
        Perms.PLUGIN.addChildren(MODULE);

        MODULE.addChildren(COMMAND, BYPASS, EDITOR, WARP);

        COMMAND.addChildren(
            COMMAND_WARPS_CREATE, COMMAND_WARPS_CREATE_OTHERS,
            COMMAND_WARPS_DELETE, COMMAND_WARPS_DELETE_OTHERS,
            COMMAND_WARPS_LIST,
            COMMAND_WARPS_TELEPORT, COMMAND_WARPS_TELEPORT_OTHERS,
            COMMAND_WARPS_RESET_COOLDOWN,
            COMMAND_WARPS_SET_COOLDOWN
        );

        EDITOR.addChildren(
            EDITOR_COMMAND_SHORTCUT,
            EDITOR_OTHERS,
            EDITOR_TYPE,
            EDITOR_PERMISSION,
            EDITOR_VISIT_COOLDOWN,
            EDITOR_VISIT_COST,
            EDITOR_VISIT_TIMES
        );

        BYPASS.addChildren(
            BYPASS_DESCRIPTION_SIZE,
            BYPASS_CREATION_SAFE,
            BYPASS_CREATION_WORLD,
            BYPASS_VISIT_COOLDOWN,
            BYPASS_VISIT_COST,
            BYPASS_VISIT_TIME
        );
    }
}
